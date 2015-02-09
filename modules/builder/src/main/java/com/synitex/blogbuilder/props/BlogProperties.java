package com.synitex.blogbuilder.props;

import com.google.common.base.Strings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BlogProperties implements IBlogProperties {

    private String postsPath;
    private String outPath;
    private BlogAuthorProperties authorProperties;
    private String blogRootUrl;
    private String gaTrackingId;
    private String gaDomainId;
    private boolean devMode = false;
    private String templatesPath;
    private String webPath;

    public BlogProperties(String bbPropsFilePath) {
        loadProperties(bbPropsFilePath);
    }

    protected void loadProperties(String bbPropsFilePath) {
        Properties props = new Properties();
        try(FileInputStream fis = new FileInputStream(new File(bbPropsFilePath))) {
            props.load(fis);

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

            devMode = Boolean.parseBoolean(props.getProperty("blog.dev.mode", "false"));
            templatesPath = props.getProperty("blog.dev.templates.path", "");
            webPath = props.getProperty("blog.dev.web.path", "");

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

    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public String getTemplatesPath() {
        return templatesPath;
    }

    @Override
    public String getWebPath() {
        return webPath;
    }

}
