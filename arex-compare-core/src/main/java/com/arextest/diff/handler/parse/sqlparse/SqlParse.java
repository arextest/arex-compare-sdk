package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.arextest.diff.model.exception.SelectIgnoreException;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rchen9 on 2023/1/12.
 */
public class SqlParse {
    private static final String ORIGINAL_SQL = "originalSql";
    private static final String PARSED_SQL = "parsedSql";

    public void doHandler(MsgObjCombination msgObjCombination, boolean onlyCompareSameColumns,
                          boolean selectIgnoreCompare) throws SelectIgnoreException {
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

            // parse the body field, compatible with the case where the body is an array
            JsonNode baseDatabaseBody = baseJSONObj.get("body");
            JsonNode testDatabaseBody = testJSONObj.get("body");

            if (baseDatabaseBody == null || testDatabaseBody == null) {
                if (testDatabaseBody != null) {
                    fillOriginalSql(testJSONObj, testDatabaseBody);
                } else if (baseDatabaseBody != null) {
                    fillOriginalSql(baseJSONObj, baseDatabaseBody);
                }
                return;
            }

            ParsedResult baseParsedResult = sqlParse(baseDatabaseBody);
            ParsedResult testParsedResult = sqlParse(testDatabaseBody);
            if (baseParsedResult != null && testParsedResult != null) {
                baseJSONObj.set(PARSED_SQL, baseParsedResult.getParsedSql());
                testJSONObj.set(PARSED_SQL, testParsedResult.getParsedSql());
                List<Boolean> isSelectInBase = baseParsedResult.getIsSelect();
                List<Boolean> isSelectInTest = testParsedResult.getIsSelect();
                if (selectIgnoreCompare) {
                    if (!isSelectInBase.isEmpty() && !isSelectInTest.isEmpty() &&
                            isSelectInBase.get(0) && isSelectInTest.get(0)) {
                        throw new SelectIgnoreException();
                    }
                }
            } else {
                fillOriginalSql(baseJSONObj, baseDatabaseBody);
                fillOriginalSql(testJSONObj, testDatabaseBody);
            }
        }
    }

    // if return null, indicate the sql parsed fail.
    public ParsedResult sqlParse(JsonNode databaseBody) {
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
                    MutablePair<JsonNode, Boolean> tempMutablePair = sqlParse(databaseBodyArray.get(i).asText());
                    parsedSql.add(tempMutablePair.getLeft());
                    isSelect.add(tempMutablePair.getRight());
                }
            } else {
                return null;
            }
        } catch (Throwable throwable) {
            return null;
        }
        return new ParsedResult(parsedSql, isSelect);
    }

    @SuppressWarnings("unchecked")
    public MutablePair<JsonNode, Boolean> sqlParse(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        return new MutablePair<>(parse.parse(statement), statement instanceof Select);
    }

    private boolean judgeParam(ObjectNode object) {
        try {
            Object parameters = object.get("parameters");
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

        JsonNode originalBaseParams = baseJSONObj.get("parameters");
        JsonNode originalTestParams = testJSONObj.get("parameters");
        String originalBaseBody = baseJSONObj.get("body").asText();
        String originalTestBody = testJSONObj.get("body").asText();
        if (!Objects.equals(originalBaseParams.getClass(), originalTestParams.getClass())) {
            return;
        }

        try {
            if (originalBaseParams instanceof ObjectNode) {
                ObjectNode originalBaseParamsObj = (ObjectNode) originalBaseParams;
                ObjectNode originalTestParamsObj = (ObjectNode) originalTestParams;
                String newBaseBody = processParams(originalBaseParamsObj, originalBaseBody);
                String newTestBody = processParams(originalTestParamsObj, originalTestBody);
                baseJSONObj.put("body", newBaseBody);
                testJSONObj.put("body", newTestBody);
            } else if (originalBaseParams instanceof ArrayNode) {
                ArrayNode originalBaseParamsArr = (ArrayNode) originalBaseParams;
                ArrayNode originalTestParamsArr = (ArrayNode) originalTestParams;

                ArrayNode newBaseBodyList = JacksonHelperUtil.getArrayNode();
                for (int i = 0; i < originalBaseParamsArr.size(); i++) {
                    ObjectNode itemBaseParamObj = (ObjectNode) originalBaseParamsArr.get(i);
                    String newBaseBody = processParams(itemBaseParamObj, originalBaseBody);
                    newBaseBodyList.add(newBaseBody);
                }
                baseJSONObj.put("body", newBaseBodyList);

                ArrayNode newTestBodyList = JacksonHelperUtil.getArrayNode();
                for (int i = 0; i < originalTestParamsArr.size(); i++) {
                    ObjectNode itemTestParamObj = (ObjectNode) originalTestParamsArr.get(i);
                    String newTestBody = processParams(itemTestParamObj, originalTestBody);
                    newTestBodyList.add(newTestBody);
                }
                testJSONObj.put("body", newTestBodyList);
            }
            baseJSONObj.remove("parameters");
            testJSONObj.remove("parameters");
        } catch (Throwable throwable) {
            baseJSONObj.set("parameters", originalBaseParams);
            testJSONObj.set("parameters", originalTestParams);
            baseJSONObj.put("body", originalBaseBody);
            testJSONObj.put("body", originalTestBody);
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
        backUpObj.set(ORIGINAL_SQL, databaseBody);
        ArrayNode parsedSql = JacksonHelperUtil.getArrayNode();
        parsedSql.add(backUpObj);
        objectNode.set(PARSED_SQL, parsedSql);
    }

    private static class ParsedResult {
        public ParsedResult() {

        }

        public ParsedResult(ArrayNode parsedSql, List<Boolean> isSelect) {
            this.parsedSql = parsedSql;
            this.isSelect = isSelect;
        }

        private ArrayNode parsedSql;
        private List<Boolean> isSelect;

        public ArrayNode getParsedSql() {
            return parsedSql;
        }

        public List<Boolean> getIsSelect() {
            return isSelect;
        }
    }
}
