package com.synitex.blogbuilder.io;

import com.synitex.blogbuilder.props.IBlogProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class AssetsWriter implements IAssetsWriter {

    private static final Logger log = LoggerFactory.getLogger(AssetsWriter.class);

    private final IBlogProperties props;

    @Autowired
    public AssetsWriter(IBlogProperties blogProperties) {
        this.props = blogProperties;
    }

    @Override
    public void writeAssets() {
        copyJsAndCssFiles();
        copyDirsWithStaticContent();
    }

    private void copyJsAndCssFiles() {
        try {

            Path assetsOutPath = Paths.get(props.getOutPath(), "assets");
            if(!assetsOutPath.toFile().exists()) {
                Files.createDirectory(assetsOutPath);
            }

            if(props.getDevProperties().isDevMode()) {

                Path resourcesPath = Paths.get(props.getDevProperties().getAssetsPath());
                log.info("Copy {} to {}.", resourcesPath, assetsOutPath);
                FileUtils.copyDirectory(resourcesPath.toFile(), assetsOutPath.toFile());

            } else {

                String pattern = "classpath:assets/**.*";
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                Resource[] resources = resolver.getResources(pattern);
                if (resources != null && resources.length > 0) {
                    for (Resource r : resources) {
                        String fileName = r.getFilename();
                        try (InputStream from = r.getURL().openStream()) {
                            Path to = Paths.get(assetsOutPath.toString(), fileName);
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

    private void copyDirsWithStaticContent() {
        File outPath = Paths.get(props.getOutPath()).toFile();
        Path postsPath = Paths.get(props.getPostsPath());

        Filter<Path> dirsOnlyFilter = entry -> entry.toFile().isDirectory();

        try(DirectoryStream<Path> ds = Files.newDirectoryStream(postsPath, dirsOnlyFilter)) {
            for(Path path : ds) {
                log.info("Copy {} to {}...", path, outPath);
                FileUtils.copyDirectoryToDirectory(path.toFile(), outPath);
            }
        } catch (IOException e) {
            log.error("Failed to copy source dir", e);
            throw new RuntimeException("Failed to copy surce dir", e);
        }
    }
    
}
