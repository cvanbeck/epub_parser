package com.github.cvanbeck;

import com.github.cvanbeck.model.Metadata;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;


public class EpubParser {
    public static Metadata parseMetadata(Document document) {
        NodeList nodeList = document.getDocumentElement().getChildNodes().item(1).getChildNodes();
        Metadata metadata = new Metadata();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node current = nodeList.item(i);
            String nodeName = current.getNodeName();

            // This means whitespace elements get skipped
            if(current.getNodeType() == Node.ELEMENT_NODE && !nodeName.equals("meta")) {
                String key = String.valueOf(metadata.getMetadata().get(nodeName.substring(3)));
                if(key.equals("null")){
                    continue;
                }
                metadata.getMetadata().get(nodeName.substring(3)).add(current.getTextContent());
            }
        }
        return metadata;
    }
}
