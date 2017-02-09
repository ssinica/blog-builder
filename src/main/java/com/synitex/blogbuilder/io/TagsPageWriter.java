package com.synitex.blogbuilder.io;

import com.google.template.soy.data.SoyMapData;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.TemplateId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class TagsPageWriter extends AbstractPageWriter implements ITagsPageWriter {

    @Autowired
    public TagsPageWriter(IBlogProperties props,
                       ITemplatesProvider templatesProvider,
                       IDto2SoyMapper soyMapper) {
        super(props, templatesProvider, soyMapper);
    }

    @Override
    public void write(TagDto tag, List<PostDto> posts, List<TagDto> tags) {
        SoyMapData data = new SoyMapData();
        Path path = Paths.get(props.getOutPath(), tag.getFile());
        write(posts, tags, TemplateId.POST_TAGS_HTML, path, data);
    }

}
