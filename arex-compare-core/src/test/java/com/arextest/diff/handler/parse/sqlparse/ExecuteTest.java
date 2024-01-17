package com.arextest.diff.handler.parse.sqlparse;

import com.arextest.diff.handler.parse.sqlparse.action.ActionFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Test;

public class ExecuteTest {

  private static void testSqlParse(Statement statement) {
    Parse parse = null;
    parse = ActionFactory.selectParse(statement);
    ObjectNode jsonObject = (ObjectNode) parse.parse(statement);
    System.out.println();
  }

  @Test
  public void testExecute() throws JSQLParserException {
    String sql = "EXEC cp_petowner @ownername='20,30'";
    Statement statement = CCJSqlParserUtil.parse(sql);
    testSqlParse(statement);
  }

  @Test
  public void testExecute1() throws JSQLParserException {
    String sql = "EXEC my_proc 'abc', 123;";
    Statement statement = CCJSqlParserUtil.parse(sql);
    testSqlParse(statement);
  }

}
