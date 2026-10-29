package com.github.cvanbeck;


import com.github.cvanbeck.model.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class EpubFolder implements AutoCloseable {
    private ZipFile zipFile;
    private Document packageDocument;

    public EpubFolder(String location) throws IOException, MalformedEpubException, ParserConfigurationException, SAXException {
        Path path = Path.of(location);
        File file = new File(path.toUri());
        if(!file.getName().endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }

        zipFile = new ZipFile(file);
        try {
            validateMimetype();
            packageDocument = getPackageDocument(zipFile.getEntry(getPackageDocumentLocation(getContainerXml())));

        } catch (Exception e) {
            zipFile.close();
            throw e;
        }
    }

    public Metadata getMetadata() {
        return EpubParser.parseMetadata(packageDocument);

    }

    public Document getPackageDocument() {
        return packageDocument;
    }

    private Document getPackageDocument(ZipEntry packageFile) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try (InputStream input = zipFile.getInputStream(packageFile)) {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            return builder.parse(input);
        }
    }

    private ZipEntry getContainerXml() throws  MalformedEpubException{
        ZipEntry containerXml = zipFile.getEntry("META-INF/container.xml");
        if (containerXml == null){
            throw new MalformedEpubException("container.xml not found");
        }
        return containerXml;
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


    private String getPackageDocumentLocation(ZipEntry xml) throws MalformedEpubException, IOException {
        // TODO: Update function to work with multiple rootfiles
        // TODO: Sort out catch block madness
        try (InputStream input = zipFile.getInputStream(xml)){
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(input);
            doc.getDocumentElement().normalize();

            Node rootFileNode = doc.getElementsByTagName("rootfile").item(0);
            if (rootFileNode == null) {
                throw new MalformedEpubException("rootfile node not found in container.xml");
            }

            Node fullPathAttribute = rootFileNode.getAttributes().getNamedItem("full-path");
            if(fullPathAttribute == null){
                throw new MalformedEpubException("full-path attribute not found");
            }

            String location = fullPathAttribute.getNodeValue();
            if(location.isBlank()){
                throw new MalformedEpubException("full-path missing content");
            }
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
