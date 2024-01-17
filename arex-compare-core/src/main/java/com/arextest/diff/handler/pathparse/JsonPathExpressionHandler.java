package com.arextest.diff.handler.pathparse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeType;
import com.arextest.diff.model.pathparse.expression.EqualsExpression;
import com.arextest.diff.model.pathparse.expression.PathExpression;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class JsonPathExpressionHandler {

  private static final Logger LOGGER = Logger.getLogger(JsonPathExpressionHandler.class.getName());

  public void doExpressionParse(RulesConfig rulesConfig,
      Object baseObj, Object testObj) throws ExecutionException, InterruptedException {

    Set<List<ExpressionNodeEntity>> result = new HashSet<>();
    try {
      List<List<ExpressionNodeEntity>> expressionExclusions = rulesConfig.getExpressionExclusions();

      CompletableFuture<LinkedList<LinkedList<ExpressionNodeEntity>>> future1 = CompletableFuture.supplyAsync(
          () -> doMultiExpressionParse(expressionExclusions, baseObj),
          TaskThreadFactory.jsonObjectThreadPool
      );

      CompletableFuture<LinkedList<LinkedList<ExpressionNodeEntity>>> future2 = CompletableFuture.supplyAsync(
          () -> doMultiExpressionParse(expressionExclusions, testObj),
          TaskThreadFactory.jsonObjectThreadPool
      );

      CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(future1, future2);
      voidCompletableFuture.get(Constant.JSON_PATH_PARSE_MINUTES_TIME, TimeUnit.MINUTES);

      result.addAll(future1.get());
      result.addAll(future2.get());

    } catch (RuntimeException | TimeoutException e) {
      LOGGER.warning("doExpressionParse error: " + e.getMessage());
    }
    rulesConfig.setExpressionExclusions(new LinkedList<>(result));
  }

  public LinkedList<LinkedList<ExpressionNodeEntity>> doMultiExpressionParse(
      List<List<ExpressionNodeEntity>> expressionExclusions, Object object) {

    LinkedList<LinkedList<ExpressionNodeEntity>> result = new LinkedList<>();

    try {
      if (ListUti.isEmpty(expressionExclusions)) {
        return result;
      }

      for (List<ExpressionNodeEntity> expressionNodeEntityList : expressionExclusions) {
        LinkedList<LinkedList<ExpressionNodeEntity>> linkedLists = doSinglePathExpressionParse(
            expressionNodeEntityList, 0, expressionNodeEntityList.size(), object, false);
        if (linkedLists != null) {
          result.addAll(linkedLists);
        }
      }
    } catch (RuntimeException exception) {
      LOGGER.warning("doMultiExpressionParse error: " + exception.getMessage());
    }
    return result;
  }


  /**
   * response/students/[i] response/students/[i]/info response/su/[i]/info
   * response/students/[info/name="xiaomi"] response/students/[info/name="xiaomi"]/age
   * response/students/[info/name="xiaomi"]/info/name response/students/info/[name="xiaomi"]
   * response/[region="beijing"] response/students[%value%="NO.111"]
   *
   * @return
   */
  private LinkedList<LinkedList<ExpressionNodeEntity>> doSinglePathExpressionParse(
      List<ExpressionNodeEntity> expressionNodeEntityList,
      int startIndex, int endIndex, Object object, boolean isArr) {

    LinkedList<LinkedList<ExpressionNodeEntity>> result = null;

    if (startIndex >= endIndex) {
      return new LinkedList<>();
    }

    if (object == null) {
      return result;
    }

    ExpressionNodeEntity expressionNodeEntity = expressionNodeEntityList.get(startIndex);
    int nodeType = expressionNodeEntity.getNodeType();

    if (nodeType == ExpressionNodeType.NAME_NODE) {

      String nodeName = expressionNodeEntity.getNodeName();
      if (object instanceof ObjectNode) {
        Object nextObj = ((ObjectNode) object).get(nodeName);
        LinkedList<LinkedList<ExpressionNodeEntity>> localPath = doSinglePathExpressionParse(
            expressionNodeEntityList, startIndex + 1, endIndex,
            nextObj, false);
        result = appendExpressionNameNode(localPath, nodeName);

      } else if (object instanceof ArrayNode) {

        // condition: fuzzy paths
        ArrayNode arrayNode = (ArrayNode) object;
        for (int i = 0; i < arrayNode.size(); i++) {
          Object nextObj = arrayNode.get(i);
          LinkedList<LinkedList<ExpressionNodeEntity>> localPath = doSinglePathExpressionParse(
              expressionNodeEntityList, startIndex, endIndex,
              nextObj, true);
          if (localPath != null) {
            if (result == null) {
              result = new LinkedList<>();
            }
            result.addAll(appendExpressionIndexNode(localPath, i));
          }
        }

      } else {
        // todo: %value%
      }

    } else if (nodeType == ExpressionNodeType.EXPRESSION_NODE) {

      PathExpression expression = expressionNodeEntity.getExpression();
      if (expression instanceof EqualsExpression) {

        if (object instanceof ObjectNode) {
          EqualsExpression equalsExpression = (EqualsExpression) expression;
          boolean verified = verifyExpression((ObjectNode) object, equalsExpression);
          if (verified) {
            if (isArr) {
              result = doSinglePathExpressionParse(
                  expressionNodeEntityList, startIndex + 1, endIndex,
                  object, false);
            } else {
              // determine whether it is the last node. If it is not the last node, it is invalid.
              if (startIndex == endIndex - 1) {
                List<String> leftValue = equalsExpression.getLeftValue();
                LinkedList<ExpressionNodeEntity> expressionNodeEntities = new LinkedList<>();
                for (String pathName : leftValue) {
                  expressionNodeEntities.add(
                      new ExpressionNodeEntity(pathName, ExpressionNodeType.NAME_NODE));
                }
                result = new LinkedList<>();
                result.add(expressionNodeEntities);
              }
            }

          }
        } else if (object instanceof ArrayNode) {
          ArrayNode arrayNode = (ArrayNode) object;
          for (int i = 0; i < arrayNode.size(); i++) {
            Object nextObj = arrayNode.get(i);
            LinkedList<LinkedList<ExpressionNodeEntity>> localPath = doSinglePathExpressionParse(
                expressionNodeEntityList, startIndex, endIndex,
                nextObj, true);

            if (localPath != null) {
              if (result == null) {
                result = new LinkedList<>();
              }
              result.addAll(appendExpressionIndexNode(localPath, i));
            }
          }
        } else {
          // todo: %value%
        }
      }

    } else if (nodeType == ExpressionNodeType.INDEX_NODE) {

      if (object instanceof ArrayNode) {
        int arrIndex = expressionNodeEntity.getIndex();
        ArrayNode arrayNode = (ArrayNode) object;
        Object nextObj = arrayNode.get(arrIndex);
        LinkedList<LinkedList<ExpressionNodeEntity>> localPath = doSinglePathExpressionParse(
            expressionNodeEntityList, startIndex + 1, endIndex, nextObj, true);
        result = appendExpressionIndexNode(localPath, arrIndex);

      } else {
        // If invalid, localPath is discarded.
      }

    } else {
      // unknown node type, no processing
    }

    return result;
  }

  private boolean verifyExpression(ObjectNode objectNode, EqualsExpression expressionNodeEntity) {
    List<String> leftValue = expressionNodeEntity.getLeftValue();
    String valueFormPath = getValueFormPath(objectNode, leftValue);
    String equalsValue = expressionNodeEntity.getRightValue();
    return Objects.equals(valueFormPath, equalsValue);
  }

  private LinkedList<LinkedList<ExpressionNodeEntity>> appendExpressionNameNode(
      LinkedList<LinkedList<ExpressionNodeEntity>> localPath,
      String nodeName) {

    LinkedList<LinkedList<ExpressionNodeEntity>> result = new LinkedList<>();

    if (localPath == null) {
      return null;
    }

    if (localPath.isEmpty()) {
      LinkedList<ExpressionNodeEntity> expressionList = new LinkedList<>();
      expressionList.add(new ExpressionNodeEntity(nodeName, ExpressionNodeType.NAME_NODE));
      result.add(expressionList);
      return result;
    }

    for (LinkedList<ExpressionNodeEntity> expressionNodeEntityList : localPath) {
      if (expressionNodeEntityList != null) {
        expressionNodeEntityList.addFirst(
            new ExpressionNodeEntity(nodeName, ExpressionNodeType.NAME_NODE));
        result.add(expressionNodeEntityList);
      }
    }
    return result;
  }

  private LinkedList<LinkedList<ExpressionNodeEntity>> appendExpressionIndexNode(
      LinkedList<LinkedList<ExpressionNodeEntity>> localPath, int index) {
    LinkedList<LinkedList<ExpressionNodeEntity>> result = new LinkedList<>();

    if (localPath == null) {
      return null;
    }

    if (localPath.isEmpty()) {
      LinkedList<ExpressionNodeEntity> expressionList = new LinkedList<>();
      expressionList.add(new ExpressionNodeEntity(index, ExpressionNodeType.INDEX_NODE));
      result.add(expressionList);
      return result;
    }

    for (LinkedList<ExpressionNodeEntity> expressionNodeEntityList : localPath) {
      if (expressionNodeEntityList != null) {
        expressionNodeEntityList.addFirst(
            new ExpressionNodeEntity(index, ExpressionNodeType.INDEX_NODE));
        result.add(expressionNodeEntityList);
      }
    }
    return result;
  }

  private String getValueFormPath(Object objectNode, List<String> pathList) {
    if (ListUti.isEmpty(pathList)) {
      return null;
    }

    try {
      for (String path : pathList) {
        objectNode = ((ObjectNode) objectNode).get(path);
      }
      return objectNode instanceof TextNode ? ((TextNode) objectNode).asText()
          : objectNode.toString();
    } catch (RuntimeException e) {
    }
    return null;
  }

}

