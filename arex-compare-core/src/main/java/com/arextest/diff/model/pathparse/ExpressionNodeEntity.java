package com.arextest.diff.model.pathparse;

import com.arextest.diff.model.pathparse.expression.PathExpression;

public class ExpressionNodeEntity {

  private String nodeName;
  private int index;
  private PathExpression expression;

  /**
   * @see ExpressionNodeType
   */
  private int nodeType;

  public ExpressionNodeEntity() {

  }

  public ExpressionNodeEntity(String nodeName, int nodeType) {
    this.nodeName = nodeName;
    this.nodeType = nodeType;
  }

  public ExpressionNodeEntity(int index, int nodeType) {
    this.index = index;
    this.nodeType = nodeType;
  }

  public ExpressionNodeEntity(PathExpression expression, int nodeType) {
    this.expression = expression;
    this.nodeType = nodeType;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public PathExpression getExpression() {
    return expression;
  }

  public void setExpression(PathExpression expression) {
    this.expression = expression;
  }

  public int getNodeType() {
    return nodeType;
  }

  public void setNodeType(int nodeType) {
    this.nodeType = nodeType;
  }
}
