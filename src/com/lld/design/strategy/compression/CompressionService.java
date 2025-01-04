package com.lld.design.strategy.compression;

import java.util.List;

public class CompressionService {
    private ICompressionStrategy compressionStrategy;

    public CompressionService(ICompressionStrategy compressionStrategy) {
        this.compressionStrategy = compressionStrategy;
    }

    public void startCompressing(List<String> fileList, String outputFileName) throws CompressionException {
        compressionStrategy.compressFiles(fileList,outputFileName);
        System.out.printf("Done");
    }
}
