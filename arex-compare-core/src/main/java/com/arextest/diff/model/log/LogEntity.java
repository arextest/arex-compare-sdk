package com.arextest.diff.model.log;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.Serializable;
import java.util.List;

public class LogEntity implements Serializable {

  /**
   * baseValue and testValue will only have values ​​at leaf nodes, and non-leaf nodes are empty The
   * final output baseValue and testValue are String
   */
  private Object baseValue;
  private Object testValue;
  // log information
  private String logInfo;
  // error path information
  private UnmatchedPairEntity pathPair;
  // reference function, upper primary key path of basic msg
  private String addRefPkNodePathLeft;
  // reference function, upper primary key path of test msg
  private String addRefPkNodePathRight;
  // the additional information about error node
  private LogTag logTag = new LogTag();
  private int warn;

  public LogEntity(String logInfo) {
    this.logInfo = logInfo;
    this.pathPair = new UnmatchedPairEntity();
    this.pathPair.setUnmatchedType(UnmatchedType.OTHERS);
  }

  public LogEntity() {

  }

  public LogEntity(Object baseValue, Object testValue, UnmatchedPairEntity pathPair) {
    this.baseValue = valueToString(baseValue);
    this.testValue = valueToString(testValue);
    this.pathPair = pathPair;
    processLogInfo(this.baseValue, this.testValue, pathPair.getUnmatchedType());
  }

  static String getNodeName(List<NodeEntity> unmatchedPath) {
    String nodeName = "";
    if (unmatchedPath.size() > 0) {
      int i = unmatchedPath.size() - 1;
      nodeName = unmatchedPath.get(i).getNodeName();
      if (nodeName == null) {
        if (i - 1 >= 0) {
          nodeName =
              unmatchedPath.get(i - 1).getNodeName() + "[" + unmatchedPath.get(i).getIndex() + "]";
        } else {
          nodeName = "[" + unmatchedPath.get(i).getIndex() + "]";
        }
      }
    }
    return nodeName;
  }

  public Object getBaseValue() {
    return baseValue;
  }

  public void setBaseValue(Object baseValue) {
    this.baseValue = baseValue;
  }

  public Object getTestValue() {
    return testValue;
  }

  public void setTestValue(Object testValue) {
    this.testValue = testValue;
  }

  public String getLogInfo() {
    return logInfo;
  }

  public void setLogInfo(String logInfo) {
    this.logInfo = logInfo;
  }

  public UnmatchedPairEntity getPathPair() {
    return pathPair;
  }

  public void setPathPair(UnmatchedPairEntity pathPair) {
    this.pathPair = pathPair;
  }

  public String getAddRefPkNodePathLeft() {
    return addRefPkNodePathLeft;
  }

  public void setAddRefPkNodePathLeft(String addRefPkNodePathLeft) {
    this.addRefPkNodePathLeft = addRefPkNodePathLeft;
  }

  public String getAddRefPkNodePathRight() {
    return addRefPkNodePathRight;
  }

  public void setAddRefPkNodePathRight(String addRefPkNodePathRight) {
    this.addRefPkNodePathRight = addRefPkNodePathRight;
  }

  // public String getPath() {
  //     return path;
  // }
  //
  // public void setPath(String path) {
  //     this.path = path;
  // }
  //
  // public String getLeftPath() {
  //     return leftPath;
  // }
  //
  // public void setLeftPath(String leftPath) {
  //     this.leftPath = leftPath;
  // }
  //
  // public String getRightPath() {
  //     return rightPath;
  // }
  //
  // public void setRightPath(String rightPath) {
  //     this.rightPath = rightPath;
  // }

  public int getWarn() {
    return warn;
  }

  public void setWarn(int warn) {
    this.warn = warn;
  }

  public LogTag getLogTag() {
    return logTag;
  }

  public void setLogTag(LogTag logTag) {
    this.logTag = logTag;
  }

  // private void processPath() {
  //     if (this.pathPair != null) {
  //         if (this.pathPair.getLeftUnmatchedPath().size() >= this.pathPair.getRightUnmatchedPath().size()) {
  //             this.path = ListUti.convertPathToStringForShow(this.pathPair.getLeftUnmatchedPath());
  //         } else {
  //             this.path = ListUti.convertPathToStringForShow(this.pathPair.getRightUnmatchedPath());
  //         }
  //         this.leftPath = ListUti.convertPathToStringForShow(this.pathPair.getLeftUnmatchedPath());
  //         this.rightPath = ListUti.convertPathToStringForShow(this.pathPair.getRightUnmatchedPath());
  //     }
  // }

  private String valueToString(Object value) {
    if (value instanceof NullNode || value == null) {
      return null;
    }
    if (value instanceof ObjectNode || value instanceof ArrayNode) {
      return null;
    }
    if (value instanceof JsonNode) {
      return ((JsonNode) value).asText();
    }
    return value.toString();
  }

  private void processLogInfo(Object baseValue, Object testValue, int unmatchedType) {
    List<NodeEntity> leftUnmatchedPath = pathPair.getLeftUnmatchedPath();
    List<NodeEntity> rightUnmatchedPath = pathPair.getRightUnmatchedPath();
    String leftNodeName = getNodeName(leftUnmatchedPath);
    String rightNodeName = getNodeName(rightUnmatchedPath);

    switch (unmatchedType) {
      case UnmatchedType.UNMATCHED:
        logInfo = String.format("The node value of [%s] is different : {%s} - {%s}", leftNodeName,
            baseValue, testValue);
        break;
      case UnmatchedType.LEFT_MISSING:
        logInfo = String.format("There is more node on the right : [%s]", rightNodeName);
        break;
      case UnmatchedType.RIGHT_MISSING:
        logInfo = String.format("There is more node on the left : [%s]", leftNodeName);
        break;
      case UnmatchedType.DIFFERENT_COUNT:
        logInfo = String.format("The Number of the node [%s] is different : {%s} - {%s}",
            leftNodeName, baseValue, testValue);
        break;
      case UnmatchedType.NOT_SUPPORT:
        logInfo = String.format("Not support the comparision of the node [%s]", leftNodeName);
        break;
      case UnmatchedType.NOT_UNIQUE:
        logInfo = String.format("The node [%s] on which the key is set is not unique",
            leftNodeName);
        break;
      case UnmatchedType.REFERENCE_NOT_FOUND:
        logInfo = String.format("Not find the reference node for [%s] = {%s}", baseValue,
            testValue);
        break;
      case UnmatchedType.NOT_EXPECT_VALUE:
        logInfo = String.format("Expected value: {%s}, Actual value: {%s}", testValue, baseValue);
        break;
      case UnmatchedType.EXPECT_NOT_NULL:
        logInfo = String.format("The node [%s] is NULL",
            (pathPair.getLeftUnmatchedPath() == null || pathPair.getLeftUnmatchedPath().size() == 0)
                ? rightNodeName : leftNodeName);
        break;
      case UnmatchedType.NOT_EXPECT_LIST_COUNT:
        logInfo = String.format("The number of List nodes [%s] is less than {%s}",
            (pathPair.getLeftUnmatchedPath() == null || pathPair.getLeftUnmatchedPath().size() == 0)
                ? rightNodeName : leftNodeName, testValue);
        break;
      case UnmatchedType.DIFFERENT_TYPE:
        logInfo = String.format("The Node [%s] is of different type {%s} - {%s}", leftNodeName,
            baseValue, testValue);
        break;
      default:
        logInfo = "";
        break;
    }
  }

  @Override
  public String toString() {
    if (this.pathPair == null) {
      return logInfo;
    }
    String leftPath = ListUti.convertPathToStringForShow(this.pathPair.getLeftUnmatchedPath());
    String rightPath = ListUti.convertPathToStringForShow(this.pathPair.getRightUnmatchedPath());
    return logInfo + ";  " + "Left node path: " + leftPath + ";  " + "Right node path: "
        + rightPath;
  }
}