package com.synitex.blogbuilder.asciidoc;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.synitex.blogbuilder.dirwatch.DirWatchConfig;
import com.synitex.blogbuilder.dirwatch.DirWatchListener;
import com.synitex.blogbuilder.dirwatch.IDirWatchService;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Asciidoctor.Factory;
import org.asciidoctor.DocumentHeader;
import org.asciidoctor.Options;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Service
public class AsciidocService implements IAsciidocService, DirWatchListener {

    private static final Logger log = LoggerFactory.getLogger(AsciidocService.class);
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

    private final IBlogProperties props;
    private final Asciidoctor ascDoc;
    private final Map<String, PostDto> postsByName = Maps.newConcurrentMap();
    private final Set<Path> pathsToUpdate = Sets.newConcurrentHashSet();
    
    @Autowired
    public AsciidocService(IBlogProperties props,
                           IDirWatchService dirWatchService) {
        this.props = props;
        ascDoc = Factory.create();

        parseAllPosts();

        String postsPath = props.getPostsPath();
        DirWatchConfig dirWatchConfig = new DirWatchConfig(postsPath, 3000, postsPredicate());
        dirWatchService.registerListener(this, dirWatchConfig);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::updatePostsIfRequired, 20, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onPathUpdated(Path path) {
        log.debug("Post updated: {}!", path);
        pathsToUpdate.add(path);
    }

    @Override
    public PostDto getPost(String name) {
        return postsByName.get(name);
    }

    @Override
    public List<PostDto> listPosts() {
        ArrayList<PostDto> posts = Lists.newArrayList(postsByName.values());
        posts.sort(getPostsComparator());
        return posts;
    }

    private void updatePostsIfRequired() {
        if(pathsToUpdate.size() > 0) {
            Set<Path> paths = new HashSet<>(pathsToUpdate);
            pathsToUpdate.clear();
            paths.forEach(this::parsePost);
        }
    }

    private void parseAllPosts() {
        Path postsPath = Paths.get(props.getPostsPath());
        try {
            Files.list(postsPath)
                    .filter(postsPredicate())
                    .forEach(this::parsePost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void parsePost(Path postPath) {
        log.info("Parsing post {}...", postPath);
        Options options = new Options();
        String content = ascDoc.renderFile(postPath.toFile(), options);
        DocumentHeader header = ascDoc.readDocumentHeader(postPath.toFile());
        PostDto post = createPostDto(header, content);
        postsByName.put(post.getPermlink(), post);
    }

    private PostDto createPostDto(DocumentHeader header, String content) {
        PostDto dto = new PostDto();
        dto.setContent(content);
        dto.setPermlink((String) header.getAttributes().get(AsciidocConstants.PERMALINK));
        dto.setTitle(header.getDocumentTitle());
        dto.setDate((String) header.getAttributes().get(AsciidocConstants.DATE));

        Assert.notNull(dto.getDate(), AsciidocConstants.DATE + " is required param in post");
        Assert.notNull(dto.getPermlink(), AsciidocConstants.PERMALINK + " is required param in post");

        String tagsSource = (String) header.getAttributes().get(AsciidocConstants.TAGS);
        if(!Strings.isNullOrEmpty(tagsSource)) {
            List<String> tagSources = Lists.newArrayList(
                    Splitter.on(",")
                            .omitEmptyStrings()
                            .trimResults()
                            .split(tagsSource)
            );
            List<TagDto> tags = Lists.newArrayList(
                    Lists.transform(tagSources, input -> new TagDto(input, -1)));
            dto.setTags(tags);
        }

        return dto;
    }

    private Predicate<Path> postsPredicate() {
        return path -> {
            File f = path.toFile();
            return f.isFile() && f.getName().endsWith(".asc");
        };
    }

    private Comparator<? super PostDto> getPostsComparator() {
        return (Comparator<PostDto>) (o1, o2) -> {
            DateTime dt1 = Strings.isNullOrEmpty(o1.getDate()) ? null : dtf.parseDateTime(o1.getDate());
            DateTime dt2 = Strings.isNullOrEmpty(o2.getDate()) ? null : dtf.parseDateTime(o2.getDate());
            return ComparisonChain.start().compare(dt2, dt1).result();
        };
    }

}
