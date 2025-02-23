package com.lld.designpatterns.strategy.compression;

import java.util.List;

public interface ICompressionStrategy {
    void compressFiles(List<String> fileNames, String outFileName) throws CompressionException;
}
