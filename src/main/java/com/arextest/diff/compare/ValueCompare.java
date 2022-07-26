package com.arextest.diff.compare;

import com.arextest.diff.model.compare.IndexPair;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.arextest.diff.compare.CompareHelper.*;

/**
 * Created by rchen9 on 2022/7/25.
 */
public class ValueCompare {

    private static final String DATE_PREFIX = "/Date(";
    private static final long EIGHT_HOUR_MILLIS = 28800000;
    private static final long EIGHT_HOUR_MILLIS_1 = 28799000;

    public static void valueCompare(Object obj1, Object obj2, CompareContext compareContext) {

        Object baseObj = compareContext.getBaseObj();
        Object testObj = compareContext.getTestObj();
        List<NodeEntity> currentNodeLeft = compareContext.getCurrentNodeLeft();
        List<NodeEntity> currentNodeRight = compareContext.getCurrentNodeRight();
        List<String> currentListKeysLeft = compareContext.getCurrentListKeysLeft();
        List<String> currentListKeysRight = compareContext.getCurrentListKeysRight();
        HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = compareContext.getListIndexKeysLeft();
        HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = compareContext.getListIndexKeysRight();
        Map<List<String>, Object> refPkListNodeCacheLeft = compareContext.getRefPkListNodeCacheLeft();
        Map<List<String>, Object> refPkListNodeCacheRight = compareContext.getRefPkListNodeCacheRight();
        List<List<NodeEntity>> currentTraceLeft = compareContext.getCurrentTraceLeft();
        List<List<NodeEntity>> currentTraceRight = compareContext.getCurrentTraceRight();
        List<List<NodeEntity>> currentTraceLeftForShow = compareContext.getCurrentTraceLeftForShow();
        List<List<NodeEntity>> currentTraceRightForShow = compareContext.getCurrentTraceRightForShow();
        List<ReferenceEntity> responseReferences = compareContext.getResponseReferences();
        byte ignoreReferenceNotFound = compareContext.getIgnoreReferenceNotFound();
        Map<List<String>, List<IndexPair>> pkListIndexPair = compareContext.getPkListIndexPair();
        List<LogEntity> logs = compareContext.getLogs();


        List<ReferenceEntity> references = findReferenceNode(currentNodeLeft, responseReferences);
        if (!references.isEmpty()) {
            String refValue1 = String.valueOf(obj1);
            String refValue2 = String.valueOf(obj2);

            // currentNodeLeft, currentNodeRight
            List<NodeEntity> formerNodePathLeft = new ArrayList<>(currentNodeLeft);
            List<NodeEntity> formerNodePathRight = new ArrayList<>(currentNodeRight);

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
                        log = new LogEntity(pkNode, obj1, getUnmatchedPair(UnmatchedType.REFERENCE_NOT_FOUND, compareContext)
                                .setListKeys(currentListKeysLeft));
                        log.setWarn(1);
                        logs.add(log);
                    }
                    if (refElementRight == null) {
                        log = new LogEntity(pkNode, obj2, getUnmatchedPair(UnmatchedType.REFERENCE_NOT_FOUND, compareContext)
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
                    arrayCopy(currentNodeLeft, refNodePathLeft);
                    arrayCopy(currentNodeRight, refNodePathRight);

                    GenericCompare.jsonCompare(refElementLeft, refElementRight, compareContext);

                    arrayCopy(currentNodeLeft, formerNodePathLeft);
                    arrayCopy(currentNodeRight, formerNodePathRight);
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

            LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(UnmatchedType.UNMATCHED, compareContext).setListKeys(currentListKeysLeft));
            logs.add(log);
        }
    }

    private static boolean dateEquals(Object obj1, Object obj2) {
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

    private static boolean judgeTime(long time1, long time2) {
        long abs = Math.abs(time1 - time2);
        return abs == EIGHT_HOUR_MILLIS || abs == EIGHT_HOUR_MILLIS_1 ||
                abs == EIGHT_HOUR_MILLIS * 2 || abs == EIGHT_HOUR_MILLIS_1 * 2;
    }

    private static long getDateLongValue(String dateStr) {
        return Long.parseLong(getDateLongValuePart(dateStr));
    }

    private static String getDateLongValuePart(String dateStr) {
        int i = dateStr.indexOf('+');
        if (i == -1) {
            i = dateStr.indexOf('-', DATE_PREFIX.length() + 1);
            if (i == -1) {
                i = dateStr.indexOf(')');
            }
        }
        return dateStr.substring(DATE_PREFIX.length(), i);
    }

    private static <T> void arrayCopy(List<T> dest, List<T> src) {
        dest.clear();
        dest.addAll(src);
    }

}
