package com.arextest.diff.utils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ArexStringEqualsUtil {

  private static final Set<String> PREFIX_SET = new HashSet<String>() {
    {
      add("arex.");
      add("arex_");
      add("_arex");
      add("AREX.");
      add("AREX_");
      add("_AREX");
    }
  };

  /**
   * The agent will add a prefix to the email or URL.
   * compare two string if the string is not equal, then compare without prefix
   * @param baseStr
   * @param testStr
   * @return
   */
  public static boolean stringEquals(String baseStr, String testStr) {
    boolean result = Objects.equals(baseStr, testStr);
    if (!result) {
      result = isEqualsWithoutPrefix(baseStr, testStr);
    }
    return result;
  }

  public static boolean isEqualsWithoutPrefix(String baseStr, String testStr) {

    if (baseStr == null && testStr == null) {
      return true;
    }

    if (baseStr == null || testStr == null) {
      return false;
    }

    int baseValueLen = baseStr.length();
    int testValueLen = testStr.length();
    if (testValueLen - baseValueLen <= 0 || (testValueLen - baseValueLen) % 5 != 0) {
      return false;
    }

    int baseIndex = 0;
    int testIndex = 0;

    if (baseValueLen == 0) {
      while (testIndex < testValueLen) {
        if (isPrefix(testIndex, testStr)) {
          testIndex = testIndex + 5;
        } else {
          return false;
        }
      }
      return true;
    } else {
      while (baseIndex < baseValueLen && testIndex < testValueLen) {
        if (isPrefix(testIndex, testStr)) {
          testIndex = testIndex + 5;
          continue;
        }
        if (baseStr.charAt(baseIndex) != testStr.charAt(testIndex)) {
          return false;
        }
        baseIndex++;
        testIndex++;
      }
      return true;
    }
  }

  private static boolean isPrefix(int testIndex, String testValue) {
    if (testIndex > testValue.length() - 5) {
      return false;
    }

    if (testValue.charAt(testIndex) != 'a' && testValue.charAt(testIndex) != '_') {
      return false;
    }

    String substring = testValue.substring(testIndex, testIndex + 5);
    return PREFIX_SET.contains(substring);
  }

}
