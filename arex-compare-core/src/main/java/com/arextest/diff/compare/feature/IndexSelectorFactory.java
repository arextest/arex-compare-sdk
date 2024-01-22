package com.arextest.diff.compare.feature;

import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.log.NodeEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IndexSelectorFactory {

  public static IndexSelector getIndexSelector(List<NodeEntity> currentNodeLeft,
      List<NodeEntity> currentNodeRight, CompareContext compareContext) {
    Map<Integer, String> indexKeysLeft = compareContext.listIndexKeysLeft.get(currentNodeLeft);
    Map<Integer, String> indexKeysRight = compareContext.listIndexKeysRight.get(currentNodeRight);

    if (indexKeysLeft == null && indexKeysRight == null) {
      return new GeneralIndexSelector();
    } else {
      return new ListKeyIndexSelector(indexKeysLeft, indexKeysRight, compareContext);
    }
  }

  private boolean isPrimitiveArrayAndEqual(CompareContext compareContext) {
    Object currentBaseObj = compareContext.currentBaseObj;
    Object currentTestObj = compareContext.currentTestObj;

    if(!(currentBaseObj instanceof ArrayNode) || !(currentTestObj instanceof ArrayNode)) {
      return false;
    }

    ArrayNode baseArray = (ArrayNode) currentBaseObj;
    ArrayNode testArray = (ArrayNode) currentTestObj;

    if(baseArray.size() != testArray.size() ) {
      return false;
    }

    if (baseArray.isEmpty()){
      return true;
    }

    if (!(baseArray.get(0).isValueNode()) || !(testArray.get(0).isValueNode()) ) {
      return false;
    }

    List<String> baseList = new ArrayList<>();
    List<String> testList = new ArrayList<>();

    for (JsonNode jsonNode: baseArray) {
      if (!jsonNode.isValueNode()) {
        return false;
      }
      baseList.add(jsonNode.asText());
    }


  }
}
