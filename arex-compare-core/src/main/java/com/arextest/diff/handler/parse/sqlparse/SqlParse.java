package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.arextest.diff.handler.parse.sqlparse.constants.DbParseConstants;
import com.arextest.diff.model.exception.SelectIgnoreException;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.NameConvertUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rchen9 on 2023/1/12.
 */
public class SqlParse {

  private static final Logger logger = LoggerFactory.getLogger(SqlParse.class);

  public void doHandler(MsgObjCombination msgObjCombination, boolean onlyCompareSameColumns,
      boolean selectIgnoreCompare, boolean nameToLower) throws SelectIgnoreException {
    Object baseObj = msgObjCombination.getBaseObj();
    Object testObj = msgObjCombination.getTestObj();
    if (baseObj == null || testObj == null) {
      return;
    }
    if (baseObj instanceof ObjectNode && testObj instanceof ObjectNode) {
      ObjectNode baseJSONObj = (ObjectNode) baseObj;
      ObjectNode testJSONObj = (ObjectNode) testObj;

      // Only compare fields with the same name and parameters use positional subscripts,
      // fill the parameters field into sql and remove the parameters
      if (onlyCompareSameColumns) {
        try {
          if (judgeParam(baseJSONObj) && judgeParam(testJSONObj)) {
            produceNewBody(baseJSONObj, testJSONObj);
          }
        } catch (Throwable throwable) {
        }
      }

      ParsedResult baseParsedResult = this.sqlParse(baseJSONObj, nameToLower);
      ParsedResult testParsedResult = this.sqlParse(testJSONObj, nameToLower);
      if (Objects.equals(baseParsedResult.isSuccess(), false) ||
          Objects.equals(testParsedResult.isSuccess(), false)) {
        return;
      }
      if (selectIgnoreCompare) {
        List<Boolean> isSelectInBase = baseParsedResult.getIsSelect();
        List<Boolean> isSelectInTest = testParsedResult.getIsSelect();
        if (!isSelectInBase.isEmpty() && !isSelectInTest.isEmpty() &&
            isSelectInBase.get(0) && isSelectInTest.get(0)) {
          throw new SelectIgnoreException();
        }
      }
    }
  }

  // if return null, indicate the sql parsed fail.
  public ParsedResult sqlParse(ObjectNode jsonObj, boolean nameToLower) {
    JsonNode databaseBody = jsonObj.get(DbParseConstants.BODY);
    if (databaseBody == null) {
      return new ParsedResult(null, false);
    }

    // additional: Handling multiple selections
    databaseBody = processMultipleSelect(databaseBody);

    boolean successParse = true;
    ArrayNode parsedSql = JacksonHelperUtil.getArrayNode();
    List<Boolean> isSelect = new ArrayList<>();
    try {
      if (databaseBody instanceof TextNode) {
        MutablePair<JsonNode, Boolean> tempMutablePair = sqlParse(databaseBody.asText());
        parsedSql.add(tempMutablePair.getLeft());
        isSelect.add(tempMutablePair.getRight());
      } else if (databaseBody instanceof ArrayNode) {
        ArrayNode databaseBodyArray = (ArrayNode) databaseBody;
        for (int i = 0; i < databaseBodyArray.size(); i++) {
          MutablePair<JsonNode, Boolean> tempMutablePair = sqlParse(
              databaseBodyArray.get(i).asText());
          parsedSql.add(tempMutablePair.getLeft());
          isSelect.add(tempMutablePair.getRight());
        }
      } else {
        successParse = false;
      }
    } catch (Throwable throwable) {
      logger.warn("sql parse error", throwable);
      successParse = false;
    }

    ParsedResult result = new ParsedResult();
    if (!successParse) {
      this.fillOriginalSql(jsonObj, databaseBody);
      result.setSuccess(false);
    } else {
      if (nameToLower) {
        NameConvertUtil.nameConvert(parsedSql);
      }
      jsonObj.set(DbParseConstants.PARSED_SQL, parsedSql);
      result.setSuccess(true);
      result.setIsSelect(isSelect);
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public MutablePair<JsonNode, Boolean> sqlParse(String sql) throws JSQLParserException {
    Statement statement = CCJSqlParserUtil.parse(sql);
    Parse parse = ActionFactory.selectParse(statement);
    return new MutablePair<>(parse.parse(statement), statement instanceof Select);
  }

  private boolean judgeParam(ObjectNode object) {
    try {
      Object parameters = object.get(DbParseConstants.PARAMETERS);
      if (parameters != null) {
        if (parameters instanceof ObjectNode) {
          ObjectNode paramObj = (ObjectNode) parameters;
          return isPositionParam(paramObj);
        } else if (parameters instanceof ArrayNode) {
          ObjectNode paramObj = (ObjectNode) ((ArrayNode) parameters).get(0);
          return isPositionParam(paramObj);
        } else {
          return false;
        }
      }
    } catch (Throwable throwable) {
      logger.warn("judgeParam error: {}", throwable.getMessage());
    }
    return false;
  }

  private boolean isPositionParam(ObjectNode paramObj) {
    List<String> names = JacksonHelperUtil.getNames(paramObj);
    if (names.size() == 0) {
      return false;
    }
    Pattern pattern = Pattern.compile("^[0-9]*[1-9][0-9]*$");
    for (String name : names) {
      Matcher matcher = pattern.matcher(name);
      if (!matcher.matches()) {
        return false;
      }
    }
    return true;
  }

  // when onlyCompareSameColumns is true and use array subscripts,
  // generate a new database comparison message
  private void produceNewBody(ObjectNode baseJSONObj, ObjectNode testJSONObj) {

    JsonNode originalBaseParams = baseJSONObj.get(DbParseConstants.PARAMETERS);
    JsonNode originalTestParams = testJSONObj.get(DbParseConstants.PARAMETERS);
    String originalBaseBody = baseJSONObj.get(DbParseConstants.BODY).asText();
    String originalTestBody = testJSONObj.get(DbParseConstants.BODY).asText();
    if (!Objects.equals(originalBaseParams.getClass(), originalTestParams.getClass())) {
      return;
    }

    try {
      if (originalBaseParams instanceof ObjectNode) {
        ObjectNode originalBaseParamsObj = (ObjectNode) originalBaseParams;
        ObjectNode originalTestParamsObj = (ObjectNode) originalTestParams;
        String newBaseBody = processParams(originalBaseParamsObj, originalBaseBody);
        String newTestBody = processParams(originalTestParamsObj, originalTestBody);
        baseJSONObj.put(DbParseConstants.BODY, newBaseBody);
        testJSONObj.put(DbParseConstants.BODY, newTestBody);
      } else if (originalBaseParams instanceof ArrayNode) {
        ArrayNode originalBaseParamsArr = (ArrayNode) originalBaseParams;
        ArrayNode originalTestParamsArr = (ArrayNode) originalTestParams;

        ArrayNode newBaseBodyList = JacksonHelperUtil.getArrayNode();
        for (int i = 0; i < originalBaseParamsArr.size(); i++) {
          ObjectNode itemBaseParamObj = (ObjectNode) originalBaseParamsArr.get(i);
          String newBaseBody = processParams(itemBaseParamObj, originalBaseBody);
          newBaseBodyList.add(newBaseBody);
        }
        baseJSONObj.set(DbParseConstants.BODY, newBaseBodyList);

        ArrayNode newTestBodyList = JacksonHelperUtil.getArrayNode();
        for (int i = 0; i < originalTestParamsArr.size(); i++) {
          ObjectNode itemTestParamObj = (ObjectNode) originalTestParamsArr.get(i);
          String newTestBody = processParams(itemTestParamObj, originalTestBody);
          newTestBodyList.add(newTestBody);
        }
        testJSONObj.set(DbParseConstants.BODY, newTestBodyList);
      }
      baseJSONObj.remove(DbParseConstants.PARAMETERS);
      testJSONObj.remove(DbParseConstants.PARAMETERS);
    } catch (Throwable throwable) {
      logger.warn("produceNewBody error: {}", throwable.getMessage());
      baseJSONObj.set(DbParseConstants.PARAMETERS, originalBaseParams);
      testJSONObj.set(DbParseConstants.PARAMETERS, originalTestParams);
      baseJSONObj.put(DbParseConstants.BODY, originalBaseBody);
      testJSONObj.put(DbParseConstants.BODY, originalTestBody);
    }
  }


  // generate a new body object
  private String processParams(ObjectNode paramObj, String sql) {
    if (sql == null || sql.length() == 0) {
      return sql;
    }

    int count = 0;
    StringBuilder newSql = new StringBuilder();
    char[] sqlCharArr = sql.toCharArray();
    for (int i = 0; i < sqlCharArr.length; i++) {
      if (sqlCharArr[i] == '?') {
        Object paramItem = paramObj.get(String.valueOf(++count));
        if (paramItem == null) {
          newSql.append(paramItem);
        } else if (paramItem instanceof TextNode) {
          newSql.append("\'");
          newSql.append(((TextNode) paramItem).asText());
          newSql.append("\'");
        } else {
          newSql.append(paramItem);
        }
      } else {
        newSql.append(sqlCharArr[i]);
      }
    }
    return newSql.toString();
  }

  private void fillOriginalSql(ObjectNode objectNode, JsonNode databaseBody) {
    ObjectNode backUpObj = JacksonHelperUtil.getObjectNode();
    backUpObj.set(DbParseConstants.ORIGINAL_SQL, databaseBody);
    ArrayNode parsedSql = JacksonHelperUtil.getArrayNode();
    parsedSql.add(backUpObj);
    objectNode.set(DbParseConstants.PARSED_SQL, parsedSql);
  }

  private JsonNode processMultipleSelect(JsonNode databaseBody) {
    JsonNode result = databaseBody;
    if (databaseBody instanceof TextNode) {
      String sql = databaseBody.asText();
      if (sql.contains(";")) {
        String[] sqls = sql.split(";");
        ArrayNode arrayNode = JacksonHelperUtil.getArrayNode();
        for (String s : sqls) {
          if (!startsWithSelect(s)) {
            break;
          }
          arrayNode.add(s);
        }
        result = arrayNode;
      }

    }
    return result;
  }

  public static boolean startsWithSelect(String str) {
    if (str == null) {
      return false;
    }
    int len = str.length();
    int i = 0;
    // Skip leading whitespace
    while (i < len && Character.isWhitespace(str.charAt(i))) {
      i++;
    }
    // Check if the remaining string starts with "select" or "SELECT"
    return str.regionMatches(true, i, "select", 0, 6);
  }


  private static class ParsedResult {

    private List<Boolean> isSelect;
    private boolean success;

    public ParsedResult() {

    }

    public ParsedResult(boolean success) {
      this.success = success;
    }

    public ParsedResult(List<Boolean> isSelect) {
      this.isSelect = isSelect;
    }

    public ParsedResult(List<Boolean> isSelect, boolean success) {
      this.isSelect = isSelect;
      this.success = success;
    }

    public boolean isSuccess() {
      return success;
    }

    public void setSuccess(boolean success) {
      this.success = success;
    }

    public List<Boolean> getIsSelect() {
      return isSelect;
    }

    public void setIsSelect(List<Boolean> isSelect) {
      this.isSelect = isSelect;
    }
  }
}
