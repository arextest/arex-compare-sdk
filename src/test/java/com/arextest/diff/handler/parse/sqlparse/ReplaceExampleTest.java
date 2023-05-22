package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.Test;

/**
 * Created by rchen9 on 2023/5/18.
 */
public class ReplaceExampleTest {

    @Test
    public void testReplace() throws JSQLParserException {
        String sql = "REPLACE INTO fltordershardstrategy(OrderId, MainOrderId, DataChange_LastTime, userdata_location) " +
                "VALUES (36768383786, 36768317034, '2023-05-14 18:00:34.556', '')," +
                "(36768317034, 36768317034, '2023-05-14 18:00:34.556', '')";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    private static void testSqlParse(Statement statement) {
        Parse parse = null;
        parse = ActionFactory.selectParse(statement);
        ObjectNode jsonObject = (ObjectNode) parse.parse(statement);
        System.out.println();
    }
}
