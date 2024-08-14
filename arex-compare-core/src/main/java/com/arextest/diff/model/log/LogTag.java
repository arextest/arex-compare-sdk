package com.arextest.diff.model.log;

import java.io.Serializable;

public class LogTag implements Serializable {

  /**
   * {@link com.arextest.diff.model.enumeration.ErrorType}
   */
  private int errorType = 0;

  private NodeErrorType nodeErrorType;

  public LogTag() {
  }

  public int getErrorType() {
    return errorType;
  }

  public void setErrorType(int errorType) {
    this.errorType = errorType;
  }

  public NodeErrorType getNodeErrorType() {
    return nodeErrorType;
  }

  public void setNodeErrorType(NodeErrorType nodeErrorType) {
    this.nodeErrorType = nodeErrorType;
  }

  public static class NodeErrorType {

    private String baseNodeType;
    private String testNodeType;

    public NodeErrorType() {
    }

    public String getBaseNodeType() {
      return baseNodeType;
    }

    public void setBaseNodeType(String baseNodeType) {
      this.baseNodeType = baseNodeType;
    }

    public String getTestNodeType() {
      return testNodeType;
    }

    public void setTestNodeType(String testNodeType) {
      this.testNodeType = testNodeType;
    }
  }
}
