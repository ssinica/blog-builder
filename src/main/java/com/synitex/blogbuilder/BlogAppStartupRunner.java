package com.synitex.blogbuilder;

import com.synitex.blogbuilder.builder.IBlogBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class BlogAppStartupRunner implements CommandLineRunner {

    private final IBlogBuilder blogBuilder;

    @Autowired
    public BlogAppStartupRunner(IBlogBuilder blogBuilder) {
        this.blogBuilder = blogBuilder;
    }

    @Override
    public void run(String... strings) throws Exception {
        blogBuilder.build(false);
    }
    
}
