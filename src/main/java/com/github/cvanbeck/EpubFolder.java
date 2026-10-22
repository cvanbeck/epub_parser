package com.github.cvanbeck;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class EpubFolder implements AutoCloseable {
    private ZipFile zipFile;

    public EpubFolder(String location) throws IOException, MalformedEpubException {
        Path path = Path.of(location);
        File file = new File(path.toUri());

        if(!file.getName().endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }

        zipFile = new ZipFile(file);

        validateMimetype();
    }


    private void validateMimetype() throws MalformedEpubException, IOException {
        ZipEntry entry = zipFile.getEntry("mimetype");

        if (entry == null) {
            throw new MalformedEpubException("Mimetype missing");
        }

        try (InputStream mimetype = zipFile.getInputStream(entry)) {
            String mimetypeContent = new String(mimetype.readAllBytes());

            if (!mimetypeContent.equals("application/epub+zip")) {
                throw new MalformedEpubException("Incorrect Mimetype format");
            }
        }
    }


    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
