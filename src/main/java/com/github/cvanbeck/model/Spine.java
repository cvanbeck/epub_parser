package com.github.cvanbeck.model;

import java.util.ArrayList;
import java.util.List;

public class Spine {
    private List<String> itemRefs;

    public Spine(){
        itemRefs = new ArrayList<>();
    }

    public List<String> getItemRef(){
        return itemRefs;
    }
    public String getItemRef(int index){
        return itemRefs.get(index);

    }


    public void addItemRef(String ref){
        itemRefs.add(ref);
    }

    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        getItemRef().forEach(itemRef -> {
            string.append(itemRef + "\n");
        });
        return  string.toString();
    }
}

