package com.arextest.diff.utils;

import com.arextest.diff.model.key.RatioEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JSONArraySort {

  public static float compareSplitSimRate(String base, String compare) {
    if (base == null && compare == null) {
      return 1f;
    }

    if (base == null || compare == null) {
      return 0f;
    }

    if (base.equals(compare)) {
      return 1f;
    }

    Set<String> baseSplit = new HashSet<>(Arrays.asList(base.split(",")));
    Set<String> compareSplit = new HashSet<>(Arrays.asList(compare.split(",")));

    float result = 0f;
    int sameCount = 0;

    for (String str : baseSplit) {
      if (compareSplit.contains(str)) {
        sameCount++;
      }
    }

    if (sameCount > 0) {
      result = (float) sameCount / (float) Math.max(baseSplit.size(), compareSplit.size());
    }
    return result;
  }

  public static void jsonArraySortSplitWhole(List<String> firstArray, List<String> secondArray,
      float simThreshold) {
    if (firstArray == null || secondArray == null) {
      return;
    }

    if (firstArray.size() == 0 || secondArray.size() == 0) {
      return;
    }

    List<RatioEntity> ratioEntities = new ArrayList<>();
    Set<Integer> firstUsedIndexs = new HashSet<>();
    Set<Integer> secondUsedIndexs = new HashSet<>();

    for (int i = 0; i < firstArray.size(); i++) {
      for (int j = 0; j < secondArray.size(); j++) {
        float ratio = compareSplitSimRate(firstArray.get(i), secondArray.get(j));
        ratioEntities.add(new RatioEntity(i, j, ratio));
      }
    }

    Collections.sort(ratioEntities, new Comparator<RatioEntity>() {
      @Override
      public int compare(RatioEntity o1, RatioEntity o2) {
        if (o1 == null && o2 == null) {
          return 0;
        }
        if (o1 == null) {
          return 1;
        }
        if (o2 == null) {
          return -1;
        }
        if (o1.getRatio() > o2.getRatio()) {
          return -1;
        }
        if (o2.getRatio() > o1.getRatio()) {
          return 1;
        }
        return 0;

      }
    });

    List<String> newFirstArray = new ArrayList<>();
    List<String> newSecondArray = new ArrayList<>();
    for (RatioEntity ratioEntity : ratioEntities) {
      int firstIndex = ratioEntity.getFirstIndex();
      int secondIndex = ratioEntity.getSecondIndex();
      if (firstUsedIndexs.contains(firstIndex) || secondUsedIndexs.contains(secondIndex)) {
        continue;
      }

      firstUsedIndexs.add(firstIndex);
      secondUsedIndexs.add(secondIndex);
      newFirstArray.add(firstArray.get(firstIndex));
      newSecondArray.add(secondArray.get(secondIndex));
    }

    if (firstUsedIndexs.size() != firstArray.size()) {
      for (int i = 0; i < firstArray.size(); i++) {
        if (firstUsedIndexs.contains(i)) {
          continue;
        }
        newFirstArray.add(firstArray.get(i));
      }
    }
    if (secondUsedIndexs.size() != secondArray.size()) {
      for (int i = 0; i < secondArray.size(); i++) {
        if (secondUsedIndexs.contains(i)) {
          continue;
        }
        newSecondArray.add(secondArray.get(i));
      }
    }
    firstArray.clear();
    secondArray.clear();
    for (int i = 0; i < newFirstArray.size(); i++) {
      firstArray.add(i, newFirstArray.get(i));
    }
    for (int i = 0; i < newSecondArray.size(); i++) {
      secondArray.add(i, newSecondArray.get(i));
    }
  }

}
