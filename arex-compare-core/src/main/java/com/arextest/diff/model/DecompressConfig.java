package com.arextest.diff.model;

import java.util.List;

/**
 * Created by rchen9 on 2023/4/26.
 */
public class DecompressConfig {

  /**
   * The bean name of the decompression method which is implement the DecompressService interface,
   * you can use an alias
   */
  private String name;

  /**
   * the collection of the node path need to decompress
   */
  private List<List<String>> nodePath;

  /**
   * variable parameter
   */
  private String args;

  public DecompressConfig() {
  }

  public DecompressConfig(String name, List<List<String>> nodePath) {
    this.name = name;
    this.nodePath = nodePath;
  }

  public DecompressConfig(String name, List<List<String>> nodePath, String args) {
    this.name = name;
    this.nodePath = nodePath;
    this.args = args;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<List<String>> getNodePath() {
    return nodePath;
  }

  public void setNodePath(List<List<String>> nodePath) {
    this.nodePath = nodePath;
  }

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
  }
}
