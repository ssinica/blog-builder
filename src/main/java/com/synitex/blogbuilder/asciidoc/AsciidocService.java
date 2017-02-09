package com.synitex.blogbuilder.asciidoc;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
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
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AsciidocService implements IAsciidocService {

    private static final Logger log = LoggerFactory.getLogger(AsciidocService.class);

    private final IBlogProperties props;
    private static final DateTimeFormatter dtf = DateTimeFormat.forPattern("dd-MM-yyyy");

    @Autowired
    public AsciidocService(IBlogProperties props) {
        this.props = props;
    }

    @Override
    public List<PostDto> listPosts() {
        List<PostDto> posts = Lists.newArrayList();

        Path postsPath = Paths.get(props.getPostsPath());

        Asciidoctor ascDoc = Factory.create();
        Options options = new Options();

        try(DirectoryStream<Path> ds = Files.newDirectoryStream(postsPath, postsFilter())) {
            for(Path postPath : ds) {
                log.info("Gen post from source: {}", postPath.getFileName().toString());
                String content = ascDoc.renderFile(postPath.toFile(), options);
                DocumentHeader header = ascDoc.readDocumentHeader(postPath.toFile());
                posts.add(createPostDto(header, content));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load posts from " + postsPath, e);
        }

        Collections.sort(posts, getPostsComparator());
        return posts;
    }

    private Comparator<? super PostDto> getPostsComparator() {
        return new Comparator<PostDto>() {
            @Override
            public int compare(PostDto o1, PostDto o2) {
                DateTime dt1 = Strings.isNullOrEmpty(o1.getDate()) ? null : dtf.parseDateTime(o1.getDate());
                DateTime dt2 = Strings.isNullOrEmpty(o2.getDate()) ? null : dtf.parseDateTime(o2.getDate());
                return ComparisonChain.start().compare(dt2, dt1).result();
            }
        };
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
            List<TagDto> tags = Lists.newArrayList(Lists.transform(tagSources, new Function<String, TagDto>() {
                @Override
                public TagDto apply(String input) {
                    return new TagDto(input, tagToFileName(input), -1);
                }
            }));
            dto.setTags(tags);
        }

        return dto;
    }

    private Filter<Path> postsFilter() {
        return new Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                File f = entry.toFile();
                return f.isFile() && f.getName().endsWith(".asc");
            }
        };
    }

    private String tagToFileName(String tag) {
        String s = tag.replace(" ", "_");
        return "tag_" + s + ".html";
    }

}
