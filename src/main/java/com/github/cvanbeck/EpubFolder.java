package com.github.cvanbeck;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;


public class EpubFolder implements AutoCloseable {
    private ZipFile zipFile;


    public EpubFolder(String location){
        if(!location.endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }

        Path path = Path.of(location);
        try {
            File file = new File(path.toUri());
            zipFile = new ZipFile(file);
            System.out.println(zipFile.getEntry("mimetype"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        zipFile.close();
    }
}
