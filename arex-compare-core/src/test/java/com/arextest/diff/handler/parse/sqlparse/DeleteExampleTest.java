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
public class DeleteExampleTest {
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
        testSqlParse(statement);
    }

    @Test
    public void testDelete2() throws JSQLParserException {
        String sql = "DELETE \n" +
                "FROM t8\n" +
                "   ORDER BY age DESC\n" +
                "   LIMIT 5;";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testDelete3() throws JSQLParserException {
        String sql = "DELETE T1,T2\n" +
                "FROM T1 \n" +
                "   INNER JOIN T2 ON T1.student_id = T2.student.id\n" +
                "WHERE T1.student_id = 2;";
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
