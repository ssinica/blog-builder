package com.synitex.blogbuilder.dto;

import java.util.List;

public class PostDto {

    private String title;
    private String content;
    private String permlink;
    private String date;
    private List<TagDto> tags;
    private String timeSincePrevPost;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPermlink() {
        return permlink;
    }

    public void setPermlink(String permlink) {
        this.permlink = permlink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public String getTimeSincePrevPost() {
        return timeSincePrevPost;
    }

    public void setTimeSincePrevPost(String timeSincePrevPost) {
        this.timeSincePrevPost = timeSincePrevPost;
    }
}
