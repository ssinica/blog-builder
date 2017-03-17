package com.synitex.blogbuilder.props;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DevProperties implements IDevProperties {

    @Value("${blog.dev.mode}")
    private boolean devMode = false;

    @Value("${blog.dev.templates.path}")
    private String templatesPath;

    @Value("${blog.dev.assets.path}")
    private String assetsPath;

    @Override
    public boolean isDevMode() {
        return devMode;
    }

    @Override
    public String getTemplatesPath() {
        return templatesPath;
    }

    @Override
    public String getAssetsPath() {
        return assetsPath;
    }

}
