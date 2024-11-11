package com.arextest.diff.model.log;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.utils.ListUti;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnmatchedPairEntity implements Serializable {

  /**
   * {@link UnmatchedType}
   */
  private int unmatchedType;
  // left unmatched path, the path from the root node to the leaf node
  // if the nodeName is null, the index is the array index
  private List<NodeEntity> leftUnmatchedPath;
  private List<NodeEntity> rightUnmatchedPath;
  // the passed array node, the basis for selecting the node
  // "Index:[0]" indicates that the element of the array is selected by the index
  private List<String> listKeys;
  // Reference jumps up and down, a collection of paths passed by the reference function
  private Trace trace;

  public UnmatchedPairEntity() {
    leftUnmatchedPath = new ArrayList<>();
    rightUnmatchedPath = new ArrayList<>();
  }

  public UnmatchedPairEntity(int unmatchedType, List<NodeEntity> leftUnmatchedPath,
      List<NodeEntity> rightUnmatchedPath, Trace trace) {
    this.unmatchedType = unmatchedType;
    this.leftUnmatchedPath = deepCopy(leftUnmatchedPath);
    this.rightUnmatchedPath = deepCopy(rightUnmatchedPath);
    this.trace = trace;
  }

  public int getUnmatchedType() {
    return unmatchedType;
  }

  public void setUnmatchedType(int unmatchedType) {
    this.unmatchedType = unmatchedType;
  }

  public List<NodeEntity> getLeftUnmatchedPath() {
    return leftUnmatchedPath;
  }

  public void setLeftUnmatchedPath(List<NodeEntity> leftUnmatchedPath) {
    this.leftUnmatchedPath = leftUnmatchedPath;
  }

  public List<NodeEntity> getRightUnmatchedPath() {
    return rightUnmatchedPath;
  }

  public void setRightUnmatchedPath(List<NodeEntity> rightUnmatchedPath) {
    this.rightUnmatchedPath = rightUnmatchedPath;
  }

  public UnmatchedPairEntity buildListKeys(List<String> listKeys) {
    if (listKeys == null) {
      return this;
    }
    this.listKeys = new ArrayList<>(listKeys);
    return this;
  }

  public List<String> getListKeys() {
    return listKeys;
  }

  public void setListKeys(List<String> listKeys) {
    this.listKeys = listKeys;
  }

  public Trace getTrace() {
    return trace;
  }

  public void setTrace(Trace trace) {
    this.trace = trace;
  }

  private List<NodeEntity> deepCopy(List<NodeEntity> origList) {
    if (origList instanceof ArrayList) {
      return (List<NodeEntity>) ((ArrayList) origList).clone();

    } else {
      List<NodeEntity> nodeEntities = new ArrayList<>();
      if (origList != null) {
        for (NodeEntity node : origList) {
          nodeEntities.add(new NodeEntity(node.getNodeName(), node.getIndex()));
        }
      }
      return nodeEntities;
    }

  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UnmatchedPairEntity that = (UnmatchedPairEntity) o;
    return unmatchedType == that.unmatchedType &&
        Objects.equals(ListUti.convertToStringList(leftUnmatchedPath),
            ListUti.convertToStringList(that.leftUnmatchedPath)) &&
        Objects.equals(ListUti.convertToStringList(rightUnmatchedPath),
            ListUti.convertToStringList(that.rightUnmatchedPath));
  }

  @Override
  public int hashCode() {
    return Objects.hash(unmatchedType, ListUti.convertToStringList(leftUnmatchedPath),
        ListUti.convertToStringList(rightUnmatchedPath));
  }
}
