package com.arextest.diff.model.pathparse.expression;

import java.util.List;
import java.util.Objects;

public class EqualsExpression implements PathExpression {

  // ['studentInfo','studentName'] => 'studentInfo/studentName'
  private List<String> leftValue;

  private String rightValue;

  public EqualsExpression() {
  }

  public EqualsExpression(List<String> leftValue, String rightValue) {
    this.leftValue = leftValue;
    this.rightValue = rightValue;
  }

  public List<String> getLeftValue() {
    return leftValue;
  }

  public void setLeftValue(List<String> leftValue) {
    this.leftValue = leftValue;
  }

  public String getRightValue() {
    return rightValue;
  }

  public void setRightValue(String rightValue) {
    this.rightValue = rightValue;
  }

  @Override
  public int hashCode() {
    return (leftValue == null ? 0 : leftValue.hashCode()) + (rightValue == null ? 0
        : rightValue.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof EqualsExpression)) {
      return false;
    }

    EqualsExpression that = (EqualsExpression) obj;
    return Objects.equals(this.getLeftValue(), that.getLeftValue())
        && Objects.equals(this.getRightValue(), that.getRightValue());
  }
}
