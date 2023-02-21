package com.arextest.diff.handler.parse.sqlparse.select;

import com.fasterxml.jackson.databind.node.ArrayNode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NamedExpressionList;
import net.sf.jsqlparser.statement.select.SubSelect;

import java.util.List;

/**
 * Created by rchen9 on 2023/2/1.
 */
public class ArexItemsListVisitorAdapter implements ItemsListVisitor {

    private ArrayNode sqlArr;

    public ArexItemsListVisitorAdapter(ArrayNode array) {
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
                sqlArr.add(expression.toString());
            }
        } else {
            for (Expression expression : expressions) {
                sqlArr.add(expression.toString());
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
