package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.handler.parse.sqlparse.constants.DbParseConstants;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/2/1.
 */
public class OnlyCompareSameColumnsFilter implements Predicate<LogEntity> {

  private static List<List<String>> IgnoreNodePathList = Arrays.asList(
      Collections.singletonList(DbParseConstants.PARAMETERS),
      Arrays.asList(DbParseConstants.PARSED_SQL, DbParseConstants.COLUMNS)
  );


  @Override
  public boolean test(LogEntity logEntity) {

    int unmatchedType = logEntity.getPathPair().getUnmatchedType();
    if (unmatchedType == UnmatchedType.LEFT_MISSING
        || unmatchedType == UnmatchedType.RIGHT_MISSING) {
      List<NodeEntity> leftUnmatchedPath = logEntity.getPathPair().getLeftUnmatchedPath();
      List<NodeEntity> rightUnmatchedPath = logEntity.getPathPair().getRightUnmatchedPath();
      List<NodeEntity> currentNode = leftUnmatchedPath.size() >= rightUnmatchedPath.size()
          ? leftUnmatchedPath : rightUnmatchedPath;
      List<String> fuzzyPath = ListUti.convertToStringList(currentNode);
      if (isIgnorePath(fuzzyPath)) {
        return false;
      }
    }
    return true;
  }


  private boolean isIgnorePath(List<String> fuzzyPath) {
    for (List<String> ignoreNodePath : IgnoreNodePathList) {

      int fuzzyPathSize = fuzzyPath.size();
      int ignorePathSize = ignoreNodePath.size();

      if (ignorePathSize < fuzzyPathSize) {
        boolean flag = true;
        for (int i = 0; i < ignorePathSize; i++) {
          if (!Objects.equals(ignoreNodePath.get(i), fuzzyPath.get(i))) {
            flag = false;
            break;
          }
        }
        if (flag) {
          return true;
        }
      }

    }
    return false;
  }
}
