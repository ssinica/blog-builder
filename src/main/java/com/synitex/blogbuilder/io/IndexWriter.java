package com.synitex.blogbuilder.io;

import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.ITofuProvider;
import com.synitex.blogbuilder.soy.TemplateId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class IndexWriter extends AbstractPageWriter implements IIndexWriter {

    @Autowired
    public IndexWriter(IBlogProperties props,
                       ITemplatesProvider templatesProvider,
                       IDto2SoyMapper soyMapper,
                       ITofuProvider tofuProvider) {
        super(props, templatesProvider, soyMapper);
    }

    @Override
    public void write(List<PostDto> posts, List<TagDto> tags, SoyTofu tofu) {
        SoyMapData data = new SoyMapData();
        Path path = Paths.get(props.getOutPath(),  "index.html");
        write(posts, tags, TemplateId.INDEX_HTML, path, data, tofu);
    }

}
