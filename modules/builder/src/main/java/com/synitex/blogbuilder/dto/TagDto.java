package com.synitex.blogbuilder.dto;

public class TagDto {

    private String file;
    private String text;
    private int count = 1;

    public TagDto(String text, String tagsFileName, int count) {
        this.text = text;
        this.file = tagsFileName;
        this.count = count;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TagDto) {
            TagDto casted = (TagDto) obj;
            return text.equals(casted.getText());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }
}
