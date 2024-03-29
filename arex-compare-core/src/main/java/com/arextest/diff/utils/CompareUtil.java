package com.arextest.diff.utils;

import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.enumeration.CategoryType;
import java.util.Objects;

public class CompareUtil {

  private static final NormalCompareUtil normalCompareUtil = new NormalCompareUtil();

  private static final DataBaseCompareUtil dataBaseCompareUtil = new DataBaseCompareUtil();

  public static CompareResult jsonCompare(RulesConfig rulesConfig) {
    if (Objects.equals(rulesConfig.getCategoryType(), CategoryType.DATABASE)) {
      return dataBaseCompareUtil.jsonCompare(rulesConfig);
    } else {
      return normalCompareUtil.jsonCompare(rulesConfig);
    }
  }
}
