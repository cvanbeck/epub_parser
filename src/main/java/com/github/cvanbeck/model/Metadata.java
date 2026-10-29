package com.github.cvanbeck.model;

import java.io.File;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;

public class Metadata {
    HashMap<String, ArrayList<String>> metadata = new HashMap<>();

    public Metadata(HashMap<String, ArrayList<String>> metadata){
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        StringBuilder metadataString = new StringBuilder();
        metadata.forEach((k, v) -> {
            metadataString.append(k + ": " + v.toString() + '\n');
        });
        return metadataString.toString();
    }
}
