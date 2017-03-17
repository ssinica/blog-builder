package com.synitex.blogbuilder.dto;

public class TagDto {
    
    private String text;
    private int count = 1;

    public TagDto(String text, int count) {
        this.text = text;
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
