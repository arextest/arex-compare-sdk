package com.arextest.diff.model.script;

import com.arextest.diff.model.log.NodeEntity;
import java.util.List;

public class ScriptMethodContext {

  private List<NodeEntity> basePath;
  private List<NodeEntity> testPath;

  public List<NodeEntity> getBasePath() {
    return basePath;
  }

  public void setBasePath(List<NodeEntity> basePath) {
    this.basePath = basePath;
  }

  public List<NodeEntity> getTestPath() {
    return testPath;
  }

  public void setTestPath(List<NodeEntity> testPath) {
    this.testPath = testPath;
  }
}