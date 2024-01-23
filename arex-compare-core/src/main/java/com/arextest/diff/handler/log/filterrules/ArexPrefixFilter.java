package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.utils.ArexStringEqualsUtil;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/4/13.
 */
public class ArexPrefixFilter implements Predicate<LogEntity> {

  @Override
  public boolean test(LogEntity logEntity) {
    int unmatchedType = logEntity.getPathPair().getUnmatchedType();
    if (unmatchedType == UnmatchedType.UNMATCHED) {
      Object baseValue = logEntity.getBaseValue();
      Object testValue = logEntity.getTestValue();
      if (baseValue != null && testValue != null) {
        return !ArexStringEqualsUtil.isEqualsWithoutPrefix((String) baseValue, (String) testValue);
      }
    }
    return true;
  }
}
