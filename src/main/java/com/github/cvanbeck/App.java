package com.github.cvanbeck;

import com.github.cvanbeck.EpubFolder;
import java.nio.file.Path;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String path = ("epubs/gutenburhCountMonteCristo.epub");
        try(EpubFolder folder = new EpubFolder(path);){

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
