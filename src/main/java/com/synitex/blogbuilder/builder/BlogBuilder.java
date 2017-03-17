package com.synitex.blogbuilder.builder;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.asciidoc.IAsciidocService;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.io.IIndexWriter;
import com.synitex.blogbuilder.io.IPostWriter;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.ITofuProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class BlogBuilder implements IBlogBuilder {

    private static final Logger log = LoggerFactory.getLogger(BlogBuilder.class);

    private final IBlogProperties props;
    private final IAsciidocService ascService;
    private final IPostWriter postWriter;
    private final IIndexWriter indexWriter;
    private final ITofuProvider tofuProvider;

    @Autowired
    public BlogBuilder(IBlogProperties props,
                       IAsciidocService ascService,
                       IPostWriter postWriter,
                       IIndexWriter indexWriter,
                       ITofuProvider tofuProvider) {
        this.props = props;
        this.ascService = ascService;
        this.postWriter = postWriter;
        this.indexWriter = indexWriter;
        this.tofuProvider = tofuProvider;
    }

    @PostConstruct
    public void init() {
        Path outPath = Paths.get(props.getOutPath());
        log.info("Prepare out dir: {}", outPath);
        prepareOutDirectory(outPath);

        log.info("Copy assets...");
        copyAssetsFiles(props);

        log.info("Copy static files...");
        copySourceDirs(props);
    }

    @Override
    public void build() {
        SoyTofu tofu = tofuProvider.getTofu();
        List<TagDto> tags = Collections.emptyList();
        List<PostDto> posts = ascService.listPosts();
        
        log.info("Converting posts to HTML...");
        postWriter.write(posts, tags, tofu);
        
        log.info("Writing index.html...");
        indexWriter.write(posts, tags, tofu);
    }

    @Override
    public void build(final String postName) {
        List<TagDto> tags = Collections.emptyList();
        List<PostDto> posts = ascService.listPosts();
        SoyTofu tofu = tofuProvider.getTofu();
        
        PostDto post = ascService.getPost(postName);
        if(post != null) {
            log.info("Writing post {} to HTML...", post.getPermlink());
            postWriter.write(post, tags, tofu);
        }
        
        log.info("Writing index.html...");
        indexWriter.write(posts, tags, tofu);

        if(props.getDevProperties().isDevMode()) {
            copyAssetsFiles(props);
        }
    }

    private void prepareOutDirectory(Path outPath) {
        if(outPath.toFile().exists()) {
            try(DirectoryStream<Path> ds = Files.newDirectoryStream(outPath, gitDeleteFilesFilter())) {
                for(Path path : ds) {
                    File file = path.toFile();
                    if(file.isDirectory()) {
                        FileUtils.deleteDirectory(file);
                    } else {
                        Files.delete(path);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to clean up output directory", e);
                throw new RuntimeException("Failed to clean up output directory", e);
            }
        } else {
            try {Files.createDirectories(outPath);} catch (IOException e) {
                log.error("Failed to create output directory", e);
                throw new RuntimeException("Failed to create output directory", e);
            }
        }
    }

    private Filter<Path> gitDeleteFilesFilter() {
        return new Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                String name = entry.toFile().getName();
                return !".git".equals(name) && !"CNAME".equalsIgnoreCase(name);
            }
        };
    }

    private void copyAssetsFiles(IBlogProperties props) {
        try {

            Path assetsPath = Paths.get(props.getOutPath(), "assets");
            if(!assetsPath.toFile().exists()) {
                Files.createDirectory(assetsPath);
            }

            if(props.getDevProperties().isDevMode()) {
                
                Path resourcesPath = Paths.get(props.getDevProperties().getAssetsPath());
                log.info("Copy {} to {}.", resourcesPath, assetsPath);
                FileUtils.copyDirectory(resourcesPath.toFile(), assetsPath.toFile());

            } else {

                String pattern = "classpath:assets/**.*";
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources(pattern);
                if (resources != null && resources.length > 0) {
                    for (Resource r : resources) {
                        String fileName = r.getFilename();
                        try (InputStream from = r.getURL().openStream()) {
                            Path to = Paths.get(assetsPath.toString(), fileName);
                            log.info("Copy {} to {}.", fileName, to);
                            Files.copy(from, to, REPLACE_EXISTING);
                        }
                    }
                }

            }

        } catch (IOException e) {
            log.error("Failed to copy assets to out directory", e);
            throw new RuntimeException("Failed to copy resources to out directory", e);
        }

    }

    private void copySourceDirs(IBlogProperties props) {
        File outPath = Paths.get(props.getOutPath()).toFile();
        Path postsPath = Paths.get(props.getPostsPath());

        Filter<Path> dirsOnlyFilter = new Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return entry.toFile().isDirectory();
            }
        };

        try(DirectoryStream<Path> ds = Files.newDirectoryStream(postsPath, dirsOnlyFilter)) {
            for(Path path : ds) {
                FileUtils.copyDirectoryToDirectory(path.toFile(), outPath);
            }
        } catch (IOException e) {
            log.error("Failed to copy source dir", e);
            throw new RuntimeException("Failed to copy surce dir", e);
        }
    }

    private List<TagDto> collectTags(List<PostDto> posts) {
        List<TagDto> tags = new ArrayList<>();

        HashMultiset<TagDto> mset = HashMultiset.create();
        for(PostDto post : posts) {
            List<TagDto> values = post.getTags();
            if(!Iterables.isEmpty(values)) {
                mset.addAll(values);
            }
        }

        for(TagDto tag : mset.elementSet()) {
            tags.add(new TagDto(tag.getText(), mset.count(tag)));
        }

        Collections.sort(tags, new Comparator<TagDto>() {
            @Override
            public int compare(TagDto o1, TagDto o2) {
                int c1 = o1.getCount();
                int c2 = o2.getCount();
                if (c1 == c2) {
                    return 0;
                } else if (c1 > c2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return tags;
    }
    
}
