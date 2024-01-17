package com.arextest.diff.model.pathparse;

import com.arextest.diff.model.pathparse.expression.PathExpression;
import java.util.Objects;

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

  @Override
  public int hashCode() {
    int result = index;
    result = 31 * result + (nodeName != null ? nodeName.hashCode() : 0) + (
        expression != null ? expression.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ExpressionNodeEntity)) {
      return false;
    }

    ExpressionNodeEntity that = (ExpressionNodeEntity) obj;
    return Objects.equals(this.getNodeName(), that.getNodeName())
        && this.getIndex() == that.getIndex()
        && Objects.equals(this.getExpression(), that.getExpression());
  }
}
