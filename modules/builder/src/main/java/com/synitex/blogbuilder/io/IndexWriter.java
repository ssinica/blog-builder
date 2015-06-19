package com.synitex.blogbuilder.io;

import com.google.inject.Singleton;
import com.google.template.soy.data.SoyMapData;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.TemplateId;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Singleton
public class IndexWriter extends AbstractPageWriter implements IIndexWriter {

    @Inject
    public IndexWriter(IBlogProperties props,
                       ITemplatesProvider templatesProvider,
                       IDto2SoyMapper soyMapper) {
        super(props, templatesProvider, soyMapper);
    }

    @Override
    public void write(List<PostDto> posts, List<TagDto> tags) {
        SoyMapData data = new SoyMapData();
        Path path = Paths.get(props.getOutPath(),  "index.html");
        write(posts, tags, TemplateId.INDEX_HTML, path, data);
    }

}
