package com.arextest.diff.compare.feature;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.register.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Map;
import java.util.Set;

public class ListKeyIndexSelector implements IndexSelector {

  private Map<Integer, String> indexKeysLeft;
  private Map<Integer, String> indexKeysRight;
  private CompareContext compareContext;

  public ListKeyIndexSelector() {
  }

  public ListKeyIndexSelector(Map<Integer, String> indexKeysLeft,
      Map<Integer, String> indexKeysRight, CompareContext compareContext) {
    this.indexKeysLeft = indexKeysLeft;
    this.indexKeysRight = indexKeysRight;
    this.compareContext = compareContext;
  }

  @Override
  public int findCorrespondLeftIndex(int curRightIndex, Set<Integer> leftComparedIndexes,
      ArrayNode obj1Array, ArrayNode obj2Array) throws Exception {
    int correspondLeftIndex = -1;
    if (indexKeysRight != null) {
      String rightKey = indexKeysRight.get(curRightIndex);

      int cnt = 0;
      boolean alreadyFind = false;
      if (indexKeysLeft != null) {
        for (Map.Entry<Integer, String> entry : indexKeysLeft.entrySet()) {
          if (rightKey.equals(entry.getValue())) {
            cnt++;
            if (correspondLeftIndex == -1 && !leftComparedIndexes.contains(entry.getKey())
                && !alreadyFind) {
              correspondLeftIndex = entry.getKey();
              alreadyFind = true;
            }
          }
        }
      }

      if (cnt > 1) {
        LogRegister.register(correspondLeftIndex == -1 ? null : obj1Array.get(correspondLeftIndex),
            obj2Array.get(curRightIndex),
            LogMarker.REPEAT_RIGHT_KEY, compareContext);
      }
    }
    return correspondLeftIndex;
  }

  @Override
  public int findCorrespondRightIndex(int curLeftIndex, Set<Integer> rightComparedIndexes,
      ArrayNode obj1Array, ArrayNode obj2Array) throws Exception {
    // when indexKeyLef is null, obj1Array must be empty. but the indexKeyLeft also judge null;
    int correspondRightIndex = -1;
    if (indexKeysLeft != null) {
      String leftKey = indexKeysLeft.get(curLeftIndex);

      int cnt = 0;
      boolean alreadyFind = false;
      if (indexKeysRight != null) {
        for (Map.Entry<Integer, String> entry : indexKeysRight.entrySet()) {
          if (leftKey.equals(entry.getValue())) {
            cnt++;
            if (correspondRightIndex == -1 && !rightComparedIndexes.contains(entry.getKey())
                && !alreadyFind) {
              correspondRightIndex = entry.getKey();
              alreadyFind = true;
            }
          }
        }


      }

      if (cnt > 1) {
        LogRegister.register(obj1Array.get(curLeftIndex),
            correspondRightIndex == -1 ? null : obj2Array.get(correspondRightIndex),
            LogMarker.REPEAT_LEFT_KEY, compareContext);
      }
    }
    return correspondRightIndex;
  }

  @Override
  public String judgeLeftIndexStandard(int curLeftIndex) {
    if (indexKeysLeft == null) {
      return indexKey(curLeftIndex);
    }
    return indexKeysLeft.get(curLeftIndex);
  }

  @Override
  public String judgeRightIndexStandard(int curRightIndex) {
    if (indexKeysRight == null) {
      return indexKey(curRightIndex);
    }
    return indexKeysRight.get(curRightIndex);
  }

  private String indexKey(int index) {
    return "Index:[" + index + "]";
  }
}
