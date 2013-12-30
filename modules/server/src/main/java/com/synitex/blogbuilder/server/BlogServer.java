package com.synitex.blogbuilder.server;

import com.synitex.blogbuilder.props.BlogProperties;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class BlogServer {

    public static void main(String[] args) {
        new BlogServer(args[0]);
    }

    public BlogServer(String bbPropsPath) {

        IBlogProperties props = new BlogProperties(bbPropsPath);

        HandlerList handlerList = new HandlerList();

        ServletContextHandler servletsHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletsHandler.setContextPath("/");
        handlerList.addHandler(servletsHandler);

        ResourceHandler rsHandler = new ResourceHandler();
        rsHandler.setResourceBase(props.getOutPath());
        handlerList.addHandler(rsHandler);

        Server httpServer = new Server(8080);
        httpServer.setHandler(handlerList);
        try {
            httpServer.start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start server", e);
        }
    }

}
