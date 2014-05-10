package com.synitex.blogbuilder.io;

import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;

import java.util.List;

public interface ITagsPageWriter {

    void write(TagDto tag, List<PostDto> posts);

}
