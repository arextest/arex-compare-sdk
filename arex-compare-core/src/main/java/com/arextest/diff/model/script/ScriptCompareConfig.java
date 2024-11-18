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

    private String functionName;
    private String functionArgs;

    public ScriptMethod() {
    }

    public ScriptMethod(String functionName, String functionArgs) {
      this.functionName = functionName;
      this.functionArgs = functionArgs;
    }

    public String getFunctionName() {
      return functionName;
    }

    public void setFunctionName(String functionName) {
      this.functionName = functionName;
    }

    public String getFunctionArgs() {
      return functionArgs;
    }

    public void setFunctionArgs(String functionArgs) {
      this.functionArgs = functionArgs;
    }
  }

}
