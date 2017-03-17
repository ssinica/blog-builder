package com.synitex.blogbuilder.soy;

import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

public interface ITemplatesProvider {

    String build(TemplateId id, SoyMapData data, SoyTofu tofu);

    String build(TemplateId id, SoyTofu tofu);

}
