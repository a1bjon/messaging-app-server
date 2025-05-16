package com.xepr.core.model;

import java.io.IOException;

@FunctionalInterface
public interface Compressible {

    double DEFAULT_SCALE = 1.0;

    double DEFAULT_QUALITY = 0.7;

    byte[] compress(byte[] data, double scale, double quality) throws IOException;
}
