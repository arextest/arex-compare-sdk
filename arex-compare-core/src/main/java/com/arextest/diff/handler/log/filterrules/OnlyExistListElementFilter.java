package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.ErrorType;
import com.arextest.diff.model.log.LogEntity;
import java.util.Objects;
import java.util.function.Predicate;

public class OnlyExistListElementFilter implements Predicate<LogEntity> {

  @Override
  public boolean test(LogEntity logEntity) {
    return !Objects.equals(logEntity.getLogTag().getErrorType(), ErrorType.LIST_LEFT_MISSING)
        && !Objects.equals(logEntity.getLogTag().getErrorType(), ErrorType.LIST_RIGHT_MISSING);
  }
}
