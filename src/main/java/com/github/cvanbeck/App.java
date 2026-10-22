package com.github.cvanbeck;


import java.io.IOException;

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
        } catch (MalformedEpubException e){
            System.err.println("Invalid EPUB file: " + e.getMessage());
        }
    }
}
