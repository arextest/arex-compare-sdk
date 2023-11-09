package com.arextest.diff.handler.log.register;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.model.compare.CompareContext;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.Objects;

/**
 * Created by rchen9 on 2023/4/6.
 */
public class LogRegisterCondition {

  public static boolean rejectRegister(Object obj1, Object obj2, LogMarker logMarker,
      CompareContext compareContext) {
    return accordingNullEqualsEmpty(obj1, obj2, logMarker, compareContext)
        || accordingNullEqualsNotExist(obj1, obj2, logMarker, compareContext);
  }

  public static boolean accordingNullEqualsEmpty(Object obj1, Object obj2, LogMarker logMarker,
      CompareContext compareContext) {

    if (!compareContext.notDistinguishNullAndEmpty) {
      return false;
    }

    switch (logMarker) {
      case NULL_CHECK:
      case RIGHT_OBJECT_MISSING:
      case LEFT_OBJECT_MISSING:
      case RIGHT_ARRAY_MISSING:
      case LEFT_ARRAY_MISSING:
        return jsonEmptyJudge(obj1) && jsonEmptyJudge(obj2);
      default:
        return false;
    }
  }

  public static boolean accordingNullEqualsNotExist(Object obj1, Object obj2, LogMarker logMarker,
      CompareContext compareContext) {

    if (!compareContext.nullEqualsNotExist) {
      return false;
    }

    switch (logMarker) {
      case RIGHT_OBJECT_MISSING:
      case LEFT_OBJECT_MISSING:
        return judgeNullAndNotExist(obj1) && judgeNullAndNotExist(obj2);
      default:
        return false;
    }
  }

  private static boolean jsonEmptyJudge(Object o) {
    if (o == null) {
      return false;
    }
    if (o instanceof NullNode) {
      return true;
    }
    if (o instanceof ArrayNode) {
      return ((ArrayNode) o).size() == 0;
    } else if (o instanceof ObjectNode) {
      return false;
    } else if (o instanceof TextNode) {
      return Objects.equals(((TextNode) o).asText(), "");
    } else {
      String s = String.valueOf(o);
      return Objects.equals(s, "");
    }
  }

  private static boolean judgeNullAndNotExist(Object o) {
    if (o == null || o instanceof NullNode) {
      return true;
    }
    return false;
  }

}
