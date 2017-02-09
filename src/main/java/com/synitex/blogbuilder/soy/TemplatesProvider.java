package com.synitex.blogbuilder.soy;

import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofu.Renderer;
import com.synitex.blogbuilder.props.BlogAuthorProperties;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplatesProvider implements ITemplatesProvider {

    private static final Logger log = LoggerFactory.getLogger(TemplatesProvider.class);

    private final ITofuProvider tofuProvider;
    private final IBlogProperties props;
    private final IDto2SoyMapper soyMapper;

    @Autowired
    public TemplatesProvider(ITofuProvider tofuProvider,
                             IBlogProperties props,
                             IDto2SoyMapper soyMapper) {
        this.tofuProvider = tofuProvider;
        this.props = props;
        this.soyMapper = soyMapper;
    }

    @Override
    public String build(TemplateId id, SoyMapData data) {
        if(log.isDebugEnabled()) {
            log.debug("Building template {}", id.getTemplateId());
        }

        SoyTofu tofu = tofuProvider.getTofu();
        Renderer renderer = tofu.newRenderer(id.getTemplateId());

        SoyMapData dataWrapper = new SoyMapData();

        if(data == null) {
            data = new SoyMapData();
        }
        dataWrapper.putSingle("data", data);

        dataWrapper.putSingle("ctx", createCtx());

        renderer.setData(dataWrapper);

        return renderer.render();
    }

    @Override
    public String build(TemplateId id) {
        return build(id, null);
    }

    private SoyMapData createCtx() {
        SoyMapData data = new SoyMapData();

        BlogAuthorProperties author = props.getAuthorProperties();
        data.putSingle("author", soyMapper.map(author));
        data.put("blogRootUrl", props.getBlogRootUrl());
        data.put("gaTrackingId", props.getGaTrackingId());
        data.put("gaDomain", props.getGaDomainName());

        return data;
    }

}
