package com.arextest.diff.handler.pathparse;

import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

public class JsonPathExpressionHandler {

//  public List<List<ExpressionNodeEntity>> doHandler(RulesConfig rulesConfig, Object baseObj, Object testObj) {
//    List<List<ExpressionNodeEntity>> expressionExclusions = rulesConfig.getExpressionExclusions();
//    doExpressionParse()
//
//    return null;
//  }

  private List<List<ExpressionNodeEntity>> doExpressionParse(RulesConfig rulesConfig,
      Object baseObj, Object testObj) {
    List<List<ExpressionNodeEntity>> expressionExclusions = rulesConfig.getExpressionExclusions();
    List<List<ExpressionNodeEntity>> leftExpression = doSingleExpressionParse(expressionExclusions,
        baseObj);
    List<List<ExpressionNodeEntity>> rightExpression = doSingleExpressionParse(expressionExclusions,
        testObj);

    return null;
  }

  private List<List<ExpressionNodeEntity>> doSingleExpressionParse(
      List<List<ExpressionNodeEntity>> expressionExclusions, Object object) {
    for (List<ExpressionNodeEntity> expressionNodeEntityList : expressionExclusions) {

      List<List<ExpressionNodeEntity>> parsedExpressionNodeEntityList = new ArrayList<>();

      doSinglePathExpressionParse(expressionNodeEntityList, object, parsedExpressionNodeEntityList);

//      List<ExpressionNodeEntity> parsedExpressionNodeEntityList = new ArrayList<>();
//      for (ExpressionNodeEntity expressionNodeEntity : expressionNodeEntityList) {
//
//        if (expressionNodeEntity.getNodeType() == ExpressionNodeType.EXPRESSION_NODE
//            || expressionNodeEntity.getNodeType() == ExpressionNodeType.INDEX_NODE) {
//
//        } else {
//
//          String nodeName = expressionNodeEntity.getNodeName();
//          if (object instanceof ObjectNode) {
//            object = ((ObjectNode) object).get(nodeName);
//          } else if (object instanceof ArrayNode) {
//
//          } else {
//
//          }
//
//          parsedExpressionNodeEntityList.add(expressionNodeEntity);
//        }
//      }

    }

    return null;
  }

  // 每层都会产生全新的数据，在遍历结束后将数据添加进去
  // 有效和无效的遍历，全新数据的添加

  private void doSinglePathExpressionParse(List<ExpressionNodeEntity> expressionNodeEntityList,
      int startIndex, int endIndex, Object object,
      List<List<ExpressionNodeEntity>> parsedExpressionNodeEntityList) {

    if (startIndex > endIndex) {
      return;
    }

    if (object == null) {
      // parsedExpressionNodeEntityList是否要置为空，
      // 考虑array情况，是否部分存在
      return;
    }

    ExpressionNodeEntity expressionNodeEntity = expressionNodeEntityList.get(startIndex);
    int nodeType = expressionNodeEntity.getNodeType();

    if (nodeType == ExpressionNodeType.NAME_NODE) {

      String nodeName = expressionNodeEntity.getNodeName();
      if (object instanceof ObjectNode) {
        Object nextObj = ((ObjectNode) object).get(nodeName);
        // ...增加路径
        doSinglePathExpressionParse(expressionNodeEntityList, startIndex + 1, endIndex,
            nextObj, parsedExpressionNodeEntityList);
      } else if (object instanceof ArrayNode) {

        // 模糊路径的情况
        ArrayNode arrayNode = (ArrayNode) object;
        for (int i = 0; i < arrayNode.size(); i++) {
          Object nextObj = arrayNode.get(i);
          // ...增加路径
          doSinglePathExpressionParse(expressionNodeEntityList, startIndex + 1, endIndex,
              nextObj, parsedExpressionNodeEntityList);
        }

      } else {
        // 无效的情况

      }


    } else if (nodeType == ExpressionNodeType.EXPRESSION_NODE) {
//      PathExpression expression = expressionNodeEntity.getExpression();

    } else if (nodeType == ExpressionNodeType.INDEX_NODE) {

      if (object instanceof ArrayNode) {
        int arrIndex = expressionNodeEntity.getIndex();
        ArrayNode arrayNode = (ArrayNode) object;
        arrayNode.get();

      } else {
        // parsedExpressionNodeEntityList是否要置为空
        return;
      }

    } else {

      String nodeName = expressionNodeEntity.getNodeName();
      if (object instanceof ObjectNode) {
        Object nextObj = ((ObjectNode) object).get(nodeName);
        // ...增加路径
        doSinglePathExpressionParse(expressionNodeEntityList, startIndex + 1, endIndex,
            nextObj, parsedExpressionNodeEntityList);
      } else if (object instanceof ArrayNode) {
        ArrayNode arrayNode = (ArrayNode) object;
        for (int i = 0; i < arrayNode.size(); i++) {
          Object nextObj = arrayNode.get(i);
          // ...增加路径
          doSinglePathExpressionParse(expressionNodeEntityList, startIndex + 1, endIndex,
              nextObj, parsedExpressionNodeEntityList);
        }


      } else {

      }

//      expressionNodeEntity.setNodeValue(object);
    }


  }


}

