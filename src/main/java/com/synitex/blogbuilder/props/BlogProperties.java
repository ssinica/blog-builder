package com.synitex.blogbuilder.props;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BlogProperties implements IBlogProperties {

    private static final Logger log = LoggerFactory.getLogger(BlogProperties.class);

    private final BlogAuthorProperties blogAuthorProperties;

    @Value("${blog.posts.path}")
    private String postsPath;

    @Value("${blog.out.path}")
    private String outPath;

    @Value("${blog.root.url}")
    private String blogRootUrl;

    @Value("${blog.ga.tracking.id}")
    private String gaTrackingId;

    @Value("${blog.ga.domain}")
    private String gaDomainId;

    @Autowired
    public BlogProperties(BlogAuthorProperties blogAuthorProperties) {
        this.blogAuthorProperties = blogAuthorProperties;
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Posts path: " + postsPath);
        log.info("Out path: " + outPath);
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
        return blogAuthorProperties;
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
