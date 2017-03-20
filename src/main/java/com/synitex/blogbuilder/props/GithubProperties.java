package com.synitex.blogbuilder.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GithubProperties implements IGithubProperties {

    @Value("${blog.github.branch}")
    private String branch;

    @Override
    public String getBranch() {
        return branch;
    }

}
