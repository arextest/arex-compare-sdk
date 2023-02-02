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
public class UpdateExampleTest {

    @Test
    public void testUpdate1() throws JSQLParserException {
        String sql = "UPDATE Websites \n" +
                "SET alexa='5000', country='USA' \n" +
                "WHERE name='菜鸟教程';";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testUpdate2() throws JSQLParserException {
        String sql = "update test set column = 1 order by id desc limit 2\n";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testUpdate3() throws JSQLParserException {
        String sql = "UPDATE tablea a\n" +
                "JOIN tableb b\n" +
                "SET a.val = b.val\n" +
                "WHERE b.id = a.id";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }

    @Test
    public void testUpdate4() throws JSQLParserException {
        String sql = "UPDATE `hotelpicture` SET `hotelid`=1026268, `title`='外观', `smallpicurl`='', `largepicurl`='', `description`='外观', `sort`=0, `newpicurl`='/0206f120009irgqljCA50.jpg', `pictype`=100, `position`='H', `typeid`=0, `sharpness`=null WHERE `id`=492752329";
        Statement statement = CCJSqlParserUtil.parse(sql);
        testSqlParse(statement);
    }
    
    private static void testSqlParse(Statement statement) {
        Parse parse = ActionFactory.selectParse(statement);
        JSONObject jsonObject = (JSONObject) parse.parse(statement);
        System.out.println();
    }
}
