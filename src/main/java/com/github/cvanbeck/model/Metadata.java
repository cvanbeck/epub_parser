    package com.github.cvanbeck.model;


    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.LinkedHashMap;

    public class Metadata {
        private final LinkedHashMap<String, ArrayList<String>> metadata;

        public Metadata(){
            metadata = new LinkedHashMap<>();

            // Required elements
            metadata.put("identifier", new ArrayList<>());
            metadata.put("title", new ArrayList<>());
            metadata.put("language", new ArrayList<>());

            // Optional elements
            metadata.put("contributor", new ArrayList<>());
            metadata.put("coverage", new ArrayList<>());
            metadata.put("creator", new ArrayList<>());
            metadata.put("date", new ArrayList<>());
            metadata.put("description", new ArrayList<>());
            metadata.put("format", new ArrayList<>());
            metadata.put("publisher", new ArrayList<>());
            metadata.put("relation", new ArrayList<>());
            metadata.put("rights", new ArrayList<>());
            metadata.put("source", new ArrayList<>());
            metadata.put("subject", new ArrayList<>());
            metadata.put("type", new ArrayList<>());
        }

        public HashMap<String, ArrayList<String>> getMetadata() {
            return metadata;
        }

        @Override
        public String toString() {
            StringBuilder metadataString = new StringBuilder();
            metadata.forEach((k, v) -> {
                if(!v.isEmpty()){
                    metadataString.append(k + ": " + v.toString() + '\n');
                }
            });
            return metadataString.toString();
        }
    }
