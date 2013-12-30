package com.synitex.blogbuilder.io;

import com.synitex.blogbuilder.dto.PostDto;

import java.util.List;

public interface IPostWriter {

    void write(PostDto post);

    void write(List<PostDto> posts);

}
