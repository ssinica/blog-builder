package com.synitex.blogbuilder.io;

import com.google.inject.Singleton;
import com.google.template.soy.data.SoyMapData;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.TemplateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Singleton
public class PostWriter extends AbstractWriter implements IPostWriter {

    private static final Logger log = LoggerFactory.getLogger(PostWriter.class);

    private final IBlogProperties props;
    private final ITemplatesProvider templatesProvider;
    private final IDto2SoyMapper soyMapper;

    @Inject
    public PostWriter(IBlogProperties props,
                      ITemplatesProvider templatesProvider,
                      IDto2SoyMapper soyMapper) {
        this.props = props;
        this.templatesProvider = templatesProvider;
        this.soyMapper = soyMapper;
    }

    @Override
    public void write(PostDto post) {
        String permalink = post.getPermlink();
        String outPath = props.getOutPath();

        SoyMapData data = new SoyMapData();
        data.putSingle("post", soyMapper.map(post));

        String postHtml = templatesProvider.build(TemplateId.POST, data);

        Path path = Paths.get(outPath, permalink + ".html");
        writeFile(path, postHtml);
    }

    @Override
    public void write(List<PostDto> posts) {
        for(PostDto post : posts) {
            write(post);
        }
    }

}
