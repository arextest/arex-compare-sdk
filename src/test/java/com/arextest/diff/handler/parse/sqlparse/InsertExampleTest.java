package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.arextest.diff.model.exception.SelectIgnoreException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.Test;

/**
 * Created by rchen9 on 2023/1/11.
 */
public class InsertExampleTest {
    @Test
    public void testInsert() throws JSQLParserException {
        String sql = "insert into stadium(visit_date, people)\n" +
                "values\n" +
                "('2017-01-07' , 199)\n" +
                ",('2017-01-09' , 188)";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testInsert2() throws JSQLParserException {
        String sql = "INSERT  INTO category_stage (\n" +
                "   SELECT \n" +
                "      *\n" +
                "   FROM category );";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testInsert3() throws JSQLParserException {
        String sql = "INSERT INTO MyTable (Text) VALUES ('A'||CHAR(10)||'B')";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testInsert4() throws JSQLParserException {
        String sql = "INSERT INTO users SET id = 123, name = '姚明', age = 25;";
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
