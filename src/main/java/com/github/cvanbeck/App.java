package com.github.cvanbeck;

import com.github.cvanbeck.EpubFolder;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        String path = ("epubs/gutenburhCountMonteCristo.epub");
        try (EpubFolder folder = new EpubFolder(path);) {
            System.out.println("EPUB opened successfully");

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to open EPUB: " + e.getMessage());
        }
    }
}
