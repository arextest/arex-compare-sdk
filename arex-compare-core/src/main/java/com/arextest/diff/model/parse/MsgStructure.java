package com.arextest.diff.model.parse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MsgStructure {

  Map<String, MsgStructure> node = new HashMap<>();
  private String fieldName;

  public MsgStructure() {
  }

  public MsgStructure(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Map<String, MsgStructure> getNode() {
    return node;
  }

  public void setNode(Map<String, MsgStructure> node) {
    this.node = node;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MsgStructure that = (MsgStructure) o;
    return Objects.equals(fieldName, that.fieldName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fieldName);
  }
}