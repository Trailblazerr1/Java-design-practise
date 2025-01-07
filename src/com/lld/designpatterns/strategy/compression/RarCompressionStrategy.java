package com.lld.designpatterns.strategy.compression;

import java.util.List;

public class RarCompressionStrategy implements ICompressionStrategy {
    @Override
    public void compressFiles(List<String> fileNames, String outFileName) throws CompressionException {
        try {
            System.out.println("Compress files as rar");
        } catch (Exception e) {
            throw new CompressionException("Rar compression failed"+e.getMessage());
        }
    }
}
