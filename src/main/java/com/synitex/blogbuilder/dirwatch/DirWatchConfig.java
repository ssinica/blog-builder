package com.synitex.blogbuilder.dirwatch;

import java.nio.file.Path;
import java.util.function.Predicate;

public class DirWatchConfig {

    private final String rootPath;
    private final long sleepBetweenPollInMillis;
    private final Predicate<Path> interestedIn;


    public DirWatchConfig(String rootPath,
                          long sleepBetweenPollInMillis,
                          Predicate<Path> interestedIn) {
        this.rootPath = rootPath;
        this.sleepBetweenPollInMillis = sleepBetweenPollInMillis;
        this.interestedIn = interestedIn;
    }

    public long getSleepBetweenPollInMillis() {
        return sleepBetweenPollInMillis;
    }

    public Predicate<Path> getInterestedIn() {
        return interestedIn;
    }

    public String getRootPath() {
        return rootPath;
    }
}
