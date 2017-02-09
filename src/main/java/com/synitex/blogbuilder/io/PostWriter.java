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
public class PostWriter extends AbstractPageWriter implements IPostWriter {

    @Autowired
    public PostWriter(IBlogProperties props,
                      ITemplatesProvider templatesProvider,
                      IDto2SoyMapper soyMapper) {
        super(props, templatesProvider, soyMapper);
    }

    @Override
    public void write(PostDto post, List<TagDto> tags) {
        String permalink = post.getPermlink();
        String outPath = props.getOutPath();

        SoyMapData data = new SoyMapData();
        data.putSingle("post", soyMapper.map(post));
        data.putSingle("tags", soyMapper.mapList(tags));

        String postHtml = templatesProvider.build(TemplateId.POST, data);

        Path path = Paths.get(outPath, permalink + ".html");
        writeFile(path, postHtml);
    }

    @Override
    public void write(List<PostDto> posts, List<TagDto> tags) {
        for(PostDto post : posts) {
            write(post, tags);
        }
    }

}
