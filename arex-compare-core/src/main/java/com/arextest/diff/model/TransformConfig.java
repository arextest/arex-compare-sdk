package com.arextest.diff.model;

import java.util.List;

public class TransformConfig {

  /**
   * the collection of the node path need to transform
   */
  List<List<String>> nodePath;
  List<TransformMethod> transformMethods;

  public TransformConfig() {
  }

  public TransformConfig(List<List<String>> nodePath, List<TransformMethod> transformMethods) {
    this.nodePath = nodePath;
    this.transformMethods = transformMethods;
  }

  public List<List<String>> getNodePath() {
    return nodePath;
  }

  public void setNodePath(List<List<String>> nodePath) {
    this.nodePath = nodePath;
  }

  public List<TransformMethod> getTransformMethods() {
    return transformMethods;
  }

  public void setTransformMethods(
      List<TransformMethod> transformMethods) {
    this.transformMethods = transformMethods;
  }

  public static class TransformMethod {

    private String methodName;
    private String methodArgs;

    public TransformMethod() {
    }

    public TransformMethod(String methodName, String methodArgs) {
      this.methodName = methodName;
      this.methodArgs = methodArgs;
    }

    public String getMethodName() {
      return methodName;
    }

    public void setMethodName(String methodName) {
      this.methodName = methodName;
    }

    public String getMethodArgs() {
      return methodArgs;
    }

    public void setMethodArgs(String methodArgs) {
      this.methodArgs = methodArgs;
    }
  }

}
