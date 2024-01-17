package com.arextest.diff.handler.parse.sqlparse;

import java.util.HashMap;
import java.util.Map;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Created by rchen9 on 2023/1/11.
 */
@Disabled
public class ExampleTest {

  @Test
  public void testCreate() throws JSQLParserException {
    String sql =
        "create or replace view itemtot as select persion,sum(amount) itemtot from ledger where actiondate "
            +
            "between to_date('01-MAR-1901','dd-mon-yyyy') " +
            "and to_date('31-MAR-1901','dd-mon-yyyy') and action in('bought','raid')" +
            " group by persion;";
    Statement parse = CCJSqlParserUtil.parse(sql);
    System.out.println();
  }

  @Test
  public void testSql() throws JSQLParserException {
    String sql = "select * from students";
    sqlParse(sql);
  }

  private void sqlParse(String sql) throws JSQLParserException {
    Select stmt = (Select) CCJSqlParserUtil.parse(sql);

    Map<String, Expression> map = new HashMap<>();
    for (SelectItem selectItem : ((PlainSelect) stmt.getSelectBody()).getSelectItems()) {
      selectItem.accept(new SelectItemVisitorAdapter() {
        @Override
        public void visit(SelectExpressionItem item) {
          // map.put(item.getAlias().getName(), item.getExpression());
          System.out.println();
        }
      });
    }

    // List<OrderByElement> orderByElements = ((PlainSelect) stmt.getSelectBody()).getOrderByElements();
    // orderByElements.forEach(o -> {
    //     o.accept(new OrderByVisitorAdapter() {
    //         @Override
    //         public void visit(OrderByElement orderBy) {
    //             super.visit(orderBy);
    //         }
    //     });
    // });
    FromItem fromItem = ((PlainSelect) stmt.getSelectBody()).getFromItem();
    fromItem.accept(new FromItemVisitorAdapter() {
      @Override
      public void visit(Table table) {
        super.visit(table);
      }

      @Override
      public void visit(SubSelect subSelect) {
        System.out.println();
      }
    });

    Expression whereExpr = ((PlainSelect) stmt.getSelectBody()).getWhere();
    if (whereExpr != null) {
      whereExpr.accept(new ExpressionVisitorAdapter() {
        @Override
        protected void visitBinaryExpression(BinaryExpression expr) {
          if (expr instanceof ComparisonOperator) {
            System.out.println("left:"
                + expr.getLeftExpression()
                + " op:"
                + expr.getStringExpression()
                + " right:"
                + expr.getRightExpression());
          }
          super.visitBinaryExpression(expr);
        }
      });
    }

    System.out.println("map " + map);
  }
}
