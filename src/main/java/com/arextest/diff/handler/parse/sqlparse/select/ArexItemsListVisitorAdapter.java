package com.arextest.diff.handler.parse.sqlparse.select;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NamedExpressionList;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/1.
 */
public class ArexItemsListVisitorAdapter implements ItemsListVisitor {

    private JSONArray sqlArr;

    public ArexItemsListVisitorAdapter(JSONArray array) {
        sqlArr = array;
    }

    @Override
    public void visit(SubSelect subSelect) {

    }

    @Override
    public void visit(ExpressionList expressionList) {
        List<Expression> expressions = expressionList.getExpressions();
        // 仅考虑values只有一个的情况
        if (expressions.get(0) instanceof RowConstructor) {
            Expression expression1 = expressions.get(0);
            ExpressionList exprList = ((RowConstructor) expression1).getExprList();
            for (Expression expression : exprList.getExpressions()) {
                sqlArr.put(expression.toString());
            }
        } else {
            for (Expression expression : expressions) {
                sqlArr.put(expression.toString());
            }
        }
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {

    }

    @Override
    public void visit(MultiExpressionList multiExprList) {

    }
}
