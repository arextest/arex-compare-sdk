package com.arextest.diff.handler.parse.sqlparse.action;

import com.arextest.diff.handler.parse.sqlparse.Parse;
import com.arextest.diff.handler.parse.sqlparse.constants.Constants;
import com.arextest.diff.handler.parse.sqlparse.select.ArexItemsListVisitorAdapter;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by rchen9 on 2023/1/6.
 */
public class InsertParse implements Parse<Insert> {
    @Override
    public JSONObject parse(Insert parseObj) {
        JSONObject sqlObject = new JSONObject();
        sqlObject.put(Constants.ACTION, Constants.INSERT);

        // table parse
        Table table = parseObj.getTable();
        if (table != null) {
            sqlObject.put(Constants.TABLE, table.getFullyQualifiedName());
        }

        // columns parse
        List<Column> columns = parseObj.getColumns();
        if (columns != null && !columns.isEmpty()) {
            JSONObject columnObj = new JSONObject();

            JSONArray values = new JSONArray();
            ItemsList itemsList = parseObj.getItemsList();
            itemsList.accept(new ArexItemsListVisitorAdapter(values));
            for (int i = 0; i < columns.size(); i++) {
                Object value = "?";
                if (i < values.length()) {
                    value = values.get(i);
                }
                columnObj.put(columns.get(i).toString(), value);
            }
            sqlObject.put(Constants.COLUMNS, columnObj);
        }

        // setColumns parse
        List<Column> setColumns = parseObj.getSetColumns();
        if (setColumns != null && !setColumns.isEmpty()) {
            JSONObject setColumnObj = new JSONObject();

            JSONArray values = new JSONArray();
            List<Expression> setExpressionList = parseObj.getSetExpressionList();
            for (Expression expression : setExpressionList) {
                values.put(expression.toString());
            }
            for (int i = 0; i < setColumns.size(); i++) {
                Object value = "?";
                if (i < values.length()) {
                    value = values.get(i);
                }
                setColumnObj.put(setColumns.get(i).toString(), value);
            }


            sqlObject.put(Constants.COLUMNS, setColumnObj);
        }

        return sqlObject;
    }
}
