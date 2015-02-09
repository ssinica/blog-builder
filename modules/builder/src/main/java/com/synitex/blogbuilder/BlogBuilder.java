package com.synitex.blogbuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synitex.blogbuilder.asciidoc.IAsciidocService;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.io.IIndexWriter;
import com.synitex.blogbuilder.io.IPostWriter;
import com.synitex.blogbuilder.io.ITagsPageWriter;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlogBuilder {

    private static final Logger log = LoggerFactory.getLogger(BlogBuilder.class);

    private Injector injector;

    public static void main(String[] args) {
        new BlogBuilder(args[0]).start();
    }

    public BlogBuilder(String blogPropertiesPath) {
        injector = Guice.createInjector(new BlogModule(blogPropertiesPath));
    }

    public void start() {

        IBlogProperties props = injector.getInstance(IBlogProperties.class);
        IAsciidocService ascService = injector.getInstance(IAsciidocService.class);
        IPostWriter postWriter = injector.getInstance(IPostWriter.class);
        IIndexWriter indexWriter = injector.getInstance(IIndexWriter.class);
        ITagsPageWriter tagsWriter = injector.getInstance(ITagsPageWriter.class);

        // prepare output directory
        Path outPath = Paths.get(props.getOutPath());
        prepareOutDirectory(outPath);

        // find all posts
        List<PostDto> posts = ascService.listPosts();

        // write posts to html files
        postWriter.write(posts);

        // write index.html
        indexWriter.write(posts);

        // write tags htmls
        Set<TagDto> tags = collectTags(posts);
        for(TagDto tag : tags) {
            List<PostDto> filteredPosts = filterPostsByTag(tag, posts);
            if(!Iterables.isEmpty(filteredPosts)) {
                tagsWriter.write(tag, filteredPosts);
            }
        }

        // copy resources to output directory
        copyStaticFiles(props);

        copySourceDirs(props);
    }

    private List<PostDto> filterPostsByTag(final TagDto tag, List<PostDto> posts) {
        return Lists.newArrayList(Iterables.filter(posts, new Predicate<PostDto>() {
            @Override
            public boolean apply(PostDto input) {
                List<TagDto> tags = input.getTags();
                return !Iterables.isEmpty(tags) && tags.contains(tag);
            }
        }));
    }

    private Set<TagDto> collectTags(List<PostDto> posts) {
        Set<TagDto> tags = new HashSet<>();
        for(PostDto post : posts) {
            List<TagDto> values = post.getTags();
            if(!Iterables.isEmpty(values)) {
                tags.addAll(values);
            }
        }
        return tags;
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

    private void copyStaticFiles(IBlogProperties props) {
        try {

            Path assetsPath = Paths.get(props.getOutPath(), "assets");
            if(!assetsPath.toFile().exists()) {
                Files.createDirectory(assetsPath);
            }

            if(props.isDevMode()){

                Path resourcesPath = Paths.get(props.getWebPath());
                FileUtils.copyDirectory(resourcesPath.toFile(), assetsPath.toFile());

            } else {

                String pattern = "classpath:assets/**.*";
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources(pattern);
                if (resources != null && resources.length > 0) {
                    for (Resource r : resources) {
                        String fileName = r.getFilename();
                        try (InputStream in = r.getURL().openStream()) {
                            Files.copy(in, Paths.get(assetsPath.toString(), fileName));
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


}
