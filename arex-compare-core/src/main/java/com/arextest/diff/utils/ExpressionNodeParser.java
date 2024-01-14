package com.arextest.diff.utils;

import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeType;
import com.arextest.diff.model.pathparse.expression.EqualsExpression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.MutablePair;

public class ExpressionNodeParser {

  private static final String EQUAL_SYMBOL = "=";
  private static final String CHILD_PATH_SPLIT_SYMBOL = "/";

  public static List<List<ExpressionNodeEntity>> doParse(Set<List<String>> exclusions) {
    if (exclusions == null || exclusions.isEmpty()) {
      return null;
    }

    List<List<ExpressionNodeEntity>> result = new ArrayList<>();
    for (List<String> exclusion : exclusions) {
      MutablePair<List<ExpressionNodeEntity>, Boolean> listBooleanMutablePair = registerExpressionNodeEntity(
          exclusion);
      if (listBooleanMutablePair == null || Boolean.FALSE.equals(
          listBooleanMutablePair.getRight())) {
        continue;
      }
      result.add(listBooleanMutablePair.getLeft());
    }
    return result;
  }

  public static List<List<ExpressionNodeEntity>> doConvertNameNode(Set<List<String>> exclusions) {
    if (exclusions == null || exclusions.isEmpty()) {
      return null;
    }

    List<List<ExpressionNodeEntity>> result = new ArrayList<>();
    for (List<String> exclusion : exclusions) {
      MutablePair<List<ExpressionNodeEntity>, Boolean> listBooleanMutablePair = registerExpressionNodeEntity(
          exclusion);
      if (listBooleanMutablePair == null || Boolean.TRUE.equals(
          listBooleanMutablePair.getRight())) {
        continue;
      }
      result.add(listBooleanMutablePair.getLeft());
    }
    return result;
  }


  private static MutablePair<List<ExpressionNodeEntity>, Boolean> registerExpressionNodeEntity(
      List<String> exclusion) {

    if (exclusion == null || exclusion.isEmpty()) {
      return null;
    }

    Boolean isExpression = false;
    List<ExpressionNodeEntity> result = new ArrayList<>();
    for (String item : exclusion) {
      // judge if it is a expression
      if (item.startsWith(Constant.EXPRESSION_PATH_IDENTIFIER_START) && item.endsWith(
          Constant.EXPRESSION_PATH_IDENTIFIER_END)) {
        isExpression = true;
        String substring = item.substring(1, item.length() - 1);
        String[] split = substring.split(EQUAL_SYMBOL, 2);
        if (split.length != 2) {
          result.add(new ExpressionNodeEntity(split[0], ExpressionNodeType.INDEX_NODE));
        } else {
          String path = split[0];
          List<String> stringPath = new ArrayList<>(
              Arrays.asList(path.split(CHILD_PATH_SPLIT_SYMBOL)));

          EqualsExpression equalsExpression = new EqualsExpression(stringPath, split[1]);
          result.add(
              new ExpressionNodeEntity(equalsExpression, ExpressionNodeType.EXPRESSION_NODE));
        }
      } else {
        result.add(new ExpressionNodeEntity(item, ExpressionNodeType.NAME_NODE));
      }
    }
    return new MutablePair<>(result, isExpression);
  }

//  private static List<ExpressionNodeEntity> convertNameNode(List<String> exclusion) {
//
//    if (exclusion == null || exclusion.isEmpty()) {
//      return null;
//    }
//
//    List<ExpressionNodeEntity> result = new ArrayList<>();
//    for (String item : exclusion) {
//      // judge if it is a expression
//      if (!item.startsWith(Constant.EXPRESSION_PATH_IDENTIFIER_START) || !item.endsWith(
//          Constant.EXPRESSION_PATH_IDENTIFIER_END)) {
//        result.add(new ExpressionNodeEntity(item, ExpressionNodeType.NAME_NODE));
//      }
//    }
//    return result;
//  }

}
