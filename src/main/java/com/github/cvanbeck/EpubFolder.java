package com.github.cvanbeck;


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
    private ZipFile zipFile;
    private File packageDocument;

    public EpubFolder(String location) throws IOException, MalformedEpubException {
        Path path = Path.of(location);
        File file = new File(path.toUri());
        if(!file.getName().endsWith(".epub")){
            throw new IllegalArgumentException("File must be of type .epub");
        }

        zipFile = new ZipFile(file);
        try {
            validateMimetype();
            packageDocument = getPackageDocument(getContainerXml());

        } catch (Exception e) {
            zipFile.close();
            throw e;
        }
    }

    public File getPackageDocument() {
        return packageDocument;
    }

    private File getPackageDocument(ZipEntry containerXml) throws MalformedEpubException, IOException{
        File packageDocument = new File(containerXml.getName());
        if(packageDocument == null){
            throw new MalformedEpubException("Package document not found");
        }
        return packageDocument;
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
