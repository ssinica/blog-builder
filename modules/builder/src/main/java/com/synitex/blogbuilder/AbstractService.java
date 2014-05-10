package com.synitex.blogbuilder;

public abstract class AbstractService {

    protected String tagToFileName(String tag) {
        String s = tag.replace(" ", "_");
        return "tag_" + s + ".html";
    }

}
