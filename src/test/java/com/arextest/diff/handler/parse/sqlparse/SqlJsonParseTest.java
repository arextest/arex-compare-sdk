package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Created by rchen9 on 2023/1/11.
 */
public class SqlJsonParseTest {

    @Test
    public void testSelect1() throws JSQLParserException {
        String sql = " SELECT\n" +
                "  DEP,\n" +
                "  JOB,\n" +
                "  SAL\n" +
                "FROM EMPL\n" +
                "WHERE JOB <> 'M'\n" +
                "GROUP BY DEP, JOB\n" +
                "HAVING AVG(SAL) > 28000\n" +
                "ORDER BY 3 DESC\n" +
                "limit 10\n" +
                "offset 10;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }

    @Test
    public void testInsert1() throws JSQLParserException {
        String sql = "INSERT INTO MyTable (Text) VALUES ('A'||CHAR(10)||'B')";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }

    @Test
    public void testInsert2() throws JSQLParserException {
        String sql = "INSERT INTO users SET id = 123, name = '姚明', age = 25;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }

    @Test
    public void testDelete1() throws JSQLParserException {
        String sql = "DELETE \n" +
                "FROM Exam\n" +
                "WHERE S_date NOT IN \n" +
                "   (\n" +
                "      SELECT \n" +
                "         e2.maxdt\n" +
                "      FROM \n" +
                "         (\n" +
                "            SELECT \n" +
                "               Order_Id, Product_Id, Amt, MAX(S_date) AS maxdt\n" +
                "            FROM Exam\n" +
                "            GROUP BY \n" +
                "               Order_Id, \n" +
                "               Product_Id, \n" +
                "               Amt\n" +
                "         )  AS e2\n" +
                "   );";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }

    @Test
    public void testDelete2() throws JSQLParserException {
        String sql = "DELETE \n" +
                "FROM t8\n" +
                "   ORDER BY age DESC\n" +
                "   LIMIT 5;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }

    @Test
    public void testUpdate1() throws JSQLParserException {
        String sql = "update test set column = 1 order by id desc limit 2";
        Statement statement = CCJSqlParserUtil.parse(sql);
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }
}
