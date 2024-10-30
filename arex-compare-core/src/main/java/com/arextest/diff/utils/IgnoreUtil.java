package com.arextest.diff.utils;

import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by rchen9 on 2022/9/22.
 */
public class IgnoreUtil {

  public static boolean ignoreProcessor(List<String> fuzzyNodePath,
      List<NodeEntity> baseNodePath, List<NodeEntity> testNodePath,
      List<List<ExpressionNodeEntity>> exclusions,
      Map<LinkedList<LinkedList<ExpressionNodeEntity>>, LinkedList<LinkedList<ExpressionNodeEntity>>> conditionExclusions,
      Set<String> ignoreNodeSet) {
    if (ignoreNodeProcessor(fuzzyNodePath, ignoreNodeSet)) {
      return true;
    }

    if (exclusions != null && !exclusions.isEmpty()) {
      for (List<ExpressionNodeEntity> ignoreNodePath : exclusions) {
        if (ignoreMatch(fuzzyNodePath, ignoreNodePath)) {
          return true;
        }
      }
    }

    if (conditionExclusions != null && !conditionExclusions.isEmpty()) {
      for (Map.Entry<LinkedList<LinkedList<ExpressionNodeEntity>>, LinkedList<LinkedList<ExpressionNodeEntity>>> entry : conditionExclusions.entrySet()) {
        LinkedList<LinkedList<ExpressionNodeEntity>> key = entry.getKey();
        LinkedList<LinkedList<ExpressionNodeEntity>> value = entry.getValue();
        if (multiIgnoreExpressionMatch(baseNodePath, key) && multiIgnoreExpressionMatch(
            testNodePath, value)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean ignoreProcessorEngine(List<String> fuzzyNodePath, List<NodeEntity> nodePath,
      List<List<ExpressionNodeEntity>> exclusions,
      List<List<ExpressionNodeEntity>> expressionExclusions,
      Set<String> ignoreNodeSet) {
    if (ignoreNodeProcessor(fuzzyNodePath, ignoreNodeSet)) {
      return true;
    }

    if (exclusions != null && !exclusions.isEmpty()) {
      for (List<ExpressionNodeEntity> ignoreNodePath : exclusions) {
        if (ignoreMatch(fuzzyNodePath, ignoreNodePath)) {
          return true;
        }
      }
    }

    if (expressionExclusions != null && !expressionExclusions.isEmpty()) {
      for (List<ExpressionNodeEntity> ignoreNodePath : expressionExclusions) {
        if (ignoreExpressionMatch(nodePath, ignoreNodePath)) {
          return true;
        }
      }
    }

    return false;
  }


  private static boolean ignoreMatch(List<String> pathInList,
      List<ExpressionNodeEntity> ignoreNodePath) {

    int size = ignoreNodePath.size();
    if (size > pathInList.size()) {
      return false;
    }

    for (int i = 0; i < size; i++) {
      if (!Objects.equals(ignoreNodePath.get(i).getNodeName(), pathInList.get(i)) &&
          !Objects.equals(ignoreNodePath.get(i).getNodeName(), Constant.DYNAMIC_PATH)) {
        return false;
      }
    }
    return true;
  }

  private static boolean ignoreNodeProcessor(List<String> nodePath, Set<String> ignoreNodeSet) {

    if (ignoreNodeSet == null || ignoreNodeSet.isEmpty()) {
      return false;
    }

    if (nodePath == null || nodePath.isEmpty()) {
      return false;
    }

    for (String nodeName : nodePath) {
      if (ignoreNodeSet.contains(nodeName)) {
        return true;
      }
    }
    return false;
  }

  private static boolean multiIgnoreExpressionMatch(List<NodeEntity> nodePath,
      LinkedList<LinkedList<ExpressionNodeEntity>> expressionNodeEntityListNodePath) {

    boolean result = false;
    if (expressionNodeEntityListNodePath == null) {
      result = true;
    } else {
      for (LinkedList<ExpressionNodeEntity> expressionNodeEntityList : expressionNodeEntityListNodePath) {
        if (ignoreExpressionMatch(nodePath, expressionNodeEntityList)) {
          result = true;
          break;
        }
      }
    }
    return result;
  }

  private static boolean ignoreExpressionMatch(List<NodeEntity> nodePath,
      List<ExpressionNodeEntity> expressionNodeEntityListNodePath) {

    int size = expressionNodeEntityListNodePath.size();
    if (size > nodePath.size()) {
      return false;
    }
    for (int i = 0; i < size; i++) {
      ExpressionNodeEntity expressionNodeEntity = expressionNodeEntityListNodePath.get(i);
      NodeEntity nodeEntity = nodePath.get(i);
      if (expressionNodeEntity.getNodeType() == ExpressionNodeType.INDEX_NODE) {
        if (nodeEntity.getNodeName() != null) {
          return false;
        }
        if (expressionNodeEntity.getIndex() != nodeEntity.getIndex()) {
          return false;
        }
      } else if (expressionNodeEntity.getNodeType() == ExpressionNodeType.NAME_NODE) {
        if (nodeEntity.getNodeName() == null) {
          return false;
        }
        if (!Objects.equals(expressionNodeEntity.getNodeName(), nodePath.get(i).getNodeName())) {
          return false;
        }

      } else {
        return false;
      }
    }
    return true;
  }


}
