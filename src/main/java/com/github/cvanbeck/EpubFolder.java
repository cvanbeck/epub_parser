package com.github.cvanbeck;


import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
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
import java.util.zip.ZipInputStream;


public class EpubFolder implements AutoCloseable {
    private ZipFile zipFile;
    private ZipEntry content;

    public EpubFolder(String location) throws IOException, MalformedEpubException {
        Path path = Path.of(location);
        File file = new File(path.toUri());

        if(!file.getName().endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }

        zipFile = new ZipFile(file);
        validateMimetype();

        ZipEntry containerXml = zipFile.getEntry("META-INF/container.xml");
        if (containerXml == null){
            throw new MalformedEpubException("container.xml not found");
        }
        System.out.println(getContentLocation(containerXml));

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

    private String getContentLocation(ZipEntry xml) throws MalformedEpubException {
        // TODO: Update function to work with multiple rootfiles
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

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    };

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
