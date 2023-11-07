package com.arextest.diff.utils;

import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FieldToLowerUtil {

  public static <V> Map<List<String>, V> mapKeyToLower(Map<List<String>, V> map) {
    if (map == null) {
      return null;
    }
    Map<List<String>, V> result = new HashMap<>();
    map.forEach((k, v) -> {
      if (k != null && v != null) {
        result.put(k.stream().map(String::toLowerCase).collect(Collectors.toList()), v);
      }
    });
    return result;
  }

  public static List<List<String>> listListToLower(List<List<String>> lists) {
    if (lists == null || lists.isEmpty()) {
      return null;
    }
    List<List<String>> result = new ArrayList<>();
    lists.forEach(item -> {
      result.add(item.stream()
          .map(String::toLowerCase)
          .collect(Collectors.toList()));
    });
    return result;
  }

  public static Set<String> setToLower(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.stream().filter(Objects::nonNull)
        .map(String::toLowerCase)
        .collect(Collectors.toSet());
  }

  public static List<String> listToLower(Collection<String> collection) {
    if (collection == null) {
      return null;
    }
    return collection.stream().filter(Objects::nonNull)
        .map(String::toLowerCase)
        .collect(Collectors.toList());
  }

  public static void referenceToLower(List<ReferenceEntity> referenceEntities) {
    referenceEntities.forEach(item -> {
      item.setPkNodePath(listToLower(item.getPkNodePath()));
      item.setPkNodeListPath(listToLower(item.getPkNodeListPath()));
      item.setFkNodePath(listToLower(item.getFkNodePath()));
    });
  }

  public static void keyConfigToLower(List<ListSortEntity> keyEntities) {
    keyEntities.forEach(item -> {
      item.setListNodepath(listToLower(item.getListNodepath()));
      item.setKeys(listListToLower(item.getKeys()));
      item.setReferenceNodeRelativePath(listToLower(item.getReferenceNodeRelativePath()));
    });
  }

}
