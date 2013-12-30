package com.synitex.blogbuilder.props;

public interface IBlogProperties {

    String getPostsPath();

    String getOutPath();

    String getTemplatesPath();

    String getStaticResourcesPath();

    BlogAuthorProperties getAuthorProperties();

    String getBlogRootUrl();

    String getGaTrackingId();

    String getGaDomainName();

}
