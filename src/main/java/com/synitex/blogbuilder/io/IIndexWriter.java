package com.synitex.blogbuilder.io;

import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;

import java.util.List;


public interface IIndexWriter {

    void write(List<PostDto> posts, List<TagDto> tags, SoyTofu tofu);

}
