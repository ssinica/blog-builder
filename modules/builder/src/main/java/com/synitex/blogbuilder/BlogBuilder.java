package com.synitex.blogbuilder;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synitex.blogbuilder.asciidoc.IAsciidocService;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.io.IIndexWriter;
import com.synitex.blogbuilder.io.IPostWriter;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BlogBuilder {

    private static final Logger log = LoggerFactory.getLogger(BlogBuilder.class);

    private Injector injector;

    public static void main(String[] args) {
        new BlogBuilder().start(args[0]);
    }

    public BlogBuilder() {

    }

    public void start(String bbPropsPath) {
        injector = Guice.createInjector(new BlogModule(bbPropsPath));

        IBlogProperties props = injector.getInstance(IBlogProperties.class);
        IAsciidocService ascService = injector.getInstance(IAsciidocService.class);
        IPostWriter postWriter = injector.getInstance(IPostWriter.class);
        IIndexWriter indexWriter = injector.getInstance(IIndexWriter.class);

        // prepare output directory
        Path outPath = Paths.get(props.getOutPath());
        prepareOutDirectory(outPath);

        // find all posts
        List<PostDto> posts = ascService.listPosts();

        // write posts to html files
        postWriter.write(posts);

        // write index.html
        indexWriter.write(posts);

        // copy resources to output directory
        copyStaticFiles(props);
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
                throw new RuntimeException("Failed to clean up output directory", e);
            }
        } else {
            try {Files.createDirectories(outPath);} catch (IOException e) {
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
        Path outPath = Paths.get(props.getOutPath());
        Path resourcesPath = Paths.get(props.getStaticResourcesPath());

        try {
            FileUtils.copyDirectory(resourcesPath.toFile(), outPath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy resources to out directory", e);
        }
    }


}
