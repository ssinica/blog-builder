package com.synitex.blogbuilder.dirwatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;

public class WatchTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(WatchTask.class);

    private final String rootPath;
    private final long sleepBetweenPollsInMillis;
    private final WatchService watchService;
    private final WatchTaskCallback watchTaskCallback;

    public WatchTask(String rootPath,
                      long sleepBetweenPollsInMillis,
                      WatchService watchService,
                      WatchTaskCallback watchTaskCallback) {
        this.rootPath = rootPath;
        this.sleepBetweenPollsInMillis = sleepBetweenPollsInMillis;
        this.watchService = watchService;
        this.watchTaskCallback = watchTaskCallback;
    }

    @Override
    public void run() {

        watch(new File(rootPath).toPath());
        boolean running = true;

        while(running) {

            WatchKey watchKey = watchService.poll();

            if(watchKey != null) {
                Path path = (Path)watchKey.watchable();

                log.debug("A change has occured for the path: {} ", path);

                for(WatchEvent<?> event: watchKey.pollEvents()) {

                    WatchEvent.Kind<?> kind = event.kind();
                    log.debug("Received an event: {} for the path: {}", kind, path);

                    Path eventPath = ((WatchEvent<Path>)event).context();
                    if(eventPath != null) {
                        eventPath = path.resolve(eventPath);
                    }

                    // add created path to watch service
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        if (Files.isDirectory(eventPath)) {
                            watch(eventPath);
                        }
                    }

                    log.debug("Path {} updated!", eventPath);
                    watchTaskCallback.onPathUpdated(rootPath, eventPath);
                }

                if(!watchKey.reset()) {
                    log.debug("Failed to call reset() on watch key with path: {}. The path will be removed from watch list.", path);
                    // watch key is no longer valid - cancel watching
                    watchKey.cancel();
                }

            }

            try {
                log.debug("No new events from watch service. Will sleep for {} ms.", sleepBetweenPollsInMillis);
                Thread.sleep(sleepBetweenPollsInMillis);
            } catch (InterruptedException ex) {
                log.warn("Watch service was interrupeted. Will finish.", ex);
                running = false;
            }

        }

    }

    private void watch(final Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    log.info("Polling for changes: {}", dir);
                    dir.register(watchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.OVERFLOW);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
