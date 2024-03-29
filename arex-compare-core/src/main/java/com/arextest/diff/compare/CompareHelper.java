package com.arextest.diff.compare;

import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.log.Trace;
import com.arextest.diff.model.log.UnmatchedPairEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CompareHelper {

  public static boolean bothEmptyString(Object obj1, Object obj2) {
    if (obj1 instanceof NullNode && Objects.equals("", ((JsonNode) obj2).asText())) {
      return true;
    }
    if (obj2 instanceof NullNode && Objects.equals("", ((JsonNode) obj1).asText())) {
      return true;
    }
    return false;
  }

  public static UnmatchedPairEntity getUnmatchedPair(int unmatchedType,
      CompareContext compareContext) {
    return new UnmatchedPairEntity(unmatchedType, compareContext.currentNodeLeft,
        compareContext.currentNodeRight,
        new Trace(compareContext.currentTraceLeftForShow, compareContext.currentTraceRightForShow));
  }


  private static List<String> findPath(List<String> nodePath, List<String>... fkPaths) {
    for (List<String> path : fkPaths) {
      int length = path.size();
      if ("%value%".equals(path.get(path.size() - 1))) {
        length = path.size() - 1;
      }
      if (length == nodePath.size()) {
        for (int i = 0; i < nodePath.size(); i++) {
          if (!path.get(i).equals(nodePath.get(i)) && !path.get(i).equals(Constant.DYNAMIC_PATH)) {
            break;
          }
          if (i == nodePath.size() - 1) {
            return path;
          }
        }
      }
    }
    return null;
  }

  public static List<ReferenceEntity> findReferenceNode(List<NodeEntity> nodeEntities,
      List<ReferenceEntity> responseReferences) {
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
        target = ((ObjectNode) target).get(nodeName);
      }
    } catch (Throwable e) {
      return null;
    }
    return target;
  }

  public static List<NodeEntity> getPkNodePath(List<ReferenceEntity> references, boolean isLeft,
      Object obj, CompareContext compareContext) {
    for (ReferenceEntity reference : references) {
      List<String> pkNodeListPath = reference.getPkNodeListPath();
      List<String> pkNodePath = reference.getPkNodePath();

      Object refList;
      Map<List<String>, Object> refPkListNodeCache =
          isLeft ? compareContext.refPkListNodeCacheLeft : compareContext.refPkListNodeCacheRight;

      if (refPkListNodeCache.containsKey(pkNodeListPath)) {
        refList = refPkListNodeCache.get(pkNodeListPath);
      } else {
        refList = findByPath(isLeft ? compareContext.baseObj : compareContext.testObj,
            pkNodeListPath);
        refPkListNodeCache.put(pkNodeListPath, refList);
      }

      if (refList instanceof ArrayNode) {
        ArrayNode array = ((ArrayNode) refList);
        for (int i = 0; i < array.size(); i++) {
          Object element = array.get(i);
          Object pkNodeValue = findByPath(element,
              pkNodePath.subList(pkNodeListPath.size(), pkNodePath.size()));
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
