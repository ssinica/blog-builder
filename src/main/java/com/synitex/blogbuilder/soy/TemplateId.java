package com.synitex.blogbuilder.soy;

public enum TemplateId {

    INDEX_HTML("blog.indexHtml"),
    POST("blog.post");

    private String templateId;

    private TemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }

}
