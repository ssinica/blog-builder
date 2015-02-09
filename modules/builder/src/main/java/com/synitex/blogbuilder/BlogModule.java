package com.synitex.blogbuilder;

import com.google.inject.AbstractModule;
import com.synitex.blogbuilder.asciidoc.AsciidocService;
import com.synitex.blogbuilder.asciidoc.IAsciidocService;
import com.synitex.blogbuilder.io.IIndexWriter;
import com.synitex.blogbuilder.io.IPostWriter;
import com.synitex.blogbuilder.io.ITagsPageWriter;
import com.synitex.blogbuilder.io.IndexWriter;
import com.synitex.blogbuilder.io.PostWriter;
import com.synitex.blogbuilder.io.TagsPageWriter;
import com.synitex.blogbuilder.props.BlogProperties;
import com.synitex.blogbuilder.props.IBlogProperties;
import com.synitex.blogbuilder.soy.Dto2SoyMapper;
import com.synitex.blogbuilder.soy.IDto2SoyMapper;
import com.synitex.blogbuilder.soy.ITemplatesProvider;
import com.synitex.blogbuilder.soy.ITofuProvider;
import com.synitex.blogbuilder.soy.TemplatesProvider;
import com.synitex.blogbuilder.soy.TofuProvider;


public class BlogModule extends AbstractModule {

    private final BlogProperties props;

    public BlogModule(String blogPropertiesPath) {
        props = new BlogProperties(blogPropertiesPath);
    }

    @Override
    protected void configure() {
        bind(IBlogProperties.class).toInstance(props);
        bind(ITofuProvider.class).to(TofuProvider.class);
        bind(IAsciidocService.class).to(AsciidocService.class);
        bind(ITemplatesProvider.class).to(TemplatesProvider.class);
        bind(IPostWriter.class).to(PostWriter.class);
        bind(IIndexWriter.class).to(IndexWriter.class);
        bind(IDto2SoyMapper.class).to(Dto2SoyMapper.class);
        bind(ITagsPageWriter.class).to(TagsPageWriter.class);
    }

}
