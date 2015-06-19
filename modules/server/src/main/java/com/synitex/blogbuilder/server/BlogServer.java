package com.synitex.blogbuilder.server;

import com.synitex.blogbuilder.BlogBuilder;
import com.synitex.blogbuilder.props.BlogProperties;
import com.synitex.blogbuilder.props.IBlogProperties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BlogServer {

    private static final Logger log = LoggerFactory.getLogger(BlogServer.class);
    private final BlogBuilder builder;

    //private final BlogBuilder builder;

    public static void main(String[] args) {
        if(args == null || args.length == 0) {
            log.info("usage: java -jar server.jar [path_to_properties]");
        } else {
            new BlogServer(args[0]);
        }
    }

    public BlogServer(String blogPropertiesPath) {

        IBlogProperties props = new BlogProperties(blogPropertiesPath);

        HandlerList handlerList = new HandlerList();

        ServletContextHandler servletsHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletsHandler.setContextPath("/");
        handlerList.addHandler(servletsHandler);

        ResourceHandler rsHandler = new ResourceHandler();
        rsHandler.setResourceBase(props.getOutPath());
        handlerList.addHandler(rsHandler);

        int port = 8080;
        Server httpServer = new Server(port);
        httpServer.setHandler(handlerList);

        try {
            httpServer.start();
            log.info("Blog server is started on port " + port);

            builder = new BlogBuilder(blogPropertiesPath);
            ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
            pool.scheduleAtFixedRate(new BuildJob(builder),
                    TimeUnit.SECONDS.toMillis(5),
                    TimeUnit.SECONDS.toMillis(15),
                    TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start blog server", e);
        }
    }

    private static class BuildJob implements Runnable {

        private final BlogBuilder builder;

        public BuildJob(BlogBuilder builder) {
            this.builder = builder;
        }

        @Override
        public void run() {
            log.info("Refreshing blog...");
            try {
                builder.start();
                log.info("Blog refreshed!");
            } catch (Exception ex) {
                log.error("Failed to refresh blog", ex);
            }
        }
    }

}
