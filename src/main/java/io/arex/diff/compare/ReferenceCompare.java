package io.arex.diff.compare;

import io.arex.diff.model.enumeration.Constant;
import io.arex.diff.model.enumeration.UnmatchedType;
import io.arex.diff.model.key.IndexKey;
import io.arex.diff.model.key.ReferenceEntity;
import io.arex.diff.model.log.LogEntity;
import io.arex.diff.model.log.NodeEntity;
import io.arex.diff.model.log.Trace;
import io.arex.diff.model.log.UnmatchedPairEntity;
import io.arex.diff.utils.KeyUtil;
import io.arex.diff.utils.ListUti;
import io.arex.diff.utils.StringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReferenceCompare {

    private Object baseObj;
    private Object testObj;

    private Map<List<String>, Object> refPkListNodeCacheLeft = new HashMap<>();
    private Map<List<String>, Object> refPkListNodeCacheRight = new HashMap<>();


    private List<String> currentListKeysLeft = new ArrayList<>();
    private List<String> currentListKeysRight = new ArrayList<>();
    // compare reference trace
    private List<List<NodeEntity>> currentTraceLeft;
    private List<List<NodeEntity>> currentTraceRight;

    private List<List<NodeEntity>> currentTraceLeftForShow;
    private List<List<NodeEntity>> currentTraceRightForShow;

    private List<NodeEntity> currentNodeLeft = new ArrayList<>();
    private List<NodeEntity> currentNodeRight = new ArrayList<>();

    private List<ReferenceEntity> responseReferences;
    private HashSet<List<String>> pkNodePaths;

    private HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = new HashMap<>();
    private HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = new HashMap<>();

    private List<LogEntity> logs;

    private List<List<NodeEntity>> listKeyPath = new ArrayList<>();

    private byte ignoreReferenceNotFound = 0;

    private boolean notDistinguishNullAndEmpty = false;

    public void setBaseObj(Object baseObj) {
        this.baseObj = baseObj;
    }

    public void setTestObj(Object testObj) {
        this.testObj = testObj;
    }

    public void setResponseReferences(List<ReferenceEntity> responseReferences) {
        this.responseReferences = responseReferences;
        HashSet<List<String>> pkNodePaths = new HashSet<>();
        pkListIndexPair = new HashMap<>();
        for (ReferenceEntity responseReference : responseReferences) {
            pkNodePaths.add(responseReference.getPkNodePath());
            pkListIndexPair.put(responseReference.getPkNodeListPath(), new ArrayList<>());
        }
        this.pkNodePaths = pkNodePaths;
    }

    public void setListIndexKeysLeft(HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft) {
        this.listIndexKeysLeft = listIndexKeysLeft;
    }

    public void setListIndexKeysRight(HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight) {
        this.listIndexKeysRight = listIndexKeysRight;
    }

    public void setLogs(List<LogEntity> logs) {
        this.logs = logs;
    }

    public void setIgnoreReferenceNotFound(byte ignoreReferenceNotFound) {
        this.ignoreReferenceNotFound = ignoreReferenceNotFound;
    }

    public void setNotDistinguishNullAndEmpty(boolean notDistinguishNullAndEmpty) {
        this.notDistinguishNullAndEmpty = notDistinguishNullAndEmpty;
    }

    public List<LogEntity> getLogs() {
        return logs;
    }


    private Map<List<String>, List<IndexPair>> pkListIndexPair;

    private static class IndexPair {
        int leftIndex;
        int rightIndex;

        public IndexPair(int leftIndex, int rightIndex) {
            this.leftIndex = leftIndex;
            this.rightIndex = rightIndex;
        }

        @Override
        public int hashCode() {
            int result = leftIndex;
            return result + 31 * rightIndex;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof IndexPair) {
                IndexPair that = (IndexPair) obj;
                return (leftIndex == that.leftIndex) && (rightIndex == that.rightIndex);
            }
            return false;
        }
    }

    public ReferenceCompare() {
        this.currentTraceLeft = Lists.newArrayList();
        this.currentTraceRight = Lists.newArrayList();
        this.currentTraceLeftForShow = Lists.newArrayList();
        this.currentTraceRightForShow = Lists.newArrayList();
    }

    public void jsonCompare(Object obj1, Object obj2) throws JSONException {

        // ignore primary key node
        if (pkNodePaths != null) {
            for (List<String> pkNodePath : pkNodePaths) {
                if (pkNodePath.equals(ListUti.convertToStringList(currentNodeLeft))) {
                    return;
                }
            }
        }

        if (Objects.equals(obj1, obj2)) {
            return;
        }

        // There is a null value in any of the left and right nodes
        if (obj1 == null || obj2 == null || JSONObject.NULL.equals(obj1) || JSONObject.NULL.equals(obj2)) {
            if (notDistinguishNullAndEmpty) {
                if (bothEmptyString(obj1, obj2)) return;
                if (obj1 == null && JSONObject.NULL.equals(obj2)) return;
                if (obj2 == null && JSONObject.NULL.equals(obj1)) return;
                if (obj1 instanceof JSONArray && ((JSONArray) obj1).length() == 0) return;
                if (obj2 instanceof JSONArray && ((JSONArray) obj2).length() == 0) return;
            }

            boolean leftNull = (obj1 == null || JSONObject.NULL.equals(obj1));
            LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(leftNull ? UnmatchedType.LEFT_MISSING : UnmatchedType.RIGHT_MISSING)
                    .setListKeys(leftNull ? currentListKeysRight : currentListKeysLeft));
            logs.add(log);
            return;
        }
        // obj1 and obj2 are different types
        if (differentType(obj1, obj2)) {
            LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(UnmatchedType.DIFFERENT_TYPE)
                    .setListKeys(currentListKeysLeft));
            logs.add(log);

            // Eliminate type inconsistencies
            obj1 = obj1.toString();
            obj2 = obj2.toString();
        }

        if (obj1 instanceof JSONObject) {
            JSONObject jsonObj1 = (JSONObject) obj1;
            JSONObject jsonObj2 = (JSONObject) obj2;

            String[] names = JSONObject.getNames(jsonObj1);
            String[] names2 = JSONObject.getNames(jsonObj2);
            if (names == null) {
                names = new String[0];
            }
            if (names2 == null) {
                names2 = new String[0];
            }

            List<String> usedFieldNames = new ArrayList<>();
            for (String fieldName : names) {
                String fieldName2 = fieldName;
                currentNodeLeft.add(new NodeEntity(fieldName, 0));

                Object obj1FieldValue, obj2FieldValue = null;
                obj1FieldValue = jsonObj1.get(fieldName);

                boolean rightExist = false;
                try {
                    obj2FieldValue = jsonObj2.get(fieldName2);
                    rightExist = true;
                    usedFieldNames.add(fieldName2);
                    currentNodeRight.add(new NodeEntity(fieldName2, 0));
                } catch (JSONException e) {
                    if (!notDistinguishNullAndEmpty || !StringUtil.jsonEmptyJudge(obj1FieldValue)) {
                        LogEntity log = new LogEntity(obj1FieldValue, obj2FieldValue, getUnmatchedPair(UnmatchedType.RIGHT_MISSING)
                                .setListKeys(currentListKeysLeft));
                        logs.add(log);
                    }
                }

                if (obj1FieldValue != null && obj2FieldValue != null)
                    jsonCompare(obj1FieldValue, obj2FieldValue);

                ListUti.removeLast(currentNodeLeft);
                if (rightExist)
                    ListUti.removeLast(currentNodeRight);
            }
            for (String fieldName : names2) {
                if (usedFieldNames.contains(fieldName)) continue;

                String fieldName2 = fieldName;
                currentNodeRight.add(new NodeEntity(fieldName, 0));

                Object obj2FieldValue, obj1FieldValue = null;
                obj2FieldValue = jsonObj2.get(fieldName);

                boolean leftExist = false;
                try {
                    obj1FieldValue = jsonObj1.get(fieldName2);
                    leftExist = true;
                    currentNodeLeft.add(new NodeEntity(fieldName2, 0));
                } catch (JSONException e) {
                    if (!notDistinguishNullAndEmpty || !StringUtil.jsonEmptyJudge(obj2FieldValue)) {
                        LogEntity log = new LogEntity(obj1FieldValue, obj2FieldValue, getUnmatchedPair(UnmatchedType.LEFT_MISSING)
                                .setListKeys(currentListKeysRight));
                        logs.add(log);
                    }
                }
                if (obj1FieldValue != null && obj2FieldValue != null)
                    jsonCompare(obj1FieldValue, obj2FieldValue);

                if (leftExist)
                    ListUti.removeLast(currentNodeLeft);
                ListUti.removeLast(currentNodeRight);
            }

        } else if (obj1 instanceof JSONArray) {
            listCompare((JSONArray) obj1, (JSONArray) obj2);

        } else {
            List<ReferenceEntity> references = findReferenceNode2(currentNodeLeft);
            if (!references.isEmpty()
                    && !"0".equals(String.valueOf(obj1))
                    && !"0".equals(String.valueOf(obj2))) {
                String refValue1 = String.valueOf(obj1);
                String refValue2 = String.valueOf(obj2);

                //currentNodeLeft,currentNodeRight
                List<NodeEntity> formerNodePathLeft = currentNodeLeft;
                List<NodeEntity> formerNodePathRight = currentNodeRight;

                currentTraceLeft.add((new ArrayList<>(currentNodeLeft)));
                currentTraceRight.add((new ArrayList<>(currentNodeRight)));
                currentTraceLeftForShow.add((new ArrayList<>(currentNodeLeft)));
                currentTraceRightForShow.add((new ArrayList<>(currentNodeRight)));

                Object refElementLeft = null;
                Object refElementRight = null;

                List<NodeEntity> refNodePathLeft = null;
                List<NodeEntity> refNodePathRight = null;

                List<String> pkNodeListPath = null;
                List<String> pkNodePath = null;
                boolean findLeft = false;
                boolean findRight = false;
                int leftIndex = -1;
                int rightIndex = -1;
                String leftKey = null;
                String rightKey = null;
                for (ReferenceEntity reference : references) {
                    pkNodeListPath = reference.getPkNodeListPath();
                    pkNodePath = reference.getPkNodePath();

                    Object refListLeft;
                    if (refPkListNodeCacheLeft.containsKey(pkNodeListPath)) {
                        refListLeft = refPkListNodeCacheLeft.get(pkNodeListPath);
                    } else {
                        refListLeft = findByPath(baseObj, pkNodeListPath);
                        refPkListNodeCacheLeft.put(pkNodeListPath, refListLeft);
                    }

                    if (refListLeft instanceof JSONArray) {
                        JSONArray array = ((JSONArray) refListLeft);
                        for (int i = 0; i < array.length(); i++) {
                            Object element = array.get(i);
                            List<String> pkSubPaths = pkNodePath.subList(pkNodeListPath.size(), pkNodePath.size());
                            Object pkNodeValue = findByPath(element, pkSubPaths);
                            if (refValue1.equals(String.valueOf(pkNodeValue))) {
                                refElementLeft = element;
                                List<NodeEntity> list = convertToNodeEntityList(pkNodeListPath);
                                leftKey = listIndexKeysLeft.get(list).get(i);
                                list.add(new NodeEntity(null, i));
                                refNodePathLeft = new ArrayList<>(list);
                                currentTraceLeft.add(new ArrayList<>(list));
                                for (String pkSub : pkSubPaths) {
                                    list.add(new NodeEntity(pkSub, 0));
                                }
                                currentTraceLeftForShow.add(list);
                                findLeft = true;
                                leftIndex = i;
                                break;
                            }
                        }
                    }

                    Object refListRight;
                    if (refPkListNodeCacheRight.containsKey(pkNodeListPath)) {
                        refListRight = refPkListNodeCacheRight.get(pkNodeListPath);
                    } else {
                        refListRight = findByPath(testObj, pkNodeListPath);
                        refPkListNodeCacheRight.put(pkNodeListPath, refListRight);
                    }
                    if (refListRight instanceof JSONArray) {
                        JSONArray array = ((JSONArray) refListRight);
                        for (int i = 0; i < array.length(); i++) {
                            Object element = array.get(i);
                            List<String> pkSubPaths = pkNodePath.subList(pkNodeListPath.size(), pkNodePath.size());
                            Object pkNodeValue = findByPath(element, pkSubPaths);
                            if (refValue2.equals(String.valueOf(pkNodeValue))) {
                                refElementRight = element;
                                List<NodeEntity> list = convertToNodeEntityList(pkNodeListPath);
                                rightKey = listIndexKeysRight.get(list).get(i);
                                list.add(new NodeEntity(null, i));
                                refNodePathRight = new ArrayList<>(list);
                                currentTraceRight.add(new ArrayList<>(list));
                                for (String pkSub : pkSubPaths) {
                                    list.add(new NodeEntity(pkSub, 0));
                                }
                                currentTraceRightForShow.add(list);
                                findRight = true;
                                rightIndex = i;
                                break;
                            }
                        }
                    }

                    if (findLeft || findRight) {
                        break;
                    }
                }

                if (refElementLeft == null || refElementRight == null) {
                    if (ignoreReferenceNotFound == 0) {
                        LogEntity log;
                        String pkNode = pkNodePath != null ? pkNodePath.get(pkNodePath.size() - 1) : "";
                        if (refElementLeft == null) {
                            log = new LogEntity(pkNode, obj1, getUnmatchedPair(UnmatchedType.REFERENCE_NOT_FOUND)
                                    .setListKeys(currentListKeysLeft));
                            log.setWarn(1);
                            logs.add(log);
                        }
                        if (refElementRight == null) {
                            log = new LogEntity(pkNode, obj2, getUnmatchedPair(UnmatchedType.REFERENCE_NOT_FOUND)
                                    .setListKeys(currentListKeysRight));
                            log.setWarn(1);
                            logs.add(log);
                        }
                    }
                } else {
                    boolean skip = false;

                    IndexPair indexPair = new IndexPair(leftIndex, rightIndex);
                    List<IndexPair> comparedIndexPairs = pkListIndexPair.get(pkNodeListPath);
                    if (comparedIndexPairs.contains(indexPair)) {
                        skip = true;
                    } else {
                        comparedIndexPairs.add(indexPair);
                    }

                    if (!skip) {
                        for (int m = 0; m < currentTraceLeft.size() - 1; m++) {
                            List<NodeEntity> trace = currentTraceLeft.get(m);
                            List<String> tracePath = ListUti.convertToStringList(trace);

                            for (int i = 0; i < pkNodeListPath.size(); i++) {
                                if (!pkNodeListPath.get(i).equals(tracePath.get(i))) break;
                                if (i == pkNodeListPath.size() - 1) {
                                    skip = true;
                                    break;
                                }
                            }
                            if (skip) {
                                break;
                            }
                        }
                    }

                    if (leftKey != null && rightKey != null && !leftKey.equals(rightKey)) {
                        skip = true;
                    }

                    if (!skip) {
                        currentNodeLeft = refNodePathLeft;
                        currentNodeRight = refNodePathRight;

                        jsonCompare(refElementLeft, refElementRight);
                        currentNodeLeft = formerNodePathLeft;
                        currentNodeRight = formerNodePathRight;
                    }
                }

                if (findLeft) {
                    ListUti.removeLast(currentTraceLeft);
                    ListUti.removeLast(currentTraceLeftForShow);
                }
                if (findRight) {
                    ListUti.removeLast(currentTraceRight);
                    ListUti.removeLast(currentTraceRightForShow);
                }

                ListUti.removeLast(currentTraceLeft);
                ListUti.removeLast(currentTraceRight);
                ListUti.removeLast(currentTraceLeftForShow);
                ListUti.removeLast(currentTraceRightForShow);
            } else if (!obj1.equals(obj2)) {
                if (obj1 instanceof Number && !obj1.getClass().equals(obj2.getClass())) {
                    if (obj1 instanceof Integer && obj2 instanceof Double) {
                        if ((Integer) obj1 == ((Double) obj2).intValue()) {
                            return;
                        }
                    } else if (obj2 instanceof Integer && obj1 instanceof Double) {
                        if ((Integer) obj2 == ((Double) obj1).intValue()) {
                            return;
                        }
                    }
                }

                if (obj1 instanceof String && obj1.toString().startsWith(DATE_PREFIX) || obj2.toString().startsWith(DATE_PREFIX)) {
                    if (dateEquals(obj1, obj2)) {
                        return;
                    }
                }

                if (bothNumberOrString(obj1, obj2)) {
                    if (obj1.toString().equals(obj2.toString())) {
                        return;
                    }
                }

                LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(UnmatchedType.UNMATCHED)
                        .setListKeys(currentListKeysLeft));
                logs.add(log);
            }
        }
    }

    private String getKey(IndexKey indexKey, List<NodeEntity> list, int index) {
        String result = null;
        IndexKey indexKey1 = KeyUtil.getIndexKey(indexKey, list);
        List<String> uniqueKeys = indexKey1.getUniqueKeys();
        if (uniqueKeys != null && !uniqueKeys.isEmpty()) {
            try {
                result = uniqueKeys.get(index);
            } catch (Exception e) {
            }
        }
        return result;
    }

    private boolean bothEmptyString(Object obj1, Object obj2) {
        if ((obj1 == null || JSONObject.NULL.equals(obj1)) && (obj2 == null || Strings.isNullOrEmpty(obj2.toString()))) {
            return true;
        }
        if ((obj2 == null || JSONObject.NULL.equals(obj2)) && (obj1 == null || Strings.isNullOrEmpty(obj1.toString()))) {
            return true;
        }
        return false;
    }

    public static final String DATE_PREFIX = "/Date(";

    private boolean differentType(Object obj1, Object obj2) {
        if (obj1.getClass().equals(obj2.getClass())) {
            return false;
        }
        if (JSONObject.NULL.equals(obj1) && obj2 instanceof String) {
            return false;
        }
        if (JSONObject.NULL.equals(obj2) && obj1 instanceof String) {
            return false;
        }
        if (obj1 instanceof Number && obj2 instanceof Number) {
            return false;
        }
        if (bothNumberOrString(obj1, obj2)) {
            return false;
        }
        return true;
    }

    private boolean bothNumberOrString(Object obj1, Object obj2) {
        return (obj1 instanceof Number && obj2 instanceof String) || (obj2 instanceof Number && obj1 instanceof String);
    }

    public static final long EIGHT_HOUR_MILLIS = 28800000;
    public static final long EIGHT_HOUR_MILLIS_1 = 28799000;

    private boolean dateEquals(Object obj1, Object obj2) {
        String dateStr1 = obj1.toString();
        String dateStr2 = obj2.toString();

        if (dateStr1.startsWith(DATE_PREFIX) && dateStr2.startsWith(DATE_PREFIX)) {
            String longValuePart1 = getDateLongValuePart(dateStr1);
            String longValuePart2 = getDateLongValuePart(dateStr2);
            if (longValuePart1.equals(longValuePart2)) {
                return true;
            } else if (dateStr1.contains("+0800") || dateStr1.contains("-0800") ||
                    dateStr2.contains("+0800") || dateStr2.contains("-0800")) {
                try {
                    return judgeTime(Long.parseLong(longValuePart1), Long.parseLong(longValuePart2));
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        Date date1, date2;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            if (dateStr1.startsWith(DATE_PREFIX)) {
                date1 = new Date(getDateLongValue(dateStr1));
            } else {
                date1 = format.parse(dateStr1);
            }
            if (dateStr2.startsWith(DATE_PREFIX)) {
                date2 = new Date(getDateLongValue(dateStr2));
            } else {
                date2 = format.parse(dateStr2);
            }

            if (date1.equals(date2)) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private long getDateLongValue(String dateStr) {
        return Long.parseLong(getDateLongValuePart(dateStr));
    }

    private boolean judgeTime(long time1, long time2) {
        long abs = Math.abs(time1 - time2);
        return abs == EIGHT_HOUR_MILLIS || abs == EIGHT_HOUR_MILLIS_1 ||
                abs == EIGHT_HOUR_MILLIS * 2 || abs == EIGHT_HOUR_MILLIS_1 * 2;
    }

    private String getDateLongValuePart(String dateStr) {
        int i = dateStr.indexOf('+');
        if (i == -1) {
            i = dateStr.indexOf('-', DATE_PREFIX.length() + 1);
            if (i == -1) {
                i = dateStr.indexOf(')');
            }
        }
        return dateStr.substring(DATE_PREFIX.length(), i);
    }

    private void listCompare(JSONArray obj1, JSONArray obj2) throws JSONException {
        JSONArray obj1Array = obj1;
        JSONArray obj2Array = obj2;

        if (obj1Array.length() != obj2Array.length()) {
            LogEntity log = new LogEntity(obj1Array.length(), obj2Array.length(), getUnmatchedPair(UnmatchedType.DIFFERENT_COUNT)
                    .setListKeys(currentListKeysLeft));
            logs.add(log);
        }

        List<Integer> usedIndexes = new ArrayList<>();
        List<Integer> leftComparedIndexes = new ArrayList<>();
        Map<Integer, String> indexKeysLeft = listIndexKeysLeft.get(currentNodeLeft);
        Map<Integer, String> indexKeysRight = listIndexKeysRight.get(currentNodeRight);

        String currentListPath = ListUti.convertPathToStringForShow(currentNodeLeft);
        if (pkListIndexPair == null) {
            pkListIndexPair = new HashMap<>();
        }


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
                        LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.RIGHT_MISSING)
                                .setListKeys(currentListKeysLeft));
                        List<ReferenceEntity> references = findReferenceNode2(currentNodeLeft);
                        if (!references.isEmpty()) {
                            List<NodeEntity> pkNodePath = getPkNodePath(references, true, element1);
                            if (pkNodePath != null) {
                                log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
                            }
                        }
                        logs.add(log);
                    }
                }
                if (element1 != null && element2 != null) {
                    jsonCompare(element1, element2);
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
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.NOT_UNIQUE)
                            .setListKeys(currentListKeysLeft));
                    log.setWarn(1);
                    addReferencePath(log, true, element1);
                    logs.add(log);
                }
                if (correspondRightIndex == -1) {
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.RIGHT_MISSING)
                            .setListKeys(currentListKeysLeft));

                    List<ReferenceEntity> references = findReferenceNode2(currentNodeLeft);
                    if (!references.isEmpty()) {
                        List<NodeEntity> pkNodePath = getPkNodePath(references, true, element1);
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
                        jsonCompare(element1, element2);
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
                        LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.LEFT_MISSING)
                                .setListKeys(currentListKeysRight));

                        List<ReferenceEntity> references = findReferenceNode2(currentNodeRight);
                        if (!references.isEmpty()) {
                            List<NodeEntity> pkNodePath = getPkNodePath(references, false, element2);
                            if (pkNodePath != null) {
                                log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
                            }
                        }
                        logs.add(log);
                    }
                }
                if (element1 != null && element2 != null)
                    jsonCompare(element1, element2);

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
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.NOT_UNIQUE)
                            .setListKeys(currentListKeysRight));
                    log.setWarn(1);
                    addReferencePath(log, false, element2);
                    logs.add(log);
                }
                if (correspondLeftIndex == -1) {
                    LogEntity log = new LogEntity(element1, element2, getUnmatchedPair(UnmatchedType.LEFT_MISSING)
                            .setListKeys(currentListKeysRight));

                    List<ReferenceEntity> references = findReferenceNode2(currentNodeRight);
                    if (!references.isEmpty()) {

                        List<NodeEntity> pkNodePath = getPkNodePath(references, false, element2);
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

                    if (needCompare && element1 != null && element2 != null)
                        jsonCompare(element1, element2);
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

    private List<NodeEntity> addListKeyPath(List<NodeEntity> nodePath, Integer index) {
        List<NodeEntity> listKeyPath = new ArrayList<>(nodePath);
        listKeyPath.add(new NodeEntity(null, index));
        return listKeyPath;
    }

    private void addReferencePath(LogEntity log, boolean left, Object obj) throws JSONException {
        List<ReferenceEntity> references = findReferenceNode2(left ? currentNodeLeft : currentNodeRight);
        if (!references.isEmpty()) {
            List<NodeEntity> pkNodePath = getPkNodePath(references, left, obj);
            if (pkNodePath != null) {
                if (left) {
                    log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
                } else {
                    log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
                }
            }
        }
    }

    private List<NodeEntity> getPkNodePath(List<ReferenceEntity> references, boolean isLeft, Object obj) throws JSONException {
        for (ReferenceEntity reference : references) {
            List<String> pkNodeListPath = reference.getPkNodeListPath();
            List<String> pkNodePath = reference.getPkNodePath();

            Object refList;
            Map<List<String>, Object> refPkListNodeCache = isLeft ? refPkListNodeCacheLeft : refPkListNodeCacheRight;

            if (refPkListNodeCache.containsKey(pkNodeListPath)) {
                refList = refPkListNodeCache.get(pkNodeListPath);
            } else {
                refList = findByPath(isLeft ? baseObj : testObj, pkNodeListPath);
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

    private String indexKey(int index) {
        return "Index:[" + index + "]";
    }

    private List<NodeEntity> convertToNodeEntityList(List<String> pkNodeListPath) {
        List<NodeEntity> list = new ArrayList<>();
        for (int j = 0; j < pkNodeListPath.size(); j++) {
            list.add(new NodeEntity(pkNodeListPath.get(j), 0));
        }
        return list;
    }

    private List<NodeEntity> getListPath(List<NodeEntity> currentNode) {
        List<NodeEntity> result = new ArrayList<>(currentNode);
        int size = result.size();
        for (int i = size - 1; i >= 0; i--) {
            NodeEntity nodeEntity = result.get(i);
            if (nodeEntity.getNodeName() != null) {
                result.remove(i);
            } else {
                result.remove(i);
                break;
            }
        }
        return result;
    }

    private List<ReferenceEntity> findReferenceNode2(List<NodeEntity> nodeEntities) {
        List<ReferenceEntity> references = new ArrayList<>();
        List<String> nodePath = new ArrayList<>();
        for (int i = 0; i < nodeEntities.size(); i++) {
            if (nodeEntities.get(i).getNodeName() != null) {
                nodePath.add(nodeEntities.get(i).getNodeName());
            }
        }
        if (responseReferences != null) {
            for (ReferenceEntity responseReference : responseReferences) {
                if (findPath(nodePath, responseReference.getFkNodePath()) != null) {
                    references.add(responseReference);
                }
            }
        }

        return references;
    }

    List<String> findPath(List<String> nodePath, List<String>... fkPaths) {
        for (List<String> path : fkPaths) {
            int length = path.size();
            if ("%value%".equals(path.get(path.size() - 1))) {
                length = path.size() - 1;
            }
            if (length == nodePath.size()) {
                for (int i = 0; i < nodePath.size(); i++) {
                    if (!path.get(i).equals(nodePath.get(i)) && !path.get(i).equals(Constant.DYNAMIC_PATH)) break;
                    if (i == nodePath.size() - 1) return path;
                }
            }
        }
        return null;
    }


    private UnmatchedPairEntity getUnmatchedPair(int unmatchedType) {
        return new UnmatchedPairEntity(unmatchedType, currentNodeLeft, currentNodeRight, new Trace(currentTraceLeftForShow, currentTraceRightForShow));
    }

    private List<String> getComparedListKeys(Map<String, String> listKeyMap) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : listKeyMap.entrySet()) {
            list.add(entry.getKey() + ": " + entry.getValue());
        }
        return list;
    }

    public Object findByPath(Object object, List<String> path) {
        if (object == null || path == null || path.size() == 0) return null;
        Object target = object;
        try {
            for (int i = 0; i < path.size(); i++) {
                String nodeName = path.get(i);
                target = ((JSONObject) target).get(nodeName);
            }
        } catch (Throwable e) {
            return null;
        }
        return target;
    }

}
