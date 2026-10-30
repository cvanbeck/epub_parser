package com.github.cvanbeck.model;

import java.util.ArrayList;
import java.util.List;

public class Spine {
    private List<String> itemRefs;

    public Spine(){
        itemRefs = new ArrayList<>();
    }

    public List<String> getItemRefs(){
        return itemRefs;
    }

    public void addItemRef(String ref){
        itemRefs.add(ref);
    }
}

