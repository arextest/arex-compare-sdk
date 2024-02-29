package com.arextest.diff.handler.parse.sqlparse.select.utils;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

public class ExpressionExtractor {

  public static String extract(Expression expression) {
    if (expression == null) {
      return "";
    }
    if (expression instanceof StringValue) {
      StringValue stringValue = (StringValue) expression;
      return stringValue.getValue();
    }
    return expression.toString();
  }
}
