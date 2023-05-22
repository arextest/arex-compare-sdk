package com.arextest.diff.handler.parse.sqlparse.action;

import com.arextest.diff.handler.parse.sqlparse.Parse;
import com.arextest.diff.handler.parse.sqlparse.constants.Constants;
import com.arextest.diff.handler.parse.sqlparse.select.ArexItemsListVisitorAdapter;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.replace.Replace;

import java.util.List;

/**
 * Created by rchen9 on 2023/5/18.
 */
public class ReplaceParse implements Parse<Replace> {
    @Override
    public ObjectNode parse(Replace parseObj) {
        ObjectNode sqlObject = JacksonHelperUtil.getObjectNode();
        sqlObject.put(Constants.ACTION, Constants.REPLACE);

        // table parse
        Table table = parseObj.getTable();
        if (table != null) {
            sqlObject.put(Constants.TABLE, table.getFullyQualifiedName());
        }

        // columns parse
        List<Column> columns = parseObj.getColumns();
        if (columns != null && !columns.isEmpty()) {
            ObjectNode columnObj = JacksonHelperUtil.getObjectNode();
            ArrayNode values = JacksonHelperUtil.getArrayNode();
            ItemsList itemsList = parseObj.getItemsList();
            itemsList.accept(new ArexItemsListVisitorAdapter(values));
            for (int i = 0; i < columns.size(); i++) {
                JsonNode value = new TextNode("?");
                if (i < values.size()) {
                    value = values.get(i);
                }
                columnObj.set(columns.get(i).toString(), value);
            }
            sqlObject.set(Constants.COLUMNS, columnObj);
        }



        return null;
    }
}
