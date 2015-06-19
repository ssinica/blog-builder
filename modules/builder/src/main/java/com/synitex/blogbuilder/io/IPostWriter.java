package com.synitex.blogbuilder.io;

import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;

import java.util.List;

public interface IPostWriter {

    void write(PostDto post, List<TagDto> tags);

    void write(List<PostDto> posts, List<TagDto> tags);

}
