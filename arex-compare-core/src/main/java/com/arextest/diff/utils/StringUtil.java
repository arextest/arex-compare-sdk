package com.arextest.diff.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class StringUtil {

  public static boolean isEmpty(String s) {
    return s == null || s.isEmpty();
  }

  public static boolean jsonEmpty(Object o) {
    if (o == null || o instanceof NullNode) {
      return true;
    }
    if (o instanceof ArrayNode) {
      if (((ArrayNode) o).size() == 0) {
        return true;
      }
    }
    String s = String.valueOf(o);
    return s.equals("null") || s.equals("");
  }

  public static String objectToString(Object o) {
    if (o == null) {
      return null;
    }

    if (o instanceof ValueNode){
      return ((ValueNode) o).asText();
    }

    return o.toString();
  }

}
