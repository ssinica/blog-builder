package com.synitex.blogbuilder.io;

import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.dto.PostDto;

import java.util.List;

public interface IPostWriter {

    void write(PostDto post, SoyTofu tofu);

    void write(List<PostDto> posts, SoyTofu tofu);

}
