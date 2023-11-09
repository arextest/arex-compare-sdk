package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by rchen9 on 2023/4/13.
 */
public class UuidFilter implements Predicate<LogEntity> {

  private static final String UUID_PATTERN = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

  public static boolean isUuid(String baseStr, String testStr) {

    if (baseStr.length() != 36 || testStr.length() != 36) {
      return false;
    }

    return Pattern.matches(UUID_PATTERN, baseStr) && Pattern.matches(UUID_PATTERN, testStr);
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
