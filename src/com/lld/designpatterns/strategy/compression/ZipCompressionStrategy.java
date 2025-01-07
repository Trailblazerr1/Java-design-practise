package com.lld.designpatterns.strategy.compression;

import java.util.List;

public class ZipCompressionStrategy implements ICompressionStrategy {
    @Override
    public void compressFiles(List<String> fileNames, String outFileName) throws CompressionException {
        try {
            System.out.println("Compress files as zip");
        } catch (Exception e) {
            throw new CompressionException("Zip compression failed "+e.getMessage());
        }
    }
}
