package com.github.cvanbeck;

import com.github.cvanbeck.model.Manifest;
import com.github.cvanbeck.model.Metadata;
import com.github.cvanbeck.model.Spine;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class EpubParser {
    private static final int METADATA_POS = 1;
    private static final int MANIFEST_POS = 2;
    private static final int SPINE_POS = 3;

    public static Metadata parseMetadata(Document document) {
        NodeList nodeList = getNodeList(document, METADATA_POS);
        Metadata metadata = new Metadata();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node current = nodeList.item(i);
            String nodeName = current.getNodeName();
            // DocumentBuilder adds elements for whitespace for some reason, this check skips those and any meta tags
            if ((current.getNodeType() != Node.ELEMENT_NODE || nodeName.equals("meta"))) { continue; }

            ArrayList<String> currentElement = metadata.getMetadata().get(nodeName.substring(3));
            if (currentElement != null) {
                currentElement.add(current.getTextContent());
            } else {
                System.err.println("WARNING: Unexpected '" + nodeName + "' element found whilst parsing metadata, this is not " +
                        "a valid DC type and has been ignored.");
            }
        }
        return metadata;
    }

    public static Manifest parseManifest(Document document){
        NodeList nodeList = getNodeList(document, 3);
        Manifest manifest = new Manifest();
        for(int i = 0; i < nodeList.getLength(); i++){
            Node current = nodeList.item(i);
            if (current.getNodeType() != Node.ELEMENT_NODE) { continue; }
            Manifest.Item item = new Manifest.Item(
                    current.getAttributes().getNamedItem("href").getNodeValue() ,
                    current.getAttributes().getNamedItem("id").getNodeValue(),
                    current.getAttributes().getNamedItem("media-type").getNodeValue()
            );
            manifest.addItem(item);
        }

        return manifest;
    }

    public static Spine parseSpine(Document document){
        NodeList nodeList = getNodeList(document, 5);
        Spine spine = new Spine();
        for(int i = 0; i < nodeList.getLength(); i++){
            Node current = nodeList.item(i);
            if(current.getNodeType() != Node.ELEMENT_NODE) {continue;}
            spine.addItemRef(current.getAttributes().getNamedItem("idref").getNodeValue());
        }
        return spine;
    }

    private static NodeList getNodeList(Document document, int position){
        return document.getDocumentElement().getChildNodes().item(position).getChildNodes();
    }
}

