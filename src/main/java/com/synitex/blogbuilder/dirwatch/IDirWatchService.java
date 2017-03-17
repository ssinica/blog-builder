package com.synitex.blogbuilder.dirwatch;

public interface IDirWatchService {

    void registerListener(DirWatchListener listener, DirWatchConfig config);
    
}
