package com.synitex.blogbuilder.server;

import com.synitex.blogbuilder.IBlogBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogRest {

    private final IBlogBuilder blogBuilder;

    @Autowired
    public BlogRest(IBlogBuilder blogBuilder) {
        this.blogBuilder = blogBuilder;
    }

    @RequestMapping(value="/rebuild", method= RequestMethod.GET)
    public String rebuild() {
        blogBuilder.build();
        return "OK";
    }
    
}
