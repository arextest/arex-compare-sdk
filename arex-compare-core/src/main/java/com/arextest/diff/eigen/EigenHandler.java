package com.arextest.diff.eigen;

import com.arextest.diff.handler.parse.JSONParse;
import com.arextest.diff.handler.parse.ObjectParse;
import com.arextest.diff.handler.parse.sqlparse.SqlParse;
import com.arextest.diff.handler.pathparse.JsonPathExpressionHandler;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EigenHandler {

  private static ObjectParse objectParse = new ObjectParse();
  private static JSONParse jsonParse = new JSONParse();
  private static SqlParse sqlParse = new SqlParse();
  private static JsonPathExpressionHandler jsonPathExpressionHandler = new JsonPathExpressionHandler();
  private static EigenMapCalculate eigenMapCalculate = new EigenMapCalculate();

  public EigenResult doHandler(RulesConfig rulesConfig) {
    Object obj;
    try {
      // if it is not json, it will return null
      obj = objectParse.msgToObj(rulesConfig.getBaseMsg(), rulesConfig);

      if (obj instanceof JsonNode) {
        jsonParse.getJSONParseResult(obj, rulesConfig);
      }

      if (Objects.equals(rulesConfig.getCategoryType(), CategoryType.DATABASE)
          && obj instanceof ObjectNode) {
        sqlParse.sqlParse((ObjectNode) obj, rulesConfig.isNameToLower());
      }
    } catch (RuntimeException e) {
      obj = null;
    }

    List<List<ExpressionNodeEntity>> expressionExclusions = new LinkedList<>();
    Map<List<ExpressionNodeEntity>, LinkedList<LinkedList<ExpressionNodeEntity>>> listLinkedListMap =
        jsonPathExpressionHandler.doMultiExpressionParse(
            rulesConfig.getExpressionExclusions(),
            obj);
    listLinkedListMap.values().forEach(expressionExclusions::addAll);
    rulesConfig.setExpressionExclusions(expressionExclusions);
    return eigenMapCalculate.doCalculate(obj, rulesConfig, new HashMap<>());
  }

}
