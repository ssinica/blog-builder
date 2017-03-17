package com.synitex.blogbuilder.dirwatch;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DirWatchService implements IDirWatchService, WatchTaskCallback {

    private static final Logger log = LoggerFactory.getLogger(DirWatchService.class);

    private final WatchService watchService;
    private final ExecutorService pool;
    private Map<String, DirWatchClient> listeners = Maps.newConcurrentMap();

    @Autowired
    public DirWatchService() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to init directory watch service", e);
        }
        pool = Executors.newFixedThreadPool(3);
    }

    @Override
    public void registerListener(DirWatchListener listener, DirWatchConfig config) {
        log.info("Register listener for path {}.", config.getRootPath());
        listeners.put(config.getRootPath(), new DirWatchClient(listener, config));
        pool.execute(new WatchTask(
                config.getRootPath(),
                config.getSleepBetweenPollInMillis(),
                watchService,
                this));
    }

    @Override
    public void onPathUpdated(String rootPath, Path path) {
        DirWatchClient client = listeners.get(rootPath);
        if(client.getConfig().getInterestedIn().test(path)) {
            client.getListener().onPathUpdated(path);
        }
    }

    // --------------------------------------------------------

    private class DirWatchClient {
        private final DirWatchListener listener;
        private final DirWatchConfig config;

        private DirWatchClient(DirWatchListener listener, DirWatchConfig config) {
            this.listener = listener;
            this.config = config;
        }

        public DirWatchListener getListener() {
            return listener;
        }

        public DirWatchConfig getConfig() {
            return config;
        }
    }
    
}
