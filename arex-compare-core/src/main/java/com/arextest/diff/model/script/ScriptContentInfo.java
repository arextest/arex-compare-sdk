package com.arextest.diff.model.script;

public class ScriptContentInfo {

  private String functionName;

  private String scriptContent;


  public ScriptContentInfo() {
  }

  public ScriptContentInfo(String functionName, String scriptContent) {
    this.functionName = functionName;
    this.scriptContent = scriptContent;
  }

  public String getFunctionName() {
    return functionName;
  }

  public void setFunctionName(String functionName) {
    this.functionName = functionName;
  }

  public String getScriptContent() {
    return scriptContent;
  }

  public void setScriptContent(String scriptContent) {
    this.scriptContent = scriptContent;
  }
}
