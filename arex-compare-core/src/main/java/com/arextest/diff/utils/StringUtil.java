package com.arextest.diff.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

public class StringUtil {

  public static boolean isEmpty(String s) {
    return s == null || s.trim().equals("");
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
}
