package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPFilter implements Predicate<LogEntity> {

  public static boolean isIp(String baseStr, String testStr) {

    if (baseStr.length() >= 7 && baseStr.length() <= 15
        && testStr.length() >= 7 && testStr.length() <= 15) {
      return isIPv4(baseStr) && isIPv4(testStr);
    }

    if (baseStr.length() >= 15 && baseStr.length() <= 39
        && testStr.length() >= 15 && testStr.length() <= 39) {
      return isIPv6(baseStr) && isIPv6(testStr);
    }

    return false;
  }

  public static boolean isIPv4(String ipAddress) {
    String ipv4Regex = "^((25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[0-1]?[0-9][0-9]?)$";
    Pattern pattern = Pattern.compile(ipv4Regex);
    Matcher matcher = pattern.matcher(ipAddress);
    return matcher.matches();
  }


  public static boolean isIPv6(String ipAddress) {
    String ipv6Regex = "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
    Pattern pattern = Pattern.compile(ipv6Regex);
    Matcher matcher = pattern.matcher(ipAddress);
    return matcher.matches();
  }


  @Override
  public boolean test(LogEntity logEntity) {
    int unmatchedType = logEntity.getPathPair().getUnmatchedType();
    if (unmatchedType == UnmatchedType.UNMATCHED) {
      Object baseValue = logEntity.getBaseValue();
      Object testValue = logEntity.getTestValue();
      if (baseValue != null && testValue != null) {
        return !isIp((String) baseValue, (String) testValue);
      }
    }
    return true;
  }

  public static void main(String[] args) {
    String ip = "0:0:0:0:0:0:0:1";
    System.out.println(isIPv6(ip));
  }

}
