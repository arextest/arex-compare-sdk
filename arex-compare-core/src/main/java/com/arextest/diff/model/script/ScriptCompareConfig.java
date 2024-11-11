package com.arextest.diff.model.script;

import java.util.List;

public class ScriptCompareConfig {

  List<String> nodePath;
  ScriptMethod scriptMethod;

  public ScriptCompareConfig() {
  }

  public ScriptCompareConfig(List<String> nodePath, ScriptMethod scriptMethod) {
    this.nodePath = nodePath;
    this.scriptMethod = scriptMethod;
  }

  public List<String> getNodePath() {
    return nodePath;
  }

  public void setNodePath(List<String> nodePath) {
    this.nodePath = nodePath;
  }

  public ScriptMethod getScriptMethod() {
    return scriptMethod;
  }

  public void setScriptMethod(
      ScriptMethod scriptMethod) {
    this.scriptMethod = scriptMethod;
  }

  public static class ScriptMethod {

    private String methodName;
    private String methodArgs;

    public ScriptMethod() {
    }

    public ScriptMethod(String methodName, String methodArgs) {
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
