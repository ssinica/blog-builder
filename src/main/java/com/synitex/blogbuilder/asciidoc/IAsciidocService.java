package com.synitex.blogbuilder.asciidoc;

import com.synitex.blogbuilder.dto.PostDto;

import java.util.List;

public interface IAsciidocService {

    List<PostDto> listPosts();

    PostDto getPost(String name);

}
