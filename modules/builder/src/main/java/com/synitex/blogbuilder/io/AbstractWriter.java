package com.synitex.blogbuilder.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public abstract class AbstractWriter {

    public void writeFile(Path path, String content) {
        try {
            Files.write(path, content.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save file", ex);
        }
    }

}