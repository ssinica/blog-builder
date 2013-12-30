package com.synitex.blogbuilder.props;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class BlogProperties implements IBlogProperties {

    private String postsPath;
    private String outPath;
    private String templatesPath;
    private String resourcesPath;
    private BlogAuthorProperties authorProperties;
    private String blogRootUrl;
    private String gaTrackingId;
    private String gaDomainId;

    public BlogProperties(String bbPropsFilePath) {
        loadProperties(bbPropsFilePath);
    }

    protected void loadProperties(String bbPropsFilePath) {
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(new File(bbPropsFilePath))) {
            props.load(fis);

            String rootPath = props.getProperty("blog-builder.root.path");
            templatesPath = Paths.get(rootPath, "modules", "builder", "src", "main", "resources", "templates").toString();
            resourcesPath = Paths.get(rootPath, "web").toString();

            postsPath = props.getProperty("blog.posts.path");
            outPath = props.getProperty("blog.out.path");

            blogRootUrl = props.getProperty("blog.root.url");

            authorProperties = new BlogAuthorProperties();
            authorProperties.setImage(props.getProperty("author.image"));
            authorProperties.setName(props.getProperty("author.name"));
            authorProperties.setTwitter(props.getProperty("author.twitter", null));
            authorProperties.setGoogle(props.getProperty("author.google", null));
            authorProperties.setGithub(props.getProperty("author.github", null));

            gaTrackingId = props.getProperty("blog.ga.tracking.id", null);
            gaDomainId = props.getProperty("blog.ga.domain", null);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read blog-builder properties file from " + bbPropsFilePath);
        }
    }

    @Override
    public String getPostsPath() {
        return postsPath;
    }

    @Override
    public String getOutPath() {
        return outPath;
    }

    @Override
    public String getTemplatesPath() {
        return templatesPath;
    }

    @Override
    public String getStaticResourcesPath() {
        return resourcesPath;
    }

    @Override
    public BlogAuthorProperties getAuthorProperties() {
        return authorProperties;
    }

    @Override
    public String getBlogRootUrl() {
        return blogRootUrl;
    }

    @Override
    public String getGaTrackingId() {
        return Strings.emptyToNull(gaTrackingId);
    }

    @Override
    public String getGaDomainName() {
        return Strings.emptyToNull(gaDomainId);
    }

}
