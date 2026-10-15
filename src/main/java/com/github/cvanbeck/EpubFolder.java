package com.github.cvanbeck;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;


public class EpubFolder implements AutoCloseable {
    private ZipFile zipFile;


    public EpubFolder(String location) throws IOException {
        Path path = Path.of(location);
        File file = new File(path.toUri());

        if(!file.getName().endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }
        
        zipFile = new ZipFile(file);
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
