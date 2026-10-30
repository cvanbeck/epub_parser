package com.github.cvanbeck;


import com.github.cvanbeck.model.Manifest;
import com.github.cvanbeck.model.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class EpubFolder implements AutoCloseable {
    private final ZipFile zipFile;
    private final Document packageDocument;

    public EpubFolder(String location) throws IOException, MalformedEpubException, ParserConfigurationException, SAXException {
        Path path = Path.of(location);
        File file = new File(path.toUri());
        if(!file.getName().endsWith(".epub")){throw new IllegalArgumentException("File must be of type .epub");}
        zipFile = new ZipFile(file);

        try {
            validateMimetype();
            packageDocument = buildPackageDocument();
        } catch (Exception e) {
            zipFile.close();
            throw e;
        }
    }

    public Metadata getMetadata() {
        return EpubParser.parseMetadata(packageDocument);
    }

    public Manifest getManifest() {
        return EpubParser.parseManifest(packageDocument);
    }

    private void validateMimetype() throws MalformedEpubException, IOException {
        ZipEntry entry = zipFile.getEntry("mimetype");
        if (entry == null) { throw new MalformedEpubException("Mimetype missing");}

        try (InputStream mimetype = zipFile.getInputStream(entry)) {
            String mimetypeContent = new String(mimetype.readAllBytes());
            if (!mimetypeContent.equals("application/epub+zip")) {
                throw new MalformedEpubException("Incorrect Mimetype format");
            }
        }
    }

    private Document buildPackageDocument() throws SAXException, IOException, ParserConfigurationException, MalformedEpubException {
        ZipEntry packageFile = zipFile.getEntry(getPackageDocumentLocation());
        if(packageFile == null) {throw new MalformedEpubException("Package Document not found");}

        try (InputStream input = zipFile.getInputStream(packageFile)) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(input);
        }
    }

    private ZipEntry getContainerXml() throws  MalformedEpubException{
        ZipEntry containerXml = zipFile.getEntry("META-INF/container.xml");
        if (containerXml == null){throw new MalformedEpubException("container.xml not found");}
        return containerXml;
    }

    private String getPackageDocumentLocation() throws MalformedEpubException, IOException {
        // TODO: Update function to work with multiple rootfiles
        ZipEntry container = getContainerXml();
        try (InputStream input = zipFile.getInputStream(container)){
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(input);

            Node rootFile = doc.getElementsByTagName("rootfile").item(0);
            if (rootFile == null) {throw new MalformedEpubException("rootfile node not found in container.xml");}

            Node fullPath = rootFile.getAttributes().getNamedItem("full-path");
            if(fullPath == null){throw new MalformedEpubException("full-path attribute not found");}

            String location = fullPath.getNodeValue();
            if(location.isBlank()){throw new MalformedEpubException("full-path missing content");}

            return location;

        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Failed to configure XML parser", e);
        } catch (SAXException e){
            throw new MalformedEpubException("Invalid container.xml", e);
        }

    };

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
