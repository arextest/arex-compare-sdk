package com.arextest.diff.compare.feature;

import org.json.JSONArray;

import java.util.List;

public interface IndexSelector {
    int findCorrespondLeftIndex(int curRightIndex, List<Integer> leftComparedIndex, JSONArray obj1Array, JSONArray obj2Array);

    int findCorrespondRightIndex(int curLeftIndex, List<Integer> rightComparedIndex, JSONArray obj1Array, JSONArray obj2Array);

    String judgeLeftIndexStandard(int leftIndex);

    String judgeRightIndexStandard(int rightIndex);
}
