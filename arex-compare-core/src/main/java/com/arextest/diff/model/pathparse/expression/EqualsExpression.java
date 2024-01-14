package com.arextest.diff.model.pathparse.expression;

import java.util.List;

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
}
