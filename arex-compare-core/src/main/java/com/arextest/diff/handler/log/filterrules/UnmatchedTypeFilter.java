package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2022/8/3.
 */
public class UnmatchedTypeFilter implements Predicate<LogEntity> {

  private static Set<UnmatchedType> filterCondition = new HashSet() {{
    add(UnmatchedType.LEFT_MISSING);
    add(UnmatchedType.RIGHT_MISSING);
    add(UnmatchedType.UNMATCHED);
  }};

  @Override
  public boolean test(LogEntity logEntity) {
    return filterCondition.contains(logEntity.getPathPair().getUnmatchedType());
  }
}
