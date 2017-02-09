package com.synitex.blogbuilder.io;

import com.google.template.soy.data.SoyMapData;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.TemplateId;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public abstract class AbstractPageWriter {

    protected final ITemplatesProvider templatesProvider;
    protected final IBlogProperties props;
    protected final IDto2SoyMapper soyMapper;
    
    public AbstractPageWriter(IBlogProperties props,
                       ITemplatesProvider templatesProvider,
                       IDto2SoyMapper soyMapper) {
        this.props = props;
        this.templatesProvider = templatesProvider;
        this.soyMapper = soyMapper;
    }

    public void write(List<PostDto> posts, List<TagDto> tags, TemplateId templateId, Path path, SoyMapData data) {
        data.putSingle("posts", soyMapper.mapList(posts));
        data.putSingle("tags", soyMapper.mapList(tags));
        String html = templatesProvider.build(templateId, data);
        writeFile(path, html);
    }

    public void writeFile(Path path, String content) {
        try {
            Files.write(path, content.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save file", ex);
        }
    }

}
