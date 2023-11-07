package com.arextest.diff.eigen;

import com.arextest.diff.handler.parse.JSONParse;
import com.arextest.diff.handler.parse.ObjectParse;
import com.arextest.diff.handler.parse.sqlparse.SqlParse;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.model.enumeration.CategoryType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.HashMap;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EigenHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(EigenHandler.class);

  private static ObjectParse objectParse = new ObjectParse();
  private static JSONParse jsonParse = new JSONParse();

  private static SqlParse sqlParse = new SqlParse();

  private static EigenMapCalculate eigenMapCalculate = new EigenMapCalculate();

  public EigenResult doHandler(RulesConfig rulesConfig) {
    Object obj = null;
    try {
      // if it is not json, it will return null
      obj = objectParse.msgToObj(rulesConfig.getBaseMsg(), rulesConfig);

      jsonParse.getJSONParseResult(obj, rulesConfig);

      if (Objects.equals(rulesConfig.getCategoryType(), CategoryType.DATABASE)
          && obj instanceof ObjectNode) {
        sqlParse.sqlParse((ObjectNode) obj, rulesConfig.isNameToLower());
      }
    } catch (Exception e) {
      LOGGER.error("EigenHandler doHandler error", e);
    }

    return eigenMapCalculate.doCalculate(obj, rulesConfig, new HashMap<>());
  }

}
