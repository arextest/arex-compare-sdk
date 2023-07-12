package com.arextest.diff.handler.parse.sqlparse.select;

import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.RowConstructor;
import net.sf.jsqlparser.expression.StringValue;
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
        if (expressions.size() > 0 && expressions.get(0) instanceof RowConstructor) {
            for (Expression expression : expressions) {
                ExpressionList exprList = ((RowConstructor) expression).getExprList();
                ArrayNode arrayNode = JacksonHelperUtil.getArrayNode();
                for (Expression expressionItem : exprList.getExpressions()) {
                    arrayNode.add(expressionToString(expressionItem));
                }
                sqlArr.add(arrayNode);
            }
        } else {
            ArrayNode arrayNode = JacksonHelperUtil.getArrayNode();
            for (Expression expression : expressions) {
                arrayNode.add(expressionToString(expression));
            }
            sqlArr.add(arrayNode);
        }
    }

    @Override
    public void visit(NamedExpressionList namedExpressionList) {

    }

    @Override
    public void visit(MultiExpressionList multiExprList) {
        List<ExpressionList> expressionLists = multiExprList.getExpressionLists();
        for (ExpressionList expressionList : expressionLists) {
            ArrayNode arrayNode = JacksonHelperUtil.getArrayNode();
            List<Expression> expressions = expressionList.getExpressions();
            for (Expression expression : expressions) {
                arrayNode.add(expressionToString(expression));
            }
            sqlArr.add(arrayNode);
        }
    }

    private String expressionToString(Expression expression) {
        if (expression instanceof StringValue) {
            return ((StringValue) expression).getValue();
        } else {
            return expression.toString();
        }
    }
}
