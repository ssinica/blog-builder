package com.synitex.blogbuilder.io;

import com.synitex.blogbuilder.dto.PostDto;

import java.util.List;


public interface IIndexWriter {

    void write(List<PostDto> posts);

}
