package com.synitex.blogbuilder.io;

import com.google.inject.Singleton;
import com.google.template.soy.data.SoyMapData;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.TemplateId;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Singleton
public class IndexWriter extends AbstractWriter implements IIndexWriter {

    private final ITemplatesProvider templatesProvider;
    private final IBlogProperties props;
    private final IDto2SoyMapper soyMapper;

    @Inject
    public IndexWriter(IBlogProperties props,
                       ITemplatesProvider templatesProvider,
                       IDto2SoyMapper soyMapper) {
        this.props = props;
        this.templatesProvider = templatesProvider;
        this.soyMapper = soyMapper;
    }

    @Override
    public void write(List<PostDto> posts) {
        SoyMapData data = new SoyMapData();
        data.putSingle("posts", soyMapper.mapList(posts));

        String html = templatesProvider.build(TemplateId.INDEX_HTML, data);

        String outPath = props.getOutPath();
        Path path = Paths.get(outPath,  "index.html");
        writeFile(path, html);
    }

}
