package com.arextest.diff.service;

public interface DecompressService {

  /**
   * The alias of the decompression method, used for unique positioning in the system
   *
   * @return
   */
  String getAliasName();

  /**
   * Concrete decompression implementation
   *
   * @param str  The original field value that needs to be decompressed
   * @param args Additional parameters for system page configuration, used for decompression
   * @return
   */
  String decompress(String str, String args);
}