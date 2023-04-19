package com.arextest.diff.handler.parse.sqlparse.action;

import com.arextest.diff.handler.parse.sqlparse.Parse;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

/**
 * Created by rchen9 on 2023/1/6.
 */
public class ActionFactory {
    public static Parse selectParse(Statement statement) {
        if (statement instanceof Insert) {
            return new InsertParse();
        } else if (statement instanceof Delete) {
            return new DeleteParse();
        } else if (statement instanceof Update) {
            return new UpdateParse();
        } else if (statement instanceof Select) {
            return new SelectParse();
        } else {
            throw new UnsupportedOperationException("not support");
        }
    }
}
