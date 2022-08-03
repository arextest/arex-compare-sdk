package com.arextest.diff.compare.feature;

import org.json.JSONArray;

import java.util.List;

public class GeneralIndexSelector implements IndexSelector {

    @Override
    public int findCorrespondLeftIndex(int curRightIndex, List<Integer> leftComparedIndex, JSONArray obj1Array, JSONArray obj2Array) {
        if (obj1Array == null || obj1Array.isEmpty()) {
            return -1;
        }
        if (curRightIndex >= obj1Array.length()) {
            return -1;
        }
        return curRightIndex;
    }

    @Override
    public int findCorrespondRightIndex(int curLeftIndex, List<Integer> rightComparedIndex, JSONArray obj1Array, JSONArray obj2Array) {
        if (obj2Array == null || obj2Array.isEmpty()) {
            return -1;
        }
        if (curLeftIndex >= obj2Array.length()) {
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
