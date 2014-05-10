package com.synitex.blogbuilder.dto;

import com.google.common.base.Strings;

import java.util.List;

public class PostDto {

    private String title;
    private String content;
    private String permlink;
    private String date;
    private String intro;
    private List<TagDto> tags;

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

    public void setIntro(String intro) {
        this.intro = Strings.emptyToNull(intro);
    }

    public String getIntro() {
        return intro;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }
}
