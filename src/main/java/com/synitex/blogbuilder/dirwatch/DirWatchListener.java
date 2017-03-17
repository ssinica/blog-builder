package com.synitex.blogbuilder.dirwatch;

import java.nio.file.Path;

public interface DirWatchListener {

    void onPathUpdated(Path path);

}
