package com.github.cvanbeck.model;

import java.util.ArrayList;

public class Manifest {
    private final ArrayList<Item> items;

    public Manifest(){
        items = new ArrayList<>();
    }

    public record Item(
            String href,
            String id,
            String mediaType
    ){};

    public ArrayList<Item> getItems(){
        return items;
    };

    public void addItem(Item item){
        items.add(item);
    }
}
