package com.arextest.diff.handler.parse.sqlparse.action;

import com.arextest.diff.handler.parse.sqlparse.Parse;
import com.arextest.diff.handler.parse.sqlparse.constants.Constants;
import com.arextest.diff.handler.parse.sqlparse.select.ArexExpressionVisitorAdapter;
import com.arextest.diff.handler.parse.sqlparse.select.ArexOrderByVisitorAdapter;
import com.arextest.diff.handler.parse.sqlparse.select.utils.JoinParseUtil;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.OrderByElement;

import java.util.List;

/**
 * Created by rchen9 on 2023/1/6.
 */
public class DeleteParse implements Parse<Delete> {
    @Override
    public ObjectNode parse(Delete parseObj) {
        ObjectNode sqlObject = JacksonHelperUtil.getObjectNode();
        sqlObject.put(Constants.ACTION, Constants.DELETE);

        // tables parse
        List<Table> tables = parseObj.getTables();
        if (tables != null && !tables.isEmpty()) {
            ObjectNode delTableObj = JacksonHelperUtil.getObjectNode();
            tables.forEach(item -> {
                delTableObj.put(item.getFullyQualifiedName(), Constants.EMPTY);
            });
            sqlObject.put(Constants.DEL_TABLES, delTableObj);
        }

        // table parse
        Table table = parseObj.getTable();
        if (table != null) {
            sqlObject.put(Constants.TABLE, table.getFullyQualifiedName());
        }

        // join parse
        List<Join> joins = parseObj.getJoins();
        if (joins != null && !joins.isEmpty()) {
            ArrayNode joinArr = JacksonHelperUtil.getArrayNode();
            joins.forEach(item -> {
                joinArr.add(JoinParseUtil.parse(item));
            });
            sqlObject.put(Constants.JOIN, joinArr);
        }

        // where parse
        Expression where = parseObj.getWhere();
        if (where != null) {
            ObjectNode whereObj = JacksonHelperUtil.getObjectNode();
            whereObj.put(Constants.AND_OR, JacksonHelperUtil.getArrayNode());
            whereObj.put(Constants.COLUMNS, JacksonHelperUtil.getObjectNode());

            where.accept(new ArexExpressionVisitorAdapter(whereObj));
            sqlObject.put(Constants.WHERE, whereObj);
        }

        // orderby parse
        List<OrderByElement> orderByElements = parseObj.getOrderByElements();
        if (orderByElements != null && !orderByElements.isEmpty()) {
            ObjectNode orderByObj = JacksonHelperUtil.getObjectNode();
            ArexOrderByVisitorAdapter arexOrderByVisitorAdapter = new ArexOrderByVisitorAdapter(orderByObj);
            orderByElements.forEach(item -> {
                item.accept(arexOrderByVisitorAdapter);
            });
            sqlObject.put(Constants.ORDER_BY, orderByObj);
        }

        // limit parse
        Limit limit = parseObj.getLimit();
        if (limit != null) {
            sqlObject.put(Constants.LIMIT, limit.toString());
        }
        return sqlObject;
    }
}
