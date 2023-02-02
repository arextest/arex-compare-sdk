package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.arextest.diff.model.parse.MsgObjCombination;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rchen9 on 2023/1/12.
 */
public class SqlParse {
    private static final String ORIGINAL_SQL = "originalSql";
    private static final String PARSED_SQL = "parsedSql";

    public void doHandler(MsgObjCombination msgObjCombination, boolean onlyCompareSameColumns) {
        Object baseObj = msgObjCombination.getBaseObj();
        Object testObj = msgObjCombination.getTestObj();
        if (baseObj == null || testObj == null) {
            return;
        }
        if (baseObj instanceof JSONObject && testObj instanceof JSONObject) {
            JSONObject baseJSONObj = (JSONObject) baseObj;
            JSONObject testJSONObj = (JSONObject) testObj;

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
            Object baseDatabaseBody = null;
            Object testDatabaseBody = null;
            JSONArray parsedBaseSql = new JSONArray();
            JSONArray parsedTestSql = new JSONArray();
            try {
                baseDatabaseBody = baseJSONObj.get("body");
                testDatabaseBody = testJSONObj.get("body");

                if (baseDatabaseBody instanceof String) {

                    parsedBaseSql.put(sqlParse((String) baseDatabaseBody));
                    parsedTestSql.put(sqlParse((String) testDatabaseBody));

                } else {
                    JSONArray baseDatabaseBodyArray = (JSONArray) baseDatabaseBody;
                    JSONArray testDatabaseBodyArray = (JSONArray) testDatabaseBody;

                    for (int i = 0; i < baseDatabaseBodyArray.length(); i++) {
                        String itemBaseDatabaseBody = baseDatabaseBodyArray.getString(i);
                        parsedBaseSql.put(sqlParse(itemBaseDatabaseBody));
                    }

                    for (int i = 0; i < baseDatabaseBodyArray.length(); i++) {
                        String itemTestDatabaseBody = testDatabaseBodyArray.getString(i);
                        parsedTestSql.put(sqlParse(itemTestDatabaseBody));
                    }
                }

                baseJSONObj.put(PARSED_SQL, parsedBaseSql);
                testJSONObj.put(PARSED_SQL, parsedTestSql);


            } catch (Throwable throwable) {

                JSONObject baseBackUpObj = new JSONObject();
                JSONObject testBackUpObj = new JSONObject();
                baseBackUpObj.put(ORIGINAL_SQL, baseDatabaseBody);
                testBackUpObj.put(ORIGINAL_SQL, testDatabaseBody);

                parsedBaseSql.put(baseBackUpObj);
                parsedTestSql.put(testBackUpObj);

                baseJSONObj.put(PARSED_SQL, parsedBaseSql);
                testJSONObj.put(PARSED_SQL, parsedTestSql);
            }


        }
    }

    @SuppressWarnings("unchecked")
    public JSONObject sqlParse(String sql) throws JSQLParserException {
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        ;
        return parse.parse(statement);
    }

    private boolean judgeParam(JSONObject object) {
        try {
            Object parameters = object.get("parameters");
            if (parameters instanceof JSONObject) {
                JSONObject paramObj = (JSONObject) parameters;
                return isPositionParam(paramObj);
            } else if (parameters instanceof JSONArray) {
                JSONObject paramObj = (JSONObject) ((JSONArray) parameters).get(0);
                return isPositionParam(paramObj);
            } else {
                return false;
            }
        } catch (Throwable throwable) {
        }
        return false;
    }

    private boolean isPositionParam(JSONObject paramObj) {
        String[] names = JSONObject.getNames(paramObj);
        if (names == null || names.length == 0) {
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
    private void produceNewBody(JSONObject baseJSONObj, JSONObject testJSONObj) {

        Object originalBaseParams = baseJSONObj.get("parameters");
        Object originalTestParams = testJSONObj.get("parameters");
        String originalBaseBody = baseJSONObj.getString("body");
        String originalTestBody = testJSONObj.getString("body");
        if (!Objects.equals(originalBaseParams.getClass(), originalTestParams.getClass())) {
            return;
        }

        try {
            if (originalBaseParams instanceof JSONObject) {
                JSONObject originalBaseParamsObj = (JSONObject) originalBaseParams;
                JSONObject originalTestParamsObj = (JSONObject) originalTestParams;
                String newBaseBody = processParams(originalBaseParamsObj, originalBaseBody);
                String newTestBody = processParams(originalTestParamsObj, originalTestBody);
                baseJSONObj.put("body", newBaseBody);
                testJSONObj.put("body", newTestBody);
            } else if (originalBaseParams instanceof JSONArray) {
                JSONArray originalBaseParamsArr = (JSONArray) originalBaseParams;
                JSONArray originalTestParamsArr = (JSONArray) originalTestParams;

                JSONArray newBaseBodyList = new JSONArray();
                for (int i = 0; i < originalBaseParamsArr.length(); i++) {
                    JSONObject itemBaseParamObj = originalBaseParamsArr.getJSONObject(i);
                    String newBaseBody = processParams(itemBaseParamObj, originalBaseBody);
                    newBaseBodyList.put(newBaseBody);
                }
                baseJSONObj.put("body", newBaseBodyList);

                JSONArray newTestBodyList = new JSONArray();
                for (int i = 0; i < originalTestParamsArr.length(); i++) {
                    JSONObject itemTestParamObj = originalTestParamsArr.getJSONObject(i);
                    String newTestBody = processParams(itemTestParamObj, originalTestBody);
                    newTestBodyList.put(newTestBody);
                }
                testJSONObj.put("body", newTestBodyList);
            }
            baseJSONObj.remove("parameters");
            testJSONObj.remove("parameters");
        } catch (Throwable throwable) {
            baseJSONObj.put("parameters", originalBaseParams);
            testJSONObj.put("parameters", originalTestParams);
            baseJSONObj.put("body", originalBaseBody);
            testJSONObj.put("body", originalTestBody);
        }


    }


    // 产生新的body对象
    private String processParams(JSONObject paramObj, String sql) {
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
                } else if (paramItem instanceof String) {
                    newSql.append("\'");
                    newSql.append(paramItem);
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

    public static void main(String[] args) {

        SqlParse sqlParse = new SqlParse();

        String sql1 = "{\"database\":\"\",\"body\":\"INSERT INTO MyTable (Text) VALUES ('A'||CHAR(10)||'B')\"}";
        String sql2 = "{\"database\":\"\",\"body\":\"UPDATE `issuebillprocess` SET `IssueBillID`=?, `ProcessStatus`=?, `ProcessRemark`=? WHERE `BillProcessID`=?\"}";

        JSONObject obj1 = new JSONObject(sql1);
        JSONObject obj2 = new JSONObject(sql2);
        MsgObjCombination msgObjCombination = new MsgObjCombination();
        msgObjCombination.setBaseObj(obj1);
        msgObjCombination.setTestObj(obj2);

        // sqlParse.doHandler(msgObjCombination);
        System.out.println();
    }

}
