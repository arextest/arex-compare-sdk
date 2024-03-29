package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by rchen9 on 2023/4/13.
 */
public class UuidFilter implements Predicate<LogEntity> {

  private static final String LOWER_CASE_UUID_PATTERN = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";
  private static final String UPPER_CASE_UUID_PATTERN = "[0-9A-F]{8}(-[0-9A-F]{4}){3}-[0-9A-F]{12}";

  public static boolean isUuid(String baseStr, String testStr) {

    if (baseStr.length() != 36 || testStr.length() != 36) {
      return false;
    }

    if (Pattern.matches(LOWER_CASE_UUID_PATTERN, baseStr) && Pattern.matches(LOWER_CASE_UUID_PATTERN, testStr)){
      return true;
    }

    if (Pattern.matches(UPPER_CASE_UUID_PATTERN, baseStr) && Pattern.matches(UPPER_CASE_UUID_PATTERN, testStr)){
      return true;
    }

    return false;
  }

  @Override
  public boolean test(LogEntity logEntity) {
    int unmatchedType = logEntity.getPathPair().getUnmatchedType();
    if (unmatchedType == UnmatchedType.UNMATCHED) {
      Object baseValue = logEntity.getBaseValue();
      Object testValue = logEntity.getTestValue();
      if (baseValue != null && testValue != null) {
        return !isUuid((String) baseValue, (String) testValue);
      }
    }
    return true;
  }
}
