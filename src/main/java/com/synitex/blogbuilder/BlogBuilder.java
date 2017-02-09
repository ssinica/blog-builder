package com.synitex.blogbuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.synitex.blogbuilder.asciidoc.IAsciidocService;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.io.IIndexWriter;
import com.synitex.blogbuilder.io.IPostWriter;
import com.synitex.blogbuilder.io.ITagsPageWriter;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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

@Service
public class BlogBuilder implements IBlogBuilder {

    private static final Logger log = LoggerFactory.getLogger(BlogBuilder.class);
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

    private final IBlogProperties props;
    private final IAsciidocService ascService;
    private final IPostWriter postWriter;
    private final IIndexWriter indexWriter;
    private final ITagsPageWriter tagsWriter;

    @Autowired
    public BlogBuilder(IBlogProperties props,
                       IAsciidocService ascService,
                       IPostWriter postWriter,
                       IIndexWriter indexWriter, ITagsPageWriter tagsWriter) {
        this.props = props;
        this.ascService = ascService;
        this.postWriter = postWriter;
        this.indexWriter = indexWriter;
        this.tagsWriter = tagsWriter;
    }


    @Override
    public void build() {
        
        // prepare output directory
        Path outPath = Paths.get(props.getOutPath());
        prepareOutDirectory(outPath);

        // find all posts
        List<PostDto> posts = ascService.listPosts();
        List<TagDto> tags = collectTags(posts);
        injectAdditionalData(posts);

        // write posts to html files
        postWriter.write(posts, tags);

        // write index.html
        indexWriter.write(posts, tags);

        // write tags htmls
        for(TagDto tag : tags) {
            List<PostDto> filteredPosts = filterPostsByTag(tag, posts);
            if(!Iterables.isEmpty(filteredPosts)) {
                tagsWriter.write(tag, filteredPosts, tags);
            }
        }

        // copy resources to output directory
        copyStaticFiles(props);

        copySourceDirs(props);
    }

    private void injectAdditionalData(List<PostDto> posts) {
        if(CollectionUtils.isEmpty(posts)) {
            return;
        }
        DateTime dtPostPrev = dtf.parseDateTime(posts.get(0).getDate());
        for(int i = 0; i < posts.size(); i++) {
            if(i > 0) {
                DateTime dtPostCur = dtf.parseDateTime(posts.get(i).getDate());
                long days = Duration.millis(dtPostPrev.getMillis() - dtPostCur.getMillis()).getStandardDays();
                posts.get(i - 1).setTimeSincePrevPost(days == 1 ? "1 day" : String.format("%s days", days));
                dtPostPrev = dtPostCur;
            }
        }
    }

    private List<PostDto> filterPostsByTag(final TagDto tag, List<PostDto > posts) {
        return Lists.newArrayList(Iterables.filter(posts, new Predicate<PostDto>() {
            @Override
            public boolean apply(PostDto input) {
                List<TagDto> tags = input.getTags();
                return !Iterables.isEmpty(tags) && tags.contains(tag);
            }
        }));
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

            //if(props.isDevMode()){

            //    Path resourcesPath = Paths.get(props.getWebPath());
            //    FileUtils.copyDirectory(resourcesPath.toFile(), assetsPath.toFile());

            //} else {

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

            //}

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
            tags.add(new TagDto(tag.getText(), tag.getFile(), mset.count(tag)));
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
