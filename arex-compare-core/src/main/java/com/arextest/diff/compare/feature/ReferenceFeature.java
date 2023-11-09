package com.arextest.diff.compare.feature;

import com.arextest.diff.compare.CompareHelper;
import com.arextest.diff.compare.GenericCompare;
import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.register.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.compare.IndexPair;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReferenceFeature {

  public static void referenceHandler(List<ReferenceEntity> references, Object obj1, Object obj2,
      CompareContext compareContext) throws Exception {

    String refValue1 = String.valueOf(obj1);
    String refValue2 = String.valueOf(obj2);

    // currentNodeLeft, currentNodeRight
    List<NodeEntity> formerNodePathLeft = new ArrayList<>(compareContext.currentNodeLeft);
    List<NodeEntity> formerNodePathRight = new ArrayList<>(compareContext.currentNodeRight);

    compareContext.currentTraceLeft.add((new ArrayList<>(compareContext.currentNodeLeft)));
    compareContext.currentTraceRight.add((new ArrayList<>(compareContext.currentNodeRight)));
    compareContext.currentTraceLeftForShow.add((new ArrayList<>(compareContext.currentNodeLeft)));
    compareContext.currentTraceRightForShow.add((new ArrayList<>(compareContext.currentNodeRight)));

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
      if (compareContext.refPkListNodeCacheLeft.containsKey(pkNodeListPath)) {
        refListLeft = compareContext.refPkListNodeCacheLeft.get(pkNodeListPath);
      } else {
        refListLeft = CompareHelper.findByPath(compareContext.baseObj, pkNodeListPath);
        compareContext.refPkListNodeCacheLeft.put(pkNodeListPath, refListLeft);
      }

      if (refListLeft instanceof ArrayNode) {
        ArrayNode array = ((ArrayNode) refListLeft);
        for (int i = 0; i < array.size(); i++) {
          Object element = array.get(i);
          List<String> pkSubPaths = pkNodePath.subList(pkNodeListPath.size(), pkNodePath.size());
          Object pkNodeValue = CompareHelper.findByPath(element, pkSubPaths);
          if (refValue1.equals(String.valueOf(pkNodeValue))) {
            refElementLeft = element;
            List<NodeEntity> list = CompareHelper.convertToNodeEntityList(pkNodeListPath);
            if (compareContext.listIndexKeysLeft.get(list) != null) {
              leftKey = compareContext.listIndexKeysLeft.get(list).get(i);
            }
            list.add(new NodeEntity(null, i));
            refNodePathLeft = new ArrayList<>(list);
            compareContext.currentTraceLeft.add(new ArrayList<>(list));
            for (String pkSub : pkSubPaths) {
              list.add(new NodeEntity(pkSub, 0));
            }
            compareContext.currentTraceLeftForShow.add(list);
            findLeft = true;
            leftIndex = i;
            break;
          }
        }
      }

      Object refListRight;
      if (compareContext.refPkListNodeCacheRight.containsKey(pkNodeListPath)) {
        refListRight = compareContext.refPkListNodeCacheRight.get(pkNodeListPath);
      } else {
        refListRight = CompareHelper.findByPath(compareContext.testObj, pkNodeListPath);
        compareContext.refPkListNodeCacheRight.put(pkNodeListPath, refListRight);
      }
      if (refListRight instanceof ArrayNode) {
        ArrayNode array = ((ArrayNode) refListRight);
        for (int i = 0; i < array.size(); i++) {
          Object element = array.get(i);
          List<String> pkSubPaths = pkNodePath.subList(pkNodeListPath.size(), pkNodePath.size());
          Object pkNodeValue = CompareHelper.findByPath(element, pkSubPaths);
          if (refValue2.equals(String.valueOf(pkNodeValue))) {
            refElementRight = element;
            List<NodeEntity> list = CompareHelper.convertToNodeEntityList(pkNodeListPath);
            if (compareContext.listIndexKeysRight.get(list) != null) {
              rightKey = compareContext.listIndexKeysRight.get(list).get(i);
            }
            list.add(new NodeEntity(null, i));
            refNodePathRight = new ArrayList<>(list);
            compareContext.currentTraceRight.add(new ArrayList<>(list));
            for (String pkSub : pkSubPaths) {
              list.add(new NodeEntity(pkSub, 0));
            }
            compareContext.currentTraceRightForShow.add(list);
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
      if (compareContext.ignoreReferenceNotFound == 0) {
        String pkNode = pkNodePath != null ? pkNodePath.get(pkNodePath.size() - 1) : "";
        if (refElementLeft == null) {
          LogRegister.register(pkNode, obj1, LogMarker.LEFT_REF_NOT_FOUND, compareContext);
        }
        if (refElementRight == null) {
          LogRegister.register(pkNode, obj2, LogMarker.RIGHT_REF_NOT_FOUND, compareContext);
        }
      }
    } else {
      boolean skip = false;

      IndexPair indexPair = new IndexPair(leftIndex, rightIndex);
      List<IndexPair> comparedIndexPairs = compareContext.pkListIndexPair.get(pkNodeListPath);
      if (comparedIndexPairs.contains(indexPair)) {
        skip = true;
      }

      if (!skip) {
        for (int m = 0; m < compareContext.currentTraceLeft.size() - 1; m++) {
          List<NodeEntity> trace = compareContext.currentTraceLeft.get(m);
          List<String> tracePath = ListUti.convertToStringList(trace);

          for (int i = 0; i < pkNodeListPath.size(); i++) {
            if (!pkNodeListPath.get(i).equals(tracePath.get(i))) {
              break;
            }
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

      if (leftKey == null || rightKey == null || !Objects.equals(leftKey, rightKey)) {
        skip = true;
      }

      if (!skip) {
        comparedIndexPairs.add(indexPair);
        compareContext.currentNodeLeft = refNodePathLeft;
        compareContext.currentNodeRight = refNodePathRight;
        GenericCompare.jsonCompare(refElementLeft, refElementRight, compareContext);
        compareContext.currentNodeLeft = formerNodePathLeft;
        compareContext.currentNodeRight = formerNodePathRight;
      }
    }

    if (findLeft) {
      ListUti.removeLast(compareContext.currentTraceLeft);
      ListUti.removeLast(compareContext.currentTraceLeftForShow);
    }
    if (findRight) {
      ListUti.removeLast(compareContext.currentTraceRight);
      ListUti.removeLast(compareContext.currentTraceRightForShow);
    }

    ListUti.removeLast(compareContext.currentTraceLeft);
    ListUti.removeLast(compareContext.currentTraceRight);
    ListUti.removeLast(compareContext.currentTraceLeftForShow);
    ListUti.removeLast(compareContext.currentTraceRightForShow);
  }

}
