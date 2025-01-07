package com.lld.designpatterns.strategy.compression;

import java.util.List;

public class Driver {
    public static void main(String[] args) {
        CompressionService rarCompressionService = new CompressionService(new RarCompressionStrategy());
        try {
            rarCompressionService.startCompressing(List.of("File1.txt","File2.txt"),"compOutput");
        } catch (CompressionException e) {
            System.out.println("Compression failed" + e.getMessage());
        }

        CompressionService zipCompressionService = new CompressionService(new ZipCompressionStrategy());
        try {
            rarCompressionService.startCompressing(List.of("File1.txt","File2.txt"),"compOutput");
        } catch (CompressionException e) {
            System.out.println("Compression failed" + e.getMessage());
        }

    }
}
