package com.synitex.blogbuilder;

import com.google.common.base.Strings;
import com.synitex.blogbuilder.builder.IBlogBuilder;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class BlogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(BlogFilter.class);

    private final IBlogProperties props;
    private final IBlogBuilder blogBuilder;

    @Autowired
    public BlogFilter(IBlogProperties props,
                      IBlogBuilder blogBuilder) {
        this.props = props;
        this.blogBuilder = blogBuilder;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // ignore
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)servletRequest;
        String reqUrl = req.getRequestURI();
        if(!Strings.isNullOrEmpty(reqUrl) && reqUrl.endsWith(".html")) {
            String postName = reqUrl.substring(1, reqUrl.length() - 5);
            blogBuilder.build(postName, true);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // ignore
    }
}
