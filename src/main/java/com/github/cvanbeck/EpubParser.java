package com.github.cvanbeck;

import com.github.cvanbeck.model.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class EpubParser {
    public static Metadata parseMetadata(Document document) {
        NodeList nodeList = document.getDocumentElement().getChildNodes().item(1).getChildNodes();
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
                        "a valid DC type");
            }
        }
        return metadata;
    }
}
