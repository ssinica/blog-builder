package com.synitex.blogbuilder.props;

import com.google.common.base.Strings;

public class BlogAuthorProperties {

    private String image;
    private String name;
    private String twitter;
    private String github;
    private String google;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Strings.emptyToNull(name);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = Strings.emptyToNull(image);
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = Strings.emptyToNull(twitter);
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = Strings.emptyToNull(github);
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = Strings.emptyToNull(google);
    }

}
