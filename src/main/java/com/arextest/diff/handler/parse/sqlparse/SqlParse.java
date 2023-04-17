package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.arextest.diff.model.exception.SelectParseException;
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

    public void doHandler(MsgObjCombination msgObjCombination, boolean onlyCompareSameColumns) throws SelectParseException {
        Object baseObj = msgObjCombination.getBaseObj();
        Object testObj = msgObjCombination.getTestObj();
        if (baseObj == null || testObj == null) {
            return;
        }
        if (baseObj instanceof ObjectNode && testObj instanceof ObjectNode) {
            ObjectNode baseJSONObj = (ObjectNode) baseObj;
            ObjectNode testJSONObj = (ObjectNode) testObj;

            // 仅比较同名字段且parameters采用位置下标，将parameters字段填入sql并移除
            if (onlyCompareSameColumns) {
                try {
                    if (judgeParam(baseJSONObj) && judgeParam(testJSONObj)) {
                        produceNewBody(baseJSONObj, testJSONObj);
                    }
                } catch (Throwable throwable) {
                }
            }

            // 解析body字段，注意body为数组的情况
            JsonNode baseDatabaseBody = baseJSONObj.get("body");
            JsonNode testDatabaseBody = testJSONObj.get("body");
            ArrayNode parsedBaseSql = JacksonHelperUtil.getArrayNode();
            ArrayNode parsedTestSql = JacksonHelperUtil.getArrayNode();

            if (baseDatabaseBody == null || testDatabaseBody == null) {
                if (testDatabaseBody != null) {
                    ObjectNode testBackUpObj = JacksonHelperUtil.getObjectNode();
                    testBackUpObj.set(ORIGINAL_SQL, testDatabaseBody);
                    parsedTestSql.add(testBackUpObj);
                    testJSONObj.set(PARSED_SQL, parsedTestSql);
                } else if (baseDatabaseBody != null) {
                    ObjectNode baseBackUpObj = JacksonHelperUtil.getObjectNode();
                    baseBackUpObj.set(ORIGINAL_SQL, baseDatabaseBody);
                    parsedBaseSql.add(baseBackUpObj);
                    baseJSONObj.set(PARSED_SQL, parsedBaseSql);
                }
                return;
            }

            try {
                if (baseDatabaseBody instanceof TextNode) {

                    parsedBaseSql.add(sqlParse(baseDatabaseBody.asText()));
                    parsedTestSql.add(sqlParse(testDatabaseBody.asText()));

                } else {
                    ArrayNode baseDatabaseBodyArray = (ArrayNode) baseDatabaseBody;
                    ArrayNode testDatabaseBodyArray = (ArrayNode) testDatabaseBody;

                    for (int i = 0; i < baseDatabaseBodyArray.size(); i++) {
                        String itemBaseDatabaseBody = baseDatabaseBodyArray.get(i).asText();
                        parsedBaseSql.add(sqlParse(itemBaseDatabaseBody));
                    }

                    for (int i = 0; i < baseDatabaseBodyArray.size(); i++) {
                        String itemTestDatabaseBody = testDatabaseBodyArray.get(i).asText();
                        parsedTestSql.add(sqlParse(itemTestDatabaseBody));
                    }
                }

                baseJSONObj.set(PARSED_SQL, parsedBaseSql);
                testJSONObj.set(PARSED_SQL, parsedTestSql);
            } catch (SelectParseException exception) {
                throw exception;
            } catch (Throwable throwable) {

                ObjectNode baseBackUpObj = JacksonHelperUtil.getObjectNode();
                ObjectNode testBackUpObj = JacksonHelperUtil.getObjectNode();
                baseBackUpObj.set(ORIGINAL_SQL, baseDatabaseBody);
                testBackUpObj.set(ORIGINAL_SQL, testDatabaseBody);

                parsedBaseSql.add(baseBackUpObj);
                parsedTestSql.add(testBackUpObj);

                baseJSONObj.set(PARSED_SQL, parsedBaseSql);
                testJSONObj.set(PARSED_SQL, parsedTestSql);
            }


        }
    }

    @SuppressWarnings("unchecked")
    public ObjectNode sqlParse(String sql) throws JSQLParserException, SelectParseException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        if (statement instanceof Select) {
            throw new SelectParseException();
        }
        Parse parse = ActionFactory.selectParse(statement);
        return parse.parse(statement);
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

    // onlyCompareSameColumns为true, 且采用数组下标时重构数据库比对报文
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


    // 产生新的body对象
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

}
