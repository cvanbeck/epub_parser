package com.github.cvanbeck;

import java.com.github.cvanbeck.EpubFolder;
import java.nio.file.Path;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String path = ("epubs/gutenburhCountMonteCristo.epub");
        try(EpubFolder folder = new EpubFolder(path);){
            System.out.println(folder.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
