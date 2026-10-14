package com.github.cvanbeck;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class EpubFolder implements AutoCloseable {
    private Path originalPath;
    private Path path;
    public EpubFolder(String location){
        if(!location.endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }

        this.originalPath = Path.of(location);
        this.path = Path.of(location + ".zip");
        try {
            Files.move(originalPath, path);
            System.out.println(path.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        Files.move(path, originalPath);
    }
}
