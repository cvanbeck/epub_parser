package com.github.cvanbeck;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;


public class EpubFolder implements AutoCloseable {
    private ZipFile zipFile;
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
            File file = new File(path.toUri());
            zipFile = new ZipFile(file);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        zipFile.close();
        Files.move(path, originalPath);
    }
}
