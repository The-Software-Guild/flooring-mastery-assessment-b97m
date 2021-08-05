package com.bm.flooringmastery;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Acts as the entry point of the whole application
 *
 * @author Benjamin Munoz
 * email: driver396@gmail.com
 * date: Aug 5, 2021
 */
public class App {
    public static void main(String[] args) throws IOException {
        FileSystem fs = FileSystems.getDefault();
        
        Path pp = FileSystems.getDefault().getPath("./Orders");
        System.out.println(pp.toString());
        
        FileSystem fs2 = pp.getFileSystem();
        
        Files.walk(pp, 1).skip(1).forEach(path -> {
            System.out.println(path.getFileName());
        });
    }
}
