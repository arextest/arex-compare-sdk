package com.arextest.diff.handler.parse.sqlparse.select;

import com.arextest.diff.handler.parse.sqlparse.constants.Constants;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import org.json.JSONObject;

/**
 * Created by rchen9 on 2023/1/6.
 */
public class ArexSelectItemVisitorAdapter implements SelectItemVisitor {

    private JSONObject sqlObject;

    public ArexSelectItemVisitorAdapter(JSONObject object) {
        sqlObject = object;
    }

    @Override
    public void visit(AllColumns allColumns) {
        sqlObject.put(allColumns.toString(), Constants.EMPTY);
    }

    @Override
    public void visit(AllTableColumns allTableColumns) {
        sqlObject.put(allTableColumns.toString(), Constants.EMPTY);
    }

    @Override
    public void visit(SelectExpressionItem selectExpressionItem) {
        sqlObject.put(selectExpressionItem.toString(), Constants.EMPTY);
    }
}
