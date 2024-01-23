package com.arextest.diff.compare.feature;

import com.arextest.diff.model.compare.CompareContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrimitiveArrayIndexSelector implements IndexSelector {

  private Map<JsonNode, List<Integer>> leftIndexKeys;
  private Map<JsonNode, List<Integer>> rightIndexKeys;

  public PrimitiveArrayIndexSelector() {
  }

  public PrimitiveArrayIndexSelector(CompareContext compareContext) {
    this.leftIndexKeys = buildIndexKeys((ArrayNode) compareContext.currentBaseObj);
    this.rightIndexKeys = buildIndexKeys((ArrayNode) compareContext.currentTestObj);
  }

  @Override
  public int findCorrespondLeftIndex(int curRightIndex, List<Integer> leftComparedIndex,
      ArrayNode obj1Array, ArrayNode obj2Array) {
    JsonNode jsonNode = obj2Array.get(curRightIndex);
    if (leftIndexKeys.containsKey(jsonNode)) {
      List<Integer> indexCollection = leftIndexKeys.get(jsonNode);
      for (Integer index : indexCollection) {
        if (!leftComparedIndex.contains(index)) {
          return index;
        }
      }
    }
    return -1;
  }

  @Override
  public int findCorrespondRightIndex(int curLeftIndex, List<Integer> rightComparedIndex,
      ArrayNode obj1Array, ArrayNode obj2Array) {
    JsonNode jsonNode = obj1Array.get(curLeftIndex);
    if (rightIndexKeys.containsKey(jsonNode)) {
      List<Integer> indexCollection = rightIndexKeys.get(jsonNode);
      for (Integer index : indexCollection) {
        if (!rightComparedIndex.contains(index)) {
          return index;
        }
      }
    }
    return -1;
  }

  @Override
  public String judgeLeftIndexStandard(int leftIndex) {
    return indexKey(leftIndex);
  }

  @Override
  public String judgeRightIndexStandard(int rightIndex) {
    return indexKey(rightIndex);
  }

  private Map<JsonNode, List<Integer>> buildIndexKeys(ArrayNode arrayNode) {
    Map<JsonNode, List<Integer>> result = new HashMap<>();
    for (int i = 0; i < arrayNode.size(); i++) {
      JsonNode jsonNode = arrayNode.get(i);
      if (result.containsKey(jsonNode)) {
        result.get(jsonNode).add(i);
      } else {
        List<Integer> indexCollection = new ArrayList<>();
        indexCollection.add(i);
        result.put(jsonNode, indexCollection);
      }
    }
    return result;
  }

  private String indexKey(int index) {
    return "Index:[" + index + "]";
  }


}
