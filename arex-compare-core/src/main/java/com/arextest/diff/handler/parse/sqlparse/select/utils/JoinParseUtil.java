package com.arextest.diff.handler.parse.sqlparse.select.utils;

import com.arextest.diff.handler.parse.sqlparse.constants.DbParseConstants;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Collection;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;

/**
 * Created by rchen9 on 2023/1/10.
 */
public class JoinParseUtil {

  public static ObjectNode parse(Join parseObj) {

    ObjectNode res = JacksonHelperUtil.getObjectNode();
    // join type parse
    res.put(DbParseConstants.TYPE, getJOINType(parseObj));

    // rightItem parse
    FromItem rightItem = parseObj.getRightItem();
    if (rightItem != null) {
      res.put(DbParseConstants.TABLE, rightItem.toString());
    }

    // onExpressions parse
    Collection<Expression> onExpressions = parseObj.getOnExpressions();
    if (onExpressions != null && !onExpressions.isEmpty()) {
      ObjectNode onObj = JacksonHelperUtil.getObjectNode();
      onExpressions.forEach(item -> {
        onObj.put(item.toString(), DbParseConstants.EMPTY);
      });
      res.put(DbParseConstants.ON, onObj);
    }

    // usingColumns parse
    List<Column> usingColumns = parseObj.getUsingColumns();
    if (usingColumns != null && !usingColumns.isEmpty()) {
      ObjectNode usingObj = JacksonHelperUtil.getObjectNode();
      usingColumns.forEach(item -> {
        usingObj.put(item.toString(), DbParseConstants.EMPTY);
      });
      res.put(DbParseConstants.USING, usingObj);
    }

    return res;
  }

  private static String getJOINType(Join parseObj) {
    StringBuilder builder = new StringBuilder();
    if (parseObj.isSimple() && parseObj.isOuter()) {
      builder.append("OUTER JOIN");
    } else if (parseObj.isSimple()) {
      builder.append("");
    } else {
      if (parseObj.isNatural()) {
        builder.append("NATURAL ");
      }

      if (parseObj.isRight()) {
        builder.append("RIGHT ");
      } else if (parseObj.isFull()) {
        builder.append("FULL ");
      } else if (parseObj.isLeft()) {
        builder.append("LEFT ");
      } else if (parseObj.isCross()) {
        builder.append("CROSS ");
      }

      if (parseObj.isOuter()) {
        builder.append("OUTER ");
      } else if (parseObj.isInner()) {
        builder.append("INNER ");
      } else if (parseObj.isSemi()) {
        builder.append("SEMI ");
      }

      if (parseObj.isStraight()) {
        builder.append("STRAIGHT_JOIN ");
      } else if (parseObj.isApply()) {
        builder.append("APPLY ");
      } else {
        builder.append("JOIN");
      }
    }
    return builder.toString();
  }
}
