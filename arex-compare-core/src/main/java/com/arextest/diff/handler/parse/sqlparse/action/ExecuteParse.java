package com.arextest.diff.handler.parse.sqlparse.action;

import com.arextest.diff.handler.parse.sqlparse.Parse;
import com.arextest.diff.handler.parse.sqlparse.constants.Constants;
import com.arextest.diff.handler.parse.sqlparse.select.ArexExpressionVisitorAdapter;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.execute.Execute;

import java.util.List;

public class ExecuteParse implements Parse<Execute> {
    @Override
    public ObjectNode parse(Execute parseObj) {
        ObjectNode sqlObject = JacksonHelperUtil.getObjectNode();
        sqlObject.put(Constants.ACTION, Constants.EXECUTE);

        // execute name parse
        String executeName = parseObj.getName();
        if (executeName != null) {
            sqlObject.put(Constants.EXECUTE_NAME, executeName);
        }

        // expressions parse
        ExpressionList exprList = parseObj.getExprList();
        if (exprList != null) {

            List<Expression> expressions = exprList.getExpressions();

            if (expressions != null && !expressions.isEmpty()) {
                ArrayNode sqlColumnArr = JacksonHelperUtil.getArrayNode();

                ObjectNode setColumnObj = JacksonHelperUtil.getObjectNode();
                setColumnObj.set(Constants.AND_OR, JacksonHelperUtil.getArrayNode());
                setColumnObj.set(Constants.COLUMNS, JacksonHelperUtil.getObjectNode());
                for (Expression expression : expressions) {
                    expression.accept(new ArexExpressionVisitorAdapter(setColumnObj));
                }
                sqlColumnArr.add(setColumnObj);
                sqlObject.set(Constants.COLUMNS, sqlColumnArr);
            }
        }

        return sqlObject;
    }
}
