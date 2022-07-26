package com.arextest.diff.compare;

import com.arextest.diff.model.compare.IndexPair;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import com.arextest.diff.utils.StringUtil;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arextest.diff.compare.CompareHelper.*;

/**
 * Created by rchen9 on 2022/7/25.
 */
public class ArrayCompare {

    public static void arrayCompare(Object obj1, Object obj2, CompareContext compareContext) throws JSONException {

        List<NodeEntity> currentNodeLeft = compareContext.getCurrentNodeLeft();
        List<NodeEntity> currentNodeRight = compareContext.getCurrentNodeRight();
        List<String> currentListKeysLeft = compareContext.getCurrentListKeysLeft();
        List<String> currentListKeysRight = compareContext.getCurrentListKeysRight();
        HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = compareContext.getListIndexKeysLeft();
        HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = compareContext.getListIndexKeysRight();
        Map<List<String>, List<IndexPair>> pkListIndexPair = compareContext.getPkListIndexPair();
        List<List<NodeEntity>> listKeyPath = compareContext.getListKeyPath();
        List<ReferenceEntity> responseReferences = compareContext.getResponseReferences();
        boolean notDistinguishNullAndEmpty = compareContext.isNotDistinguishNullAndEmpty();
        List<LogEntity> logs = compareContext.getLogs();

        JSONArray obj1Array = (JSONArray) obj1;
        JSONArray obj2Array = (JSONArray) obj2;

        if (obj1Array.length() != obj2Array.length()) {
            LogEntity log = new LogEntity(obj1Array.length(), obj2Array.length(), getUnmatchedPair(UnmatchedType.DIFFERENT_COUNT, compareContext)
                    .setListKeys(currentListKeysLeft));
            logs.add(log);
        }

        List<Integer> usedIndexes = new ArrayList<>();
        List<Integer> leftComparedIndexes = new ArrayList<>();
        Map<Integer, String> indexKeysLeft = listIndexKeysLeft.get(currentNodeLeft);
        Map<Integer, String> indexKeysRight = listIndexKeysRight.get(currentNodeRight);

        String currentListPath = ListUti.convertPathToStringForShow(currentNodeLeft);

        // compare by index
        if (indexKeysLeft == null) {
            for (int i = 0; i < obj1Array.length(); i++) {
                IndexPair indexPair = new IndexPair(i, i);
                List<IndexPair> comparedIndexPairs = pkListIndexPair.get(ListUti.convertToStringList(currentNodeLeft));
                if (comparedIndexPairs != null && comparedIndexPairs.contains(indexPair)) {
                    continue;
                } else if (comparedIndexPairs != null) {
                    comparedIndexPairs.add(indexPair);
                }

                currentListKeysLeft.add(currentListPath);
                currentListKeysLeft.add(indexKey(i));
                listKeyPath.add(addListKeyPath(currentNodeLeft, i));
                currentNodeLeft.add(new NodeEntity(null, i));

                Object element1 = obj1Array.get(i);
                Object element2 = null;
                boolean rightExist = false;
                try {
                    element2 = obj2Array.get(i);
                    rightExist = true;
                    usedIndexes.add(i);

                    currentNodeRight.add(new NodeEntity(null, i));
                    currentListKeysRight.add(currentListPath);
                    currentListKeysRight.add(indexKey(i));
                } catch (JSONException e) {
                    if (!notDistinguishNullAndEmpty || !StringUtil.jsonEmptyJudge(element1)) {
                        LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.RIGHT_MISSING, compareContext)
                                .setListKeys(currentListKeysLeft));
                        List<ReferenceEntity> references = findReferenceNode(currentNodeLeft, responseReferences);
                        if (!references.isEmpty()) {
                            List<NodeEntity> pkNodePath = getPkNodePath(references, true, element1, compareContext);
                            if (pkNodePath != null) {
                                log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
                            }
                        }
                        logs.add(log);
                    }
                }
                if (element1 != null && element2 != null) {
                    GenericCompare.jsonCompare(element1, element2, compareContext);
                }

                ListUti.removeLast(currentListKeysLeft);
                ListUti.removeLast(currentListKeysLeft);
                ListUti.removeLast(listKeyPath);

                ListUti.removeLast(currentNodeLeft);
                if (rightExist) {
                    ListUti.removeLast(currentListKeysRight);
                    ListUti.removeLast(currentListKeysRight);
                    ListUti.removeLast(currentNodeRight);
                }
            }
        } else {
            for (int i = 0; i < obj1Array.length(); i++) {
                leftComparedIndexes.add(i);

                String leftKey = indexKeysLeft.get(i);
                if (leftKey == null) {
                    continue;
                }
                currentNodeLeft.add(new NodeEntity(null, i));
                currentListKeysLeft.add(currentListPath);
                currentListKeysLeft.add(leftKey);
                listKeyPath.add(new ArrayList<>(currentNodeLeft));

                int correspondRightIndex = -1;
                int cnt = 0;
                boolean alreadyFind = false;

                if (indexKeysRight != null) {
                    for (Integer index : indexKeysRight.keySet()) {
                        if (leftKey.equals(indexKeysRight.get(index))) {
                            cnt++;
                            if (correspondRightIndex == -1 && !usedIndexes.contains(index) && !alreadyFind) {
                                correspondRightIndex = index;
                                alreadyFind = true;
                            }
                        }
                    }
                }

                Object element1 = obj1Array.get(i);
                Object element2 = null;

                boolean rightExist = false;
                if (cnt > 1) {
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.NOT_UNIQUE, compareContext)
                            .setListKeys(currentListKeysLeft));
                    log.setWarn(1);
                    addReferencePath(log, true, element1, compareContext);
                    logs.add(log);
                }
                if (correspondRightIndex == -1) {
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.RIGHT_MISSING, compareContext)
                            .setListKeys(currentListKeysLeft));

                    List<ReferenceEntity> references = findReferenceNode(currentNodeLeft, responseReferences);
                    if (!references.isEmpty()) {
                        List<NodeEntity> pkNodePath = getPkNodePath(references, true, element1, compareContext);
                        if (pkNodePath != null) {
                            log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
                        }
                    }
                    logs.add(log);

                } else {
                    element2 = obj2Array.get(correspondRightIndex);
                    rightExist = true;
                    usedIndexes.add(correspondRightIndex);

                    currentNodeRight.add(new NodeEntity(null, correspondRightIndex));
                    currentListKeysRight.add(currentListPath);
                    currentListKeysRight.add(leftKey);

                    boolean needCompare = true;
                    IndexPair indexPair = new IndexPair(i, correspondRightIndex);
                    List<IndexPair> comparedIndexPairs = pkListIndexPair.get(ListUti.convertToStringList(currentNodeLeft));
                    if (comparedIndexPairs != null && comparedIndexPairs.contains(indexPair)) {
                        needCompare = false;
                    } else if (comparedIndexPairs != null) {
                        comparedIndexPairs.add(indexPair);
                    }

                    if (needCompare && element1 != null && element2 != null) {
                        GenericCompare.jsonCompare(element1, element2, compareContext);
                    }
                }

                ListUti.removeLast(currentListKeysLeft);
                ListUti.removeLast(currentListKeysLeft);
                ListUti.removeLast(currentNodeLeft);
                ListUti.removeLast(listKeyPath);
                if (rightExist) {
                    ListUti.removeLast(currentListKeysRight);
                    ListUti.removeLast(currentListKeysRight);
                    ListUti.removeLast(currentNodeRight);
                }
            }
        }

        if (indexKeysRight == null) {
            for (int i = 0; i < obj2Array.length(); i++) {
                if (usedIndexes.contains(i)) {
                    continue;
                }
                IndexPair indexPair = new IndexPair(i, i);
                List<IndexPair> comparedIndexPairs = pkListIndexPair.get(ListUti.convertToStringList(currentNodeRight));
                if (comparedIndexPairs != null && comparedIndexPairs.contains(indexPair)) {
                    continue;
                } else if (comparedIndexPairs != null) {
                    comparedIndexPairs.add(indexPair);
                }

                currentNodeRight.add(new NodeEntity(null, i));
                currentListKeysRight.add(currentListPath);
                currentListKeysRight.add(indexKey(i));
                listKeyPath.add(addListKeyPath(currentNodeRight, i));

                Object element1 = null;
                Object element2 = obj2Array.get(i);

                boolean leftExist = false;
                try {
                    element1 = obj1Array.get(i);
                    leftExist = true;
                    currentNodeLeft.add(new NodeEntity(null, i));
                    currentListKeysLeft.add(currentListPath);
                    currentListKeysLeft.add(indexKey(i));
                } catch (JSONException e) {
                    if (!notDistinguishNullAndEmpty || !StringUtil.jsonEmptyJudge(element2)) {
                        LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.LEFT_MISSING, compareContext)
                                .setListKeys(currentListKeysRight));

                        List<ReferenceEntity> references = findReferenceNode(currentNodeRight, responseReferences);
                        if (!references.isEmpty()) {
                            List<NodeEntity> pkNodePath = getPkNodePath(references, false, element2, compareContext);
                            if (pkNodePath != null) {
                                log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
                            }
                        }
                        logs.add(log);
                    }
                }
                if (element1 != null && element2 != null)
                    GenericCompare.jsonCompare(element1, element2, compareContext);

                if (leftExist) {
                    ListUti.removeLast(currentListKeysLeft);
                    ListUti.removeLast(currentListKeysLeft);
                    ListUti.removeLast(currentNodeLeft);
                }

                ListUti.removeLast(currentListKeysRight);
                ListUti.removeLast(currentListKeysRight);
                ListUti.removeLast(currentNodeRight);
                ListUti.removeLast(listKeyPath);
            }

        } else {
            for (int i = 0; i < obj2Array.length(); i++) {
                if (usedIndexes.contains(i)) continue;
                String rightKey = indexKeysRight.get(i);
                if (rightKey == null) {
                    continue;
                }
                currentNodeRight.add(new NodeEntity(null, i));
                currentListKeysRight.add(currentListPath);
                currentListKeysRight.add(rightKey);
                listKeyPath.add(new ArrayList<>(currentNodeLeft));

                int correspondLeftIndex = -1;
                int cnt = 0;
                boolean alreadyFind = false;
                if (indexKeysLeft != null) {
                    for (Integer index : indexKeysLeft.keySet()) {
                        if (rightKey.equals(indexKeysLeft.get(index))) {
                            cnt++;
                            if (correspondLeftIndex == -1 && !leftComparedIndexes.contains(index) && !alreadyFind) {
                                correspondLeftIndex = index;
                                alreadyFind = true;
                            }
                        }
                    }
                }

                Object element1 = null;
                Object element2 = obj2Array.get(i);

                boolean leftExist = false;
                if (cnt > 1) {
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.NOT_UNIQUE, compareContext)
                            .setListKeys(currentListKeysRight));
                    log.setWarn(1);
                    addReferencePath(log, false, element2, compareContext);
                    logs.add(log);
                }
                if (correspondLeftIndex == -1) {
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.LEFT_MISSING, compareContext)
                            .setListKeys(currentListKeysRight));

                    List<ReferenceEntity> references = findReferenceNode(currentNodeRight, responseReferences);
                    if (!references.isEmpty()) {
                        List<NodeEntity> pkNodePath = getPkNodePath(references, false, element2, compareContext);
                        if (pkNodePath != null) {
                            log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
                        }
                    }
                    logs.add(log);
                } else {
                    element1 = obj1Array.get(correspondLeftIndex);
                    leftExist = true;
                    currentListKeysLeft.add(currentListPath);
                    currentListKeysLeft.add(rightKey);
                    currentNodeLeft.add(new NodeEntity(null, correspondLeftIndex));

                    boolean needCompare = true;
                    IndexPair indexPair = new IndexPair(correspondLeftIndex, i);
                    List<IndexPair> comparedIndexPairs = pkListIndexPair.get(ListUti.convertToStringList(currentNodeRight));
                    if (comparedIndexPairs != null && comparedIndexPairs.contains(indexPair)) {
                        needCompare = false;
                    } else if (comparedIndexPairs != null) {
                        comparedIndexPairs.add(indexPair);
                    }

                    if (needCompare && element1 != null && element2 != null) {
                        GenericCompare.jsonCompare(element1, element2, compareContext);
                    }
                }

                if (leftExist) {
                    ListUti.removeLast(currentListKeysLeft);
                    ListUti.removeLast(currentListKeysLeft);
                    ListUti.removeLast(currentNodeLeft);
                }

                ListUti.removeLast(currentListKeysRight);
                ListUti.removeLast(currentListKeysRight);
                ListUti.removeLast(currentNodeRight);
                ListUti.removeLast(listKeyPath);
            }
        }
    }

    private static List<NodeEntity> getPkNodePath(List<ReferenceEntity> references, boolean isLeft, Object obj, CompareContext compareContext) throws JSONException {
        for (ReferenceEntity reference : references) {
            List<String> pkNodeListPath = reference.getPkNodeListPath();
            List<String> pkNodePath = reference.getPkNodePath();

            Object refList;
            Map<List<String>, Object> refPkListNodeCache = isLeft ? compareContext.getRefPkListNodeCacheLeft() : compareContext.getRefPkListNodeCacheRight();

            if (refPkListNodeCache.containsKey(pkNodeListPath)) {
                refList = refPkListNodeCache.get(pkNodeListPath);
            } else {
                refList = findByPath(isLeft ? compareContext.getBaseObj() : compareContext.getTestObj(), pkNodeListPath);
                refPkListNodeCache.put(pkNodeListPath, refList);
            }

            if (refList instanceof JSONArray) {
                JSONArray array = ((JSONArray) refList);
                for (int i = 0; i < array.length(); i++) {
                    Object element = array.get(i);
                    Object pkNodeValue = findByPath(element, pkNodePath.subList(pkNodeListPath.size(), pkNodePath.size()));
                    if (String.valueOf(obj).equals(String.valueOf(pkNodeValue))) {
                        List<NodeEntity> list = convertToNodeEntityList(pkNodeListPath);
                        list.add(new NodeEntity(null, i));
                        return list;
                    }
                }
            }
        }
        return null;
    }

    private static String indexKey(int index) {
        return "Index:[" + index + "]";
    }

    private static void addReferencePath(LogEntity log, boolean left, Object obj, CompareContext compareContext) throws JSONException {
        List<ReferenceEntity> references = findReferenceNode(left ? compareContext.getCurrentNodeLeft() : compareContext.getCurrentNodeRight(), compareContext.getResponseReferences());
        if (!references.isEmpty()) {
            List<NodeEntity> pkNodePath = getPkNodePath(references, left, obj, compareContext);
            if (pkNodePath != null) {
                if (left) {
                    log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
                } else {
                    log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
                }
            }
        }
    }

    private static List<NodeEntity> addListKeyPath(List<NodeEntity> nodePath, Integer index) {
        List<NodeEntity> listKeyPath = new ArrayList<>(nodePath);
        listKeyPath.add(new NodeEntity(null, index));
        return listKeyPath;
    }
}
