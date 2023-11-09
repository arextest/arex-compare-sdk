package com.arextest.diff.model.key;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ReferenceEntity implements Serializable {

  private List<String> pkNodePath;

  private List<String> pkNodeListPath;

  private List<String> fkNodePath;

  public ReferenceEntity() {
  }

  public ReferenceEntity(List<String> pkNodePath, List<String> pkNodeListPath,
      List<String> fkNodePath) {
    this.pkNodePath = pkNodePath;
    this.pkNodeListPath = pkNodeListPath;
    this.fkNodePath = fkNodePath;
  }

  public List<String> getPkNodePath() {
    return pkNodePath;
  }

  public void setPkNodePath(List<String> pkNodePath) {
    this.pkNodePath = pkNodePath;
  }

  public List<String> getPkNodeListPath() {
    return pkNodeListPath;
  }

  public void setPkNodeListPath(List<String> pkNodeListPath) {
    this.pkNodeListPath = pkNodeListPath;
  }

  public List<String> getFkNodePath() {
    return fkNodePath;
  }

  public void setFkNodePath(List<String> fkNodePath) {
    this.fkNodePath = fkNodePath;
  }

  @Override
  public int hashCode() {
    int result = 0;
    result = 31 * result + Objects.hashCode(pkNodePath);
    result = 31 * result + Objects.hashCode(pkNodeListPath);
    result = 31 * result + Objects.hashCode(fkNodePath);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ReferenceEntity)) {
      return false;
    }
    ReferenceEntity that = (ReferenceEntity) obj;

    return Objects.equals(this.pkNodePath, that.pkNodePath)
        && Objects.equals(this.pkNodeListPath, that.pkNodeListPath)
        && Objects.equals(this.fkNodePath, that.fkNodePath);
  }
}
