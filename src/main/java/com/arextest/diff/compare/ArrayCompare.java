package com.arextest.diff.compare;

import com.arextest.diff.compare.feature.IndexSelector;
import com.arextest.diff.compare.feature.IndexSelectorFactory;
import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.compare.IndexPair;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rchen9 on 2022/7/25.
 */
public class ArrayCompare {

    public static void arrayCompare(Object obj1, Object obj2, CompareContext compareContext) {

        ArrayNode obj1Array = (ArrayNode) obj1;
        ArrayNode obj2Array = (ArrayNode) obj2;

        if (obj1Array.size() != obj2Array.size()) {
            LogRegister.register(obj1Array.size(), obj2Array.size(), LogMarker.DIFF_ARRAY_COUNT, compareContext);
        }

        List<Integer> leftComparedIndexes = new ArrayList<>();
        List<Integer> rightComparedIndexes = new ArrayList<>();


        // decide to use which indexSelector
        IndexSelector indexSelector = IndexSelectorFactory.getIndexSelector(compareContext.currentNodeLeft, compareContext.currentNodeRight, compareContext);

        String currentListPath = ListUti.convertPathToStringForShow(compareContext.currentNodeLeft);

        for (int i = 0; i < obj1Array.size(); i++) {
            leftComparedIndexes.add(i);

            compareContext.currentNodeLeft.add(new NodeEntity(null, i));
            compareContext.currentListKeysLeft.add(currentListPath);
            compareContext.currentListKeysLeft.add(indexSelector.judgeLeftIndexStandard(i));

            int correspondRightIndex = indexSelector.findCorrespondRightIndex(i, rightComparedIndexes, obj1Array, obj2Array);

            Object element1 = obj1Array.get(i);
            Object element2 = null;
            boolean rightExist = false;

            if (correspondRightIndex == -1) {
                LogRegister.register(element1, element2, LogMarker.RIGHT_ARRAY_MISSING_KEY, compareContext);
            } else {
                element2 = obj2Array.get(correspondRightIndex);
                rightExist = true;
                rightComparedIndexes.add(correspondRightIndex);

                compareContext.currentNodeRight.add(new NodeEntity(null, correspondRightIndex));
                compareContext.currentListKeysRight.add(currentListPath);
                compareContext.currentListKeysRight.add(indexSelector.judgeRightIndexStandard(correspondRightIndex));

                boolean needCompare = !isComparedByRefer(i, correspondRightIndex, compareContext.pkListIndexPair, compareContext.currentNodeLeft);
                if (needCompare && element1 != null && element2 != null) {
                    GenericCompare.jsonCompare(element1, element2, compareContext);
                }
            }
            ListUti.removeLast(compareContext.currentListKeysLeft);
            ListUti.removeLast(compareContext.currentListKeysLeft);
            ListUti.removeLast(compareContext.currentNodeLeft);
            if (rightExist) {
                ListUti.removeLast(compareContext.currentListKeysRight);
                ListUti.removeLast(compareContext.currentListKeysRight);
                ListUti.removeLast(compareContext.currentNodeRight);
            }
        }

        for (int i = 0; i < obj2Array.size(); i++) {
            if (rightComparedIndexes.contains(i)) {
                continue;
            }
            compareContext.currentNodeRight.add(new NodeEntity(null, i));
            compareContext.currentListKeysRight.add(currentListPath);
            compareContext.currentListKeysRight.add(indexSelector.judgeRightIndexStandard(i));

            int correspondLeftIndex = indexSelector.findCorrespondLeftIndex(i, leftComparedIndexes, obj1Array, obj2Array);

            Object element1 = null;
            Object element2 = obj2Array.get(i);
            boolean leftExist = false;
            if (correspondLeftIndex == -1) {
                LogRegister.register(element1, element2, LogMarker.LEFT_ARRAY_MISSING_KEY, compareContext);
            } else {
                element1 = obj1Array.get(correspondLeftIndex);
                leftExist = true;
                compareContext.currentListKeysLeft.add(currentListPath);
                compareContext.currentListKeysLeft.add(indexSelector.judgeLeftIndexStandard(correspondLeftIndex));
                compareContext.currentNodeLeft.add(new NodeEntity(null, correspondLeftIndex));

                boolean needCompare = !isComparedByRefer(correspondLeftIndex, i, compareContext.pkListIndexPair, compareContext.currentNodeRight);
                if (needCompare && element1 != null && element2 != null) {
                    GenericCompare.jsonCompare(element1, element2, compareContext);
                }
            }

            if (leftExist) {
                ListUti.removeLast(compareContext.currentListKeysLeft);
                ListUti.removeLast(compareContext.currentListKeysLeft);
                ListUti.removeLast(compareContext.currentNodeLeft);
            }
            ListUti.removeLast(compareContext.currentListKeysRight);
            ListUti.removeLast(compareContext.currentListKeysRight);
            ListUti.removeLast(compareContext.currentNodeRight);
        }
    }

    private static boolean isComparedByRefer(int leftIndex, int rightIndex, Map<List<String>, List<IndexPair>> pkListIndexPair, List<NodeEntity> currentListNode) {
        boolean result = false;
        IndexPair indexPair = new IndexPair(leftIndex, rightIndex);
        List<IndexPair> comparedIndexPairs = pkListIndexPair.get(ListUti.convertToStringList(currentListNode));
        if (comparedIndexPairs != null && comparedIndexPairs.contains(indexPair)) {
            result = true;
        } else if (comparedIndexPairs != null) {
            comparedIndexPairs.add(indexPair);
        }
        return result;
    }
}
