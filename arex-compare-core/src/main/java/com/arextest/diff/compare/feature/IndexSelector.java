package com.arextest.diff.compare.feature;

import com.arextest.diff.model.exception.FindErrorException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;

public interface IndexSelector {

  int findCorrespondLeftIndex(int curRightIndex, List<Integer> leftComparedIndex,
      ArrayNode obj1Array, ArrayNode obj2Array) throws FindErrorException, Exception;

  int findCorrespondRightIndex(int curLeftIndex, List<Integer> rightComparedIndex,
      ArrayNode obj1Array, ArrayNode obj2Array) throws Exception;

  String judgeLeftIndexStandard(int leftIndex);

  String judgeRightIndexStandard(int rightIndex);
}
