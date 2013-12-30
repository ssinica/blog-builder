package com.synitex.blogbuilder.soy;

import com.google.template.soy.data.SoyMapData;

public interface ITemplatesProvider {

    String build(TemplateId id, SoyMapData data);

    String build(TemplateId id);

}
