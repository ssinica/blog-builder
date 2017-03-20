package com.synitex.blogbuilder.props;

public interface IBlogProperties {

    String getPostsPath();

    String getOutPath();

    BlogAuthorProperties getAuthorProperties();

    String getBlogRootUrl();

    String getGaTrackingId();

    String getGaDomainName();

    IDevProperties getDevProperties();

    IGithubProperties getGithubProperties();

}
