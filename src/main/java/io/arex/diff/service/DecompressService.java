package io.arex.diff.service;

public interface DecompressService {

    String getAliasName();

    String decompress(String str) throws Throwable;
}
