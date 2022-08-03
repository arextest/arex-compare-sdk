package com.arextest.diff.compare;

import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.log.Trace;
import com.arextest.diff.model.log.UnmatchedPairEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompareHelper {

    public static UnmatchedPairEntity getUnmatchedPair(int unmatchedType, CompareContext compareContext) {
        return new UnmatchedPairEntity(unmatchedType, compareContext.getCurrentNodeLeft(), compareContext.getCurrentNodeRight(),
                new Trace(compareContext.getCurrentTraceLeftForShow(), compareContext.getCurrentTraceRightForShow()));
    }


    private static List<String> findPath(List<String> nodePath, List<String>... fkPaths) {
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

    public static List<ReferenceEntity> findReferenceNode(List<NodeEntity> nodeEntities, List<ReferenceEntity> responseReferences) {
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

    public static Object findByPath(Object object, List<String> path) {
        if (object == null || path == null || path.size() == 0) {
            return null;
        }
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

    public static List<NodeEntity> getPkNodePath(List<ReferenceEntity> references, boolean isLeft, Object obj, CompareContext compareContext) throws JSONException {
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

    public static List<NodeEntity> convertToNodeEntityList(List<String> pkNodeListPath) {
        List<NodeEntity> list = new ArrayList<>();
        for (int j = 0; j < pkNodeListPath.size(); j++) {
            list.add(new NodeEntity(pkNodeListPath.get(j), 0));
        }
        return list;
    }

}
