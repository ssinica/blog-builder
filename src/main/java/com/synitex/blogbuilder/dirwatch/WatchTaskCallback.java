package com.synitex.blogbuilder.dirwatch;

import java.nio.file.Path;

public interface WatchTaskCallback {

    void onPathUpdated(String rootPath, Path path);

}
