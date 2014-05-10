package com.synitex.blogbuilder.io;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.template.soy.data.SoyMapData;
import com.synitex.blogbuilder.dto.PostDto;
import com.synitex.blogbuilder.dto.TagDto;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.TemplateId;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class AbstractPageWriter extends AbstractWriter {

    protected final ITemplatesProvider templatesProvider;
    protected final IBlogProperties props;
    protected final IDto2SoyMapper soyMapper;

    @Inject
    public AbstractPageWriter(IBlogProperties props,
                       ITemplatesProvider templatesProvider,
                       IDto2SoyMapper soyMapper) {
        this.props = props;
        this.templatesProvider = templatesProvider;
        this.soyMapper = soyMapper;
    }

    public void write(List<PostDto> posts, TemplateId templateId, Path path, SoyMapData data) {
        data.putSingle("posts", soyMapper.mapList(posts));

        List<TagDto> tags = collectTags(posts);
        data.putSingle("tags", soyMapper.mapList(tags));

        String html = templatesProvider.build(templateId, data);

        writeFile(path, html);
    }

    protected List<TagDto> collectTags(List<PostDto> posts) {
        List<TagDto> tags = new ArrayList<>();

        HashMultiset<TagDto> mset = HashMultiset.create();
        for(PostDto post : posts) {
            List<TagDto> values = post.getTags();
            if(!Iterables.isEmpty(values)) {
                mset.addAll(values);
            }
        }

        for(TagDto tag : mset.elementSet()) {
            tags.add(new TagDto(tag.getText(), tag.getFile(), mset.count(tag)));
        }

        Collections.sort(tags, new Comparator<TagDto>() {
            @Override
            public int compare(TagDto o1, TagDto o2) {
                int c1 = o1.getCount();
                int c2 = o2.getCount();
                if (c1 == c2) {
                    return 0;
                } else if (c1 > c2) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return tags;
    }

}
