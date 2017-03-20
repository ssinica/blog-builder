package com.synitex.blogbuilder.builder;

public interface IBlogBuilder {

    void build(boolean adminMode);

    void build(String postName, boolean adminMode);

}
