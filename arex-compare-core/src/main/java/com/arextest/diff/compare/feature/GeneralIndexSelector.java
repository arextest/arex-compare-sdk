package com.arextest.diff.compare.feature;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;

public class GeneralIndexSelector implements IndexSelector {

  @Override
  public int findCorrespondLeftIndex(int curRightIndex, List<Integer> leftComparedIndex,
      ArrayNode obj1Array, ArrayNode obj2Array) {
    if (obj1Array == null || obj1Array.isEmpty()) {
      return -1;
    }
    if (curRightIndex >= obj1Array.size()) {
      return -1;
    }
    return curRightIndex;
  }

  @Override
  public int findCorrespondRightIndex(int curLeftIndex, List<Integer> rightComparedIndex,
      ArrayNode obj1Array, ArrayNode obj2Array) {
    if (obj2Array == null || obj2Array.isEmpty()) {
      return -1;
    }
    if (curLeftIndex >= obj2Array.size()) {
      return -1;
    }
    return curLeftIndex;
  }

  @Override
  public String judgeLeftIndexStandard(int leftIndex) {
    return indexKey(leftIndex);
  }

  @Override
  public String judgeRightIndexStandard(int rightIndex) {
    return indexKey(rightIndex);
  }

  private String indexKey(int index) {
    return "Index:[" + index + "]";
  }
}
