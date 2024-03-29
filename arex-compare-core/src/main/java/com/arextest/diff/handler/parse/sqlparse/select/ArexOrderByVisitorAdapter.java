package com.arextest.diff.handler.parse.sqlparse.select;

import com.arextest.diff.handler.parse.sqlparse.constants.DbParseConstants;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.OrderByVisitor;

/**
 * Created by rchen9 on 2023/1/11.
 */
public class ArexOrderByVisitorAdapter implements OrderByVisitor {

  private ObjectNode sqlObject;

  public ArexOrderByVisitorAdapter(ObjectNode object) {
    sqlObject = object;
  }

  @Override
  public void visit(OrderByElement orderBy) {
    sqlObject.put(orderBy.toString(), DbParseConstants.EMPTY);
  }
}
