package com.arextest.diff.model.script;

public class ScriptContentInfo {

  private String aliasName;

  private String functionName;

  private String scriptContent;


  public ScriptContentInfo() {
  }

  public ScriptContentInfo(String aliasName, String functionName, String scriptContent) {
    this.aliasName = aliasName;
    this.functionName = functionName;
    this.scriptContent = scriptContent;
  }

  public String getAliasName() {
    return aliasName;
  }

  public void setAliasName(String aliasName) {
    this.aliasName = aliasName;
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
