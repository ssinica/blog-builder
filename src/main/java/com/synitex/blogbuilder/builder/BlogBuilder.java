package com.synitex.blogbuilder.builder;

import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.asciidoc.IAsciidocService;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.io.IAssetsWriter;
import com.synitex.blogbuilder.io.IIndexWriter;
import com.synitex.blogbuilder.io.IPostWriter;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.ITofuProvider;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class BlogBuilder implements IBlogBuilder {

    private static final Logger log = LoggerFactory.getLogger(BlogBuilder.class);

    private final IBlogProperties props;
    private final IAsciidocService ascService;
    private final IPostWriter postWriter;
    private final IIndexWriter indexWriter;
    private final ITofuProvider tofuProvider;
    private final IAssetsWriter assetsWriter;

    @Autowired
    public BlogBuilder(IBlogProperties props,
                       IAsciidocService ascService,
                       IPostWriter postWriter,
                       IIndexWriter indexWriter,
                       ITofuProvider tofuProvider, IAssetsWriter assetsWriter) {
        this.props = props;
        this.ascService = ascService;
        this.postWriter = postWriter;
        this.indexWriter = indexWriter;
        this.tofuProvider = tofuProvider;
        this.assetsWriter = assetsWriter;

        Path outPath = Paths.get(props.getOutPath());
        log.info("Prepare out dir: {}", outPath);
        prepareOutDirectory(outPath);
    }

    @Override
    public void build(boolean adminMode) {
        SoyTofu tofu = tofuProvider.getTofu();
        List<PostDto> posts = ascService.listPosts();
        
        log.info("Writing posts...");
        postWriter.write(posts, tofu, adminMode);
        
        log.info("Writing index.html...");
        indexWriter.write(posts, tofu, adminMode);

        log.info("Writing assets...");
        assetsWriter.writeAssets();
    }

    @Override
    public void build(final String postName, boolean adminMode) {
        List<PostDto> posts = ascService.listPosts();
        SoyTofu tofu = tofuProvider.getTofu();
        
        PostDto post = ascService.getPost(postName);
        if(post != null) {
            log.info("Writing post {} to HTML...", post.getPermlink());
            postWriter.write(post, tofu, adminMode);
        }
        
        log.info("Writing index.html...");
        indexWriter.write(posts, tofu, adminMode);

        log.info("Writing assets...");
        assetsWriter.writeAssets();
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
        return entry -> {
            String name = entry.toFile().getName();
            return !".git".equals(name) && !"CNAME".equalsIgnoreCase(name);
        };
    }
    
}
