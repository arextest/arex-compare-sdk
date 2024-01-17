package com.arextest.diff.utils;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.DecompressConfig;
import com.arextest.diff.model.GlobalOptions;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.SystemConfig;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsToRulesConvert {

  public static RulesConfig optionsToConfig(String baseMsg, String testMsg,
      CompareOptions compareOptions, GlobalOptions globalOptions) {
    RulesConfig rulesConfig = new RulesConfig();
    rulesConfig.setBaseMsg(baseMsg);
    rulesConfig.setTestMsg(testMsg);

    systemToRules(rulesConfig);
    globalOptionsToRules(globalOptions, rulesConfig);
    optionsToRules(compareOptions, rulesConfig);
    if (rulesConfig.isNameToLower()) {
      configToLower(rulesConfig);
    }
    return rulesConfig;
  }

  private static void configToLower(RulesConfig rulesConfig) {
    rulesConfig.setInclusions(FieldToLowerUtil.listListToLower(rulesConfig.getInclusions()));
    rulesConfig.setExclusions(
        FieldToLowerUtil.expressionNodeListToLower(rulesConfig.getExclusions()));
    rulesConfig.setExpressionExclusions(
        FieldToLowerUtil.expressionNodeListToLower(rulesConfig.getExpressionExclusions()));
    rulesConfig.setIgnoreNodeSet(FieldToLowerUtil.setToLower(rulesConfig.getIgnoreNodeSet()));
    rulesConfig.setDecompressConfigMap(
        FieldToLowerUtil.mapKeyToLower(rulesConfig.getDecompressConfigMap()));
    FieldToLowerUtil.referenceToLower(rulesConfig.getReferenceEntities());
    FieldToLowerUtil.keyConfigToLower(rulesConfig.getListSortEntities());
  }


  private static void systemToRules(RulesConfig rulesConfig) {
    rulesConfig.setIgnoreNodeSet(SystemConfig.getIgnoreNodeSet());
  }

  private static void globalOptionsToRules(GlobalOptions globalOptions, RulesConfig rulesConfig) {
    if (globalOptions == null) {
      return;
    }
    rulesConfig.setNameToLower(globalOptions.isNameToLower());
    rulesConfig.setNullEqualsEmpty(globalOptions.isNullEqualsEmpty());
    rulesConfig.setIgnoredTimePrecision(globalOptions.getIgnoredTimePrecision());
    rulesConfig.setNullEqualsNotExist(globalOptions.isNullEqualsNotExist());
    if (globalOptions.getIgnoreNodeSet() != null) {
      rulesConfig.setIgnoreNodeSet(globalOptions.getIgnoreNodeSet());
    }
    if (globalOptions.getOnlyCompareCoincidentColumn() != null) {
      rulesConfig.setOnlyCompareCoincidentColumn(globalOptions.getOnlyCompareCoincidentColumn());
    }
    if (globalOptions.getSelectIgnoreCompare() != null) {
      rulesConfig.setSelectIgnoreCompare(globalOptions.getSelectIgnoreCompare());
    }
    if (globalOptions.getUuidIgnore() != null) {
      rulesConfig.setUuidIgnore(globalOptions.getUuidIgnore());
    }
  }

  private static void optionsToRules(CompareOptions compareOptions, RulesConfig rulesConfig) {
    if (compareOptions == null) {
      return;
    }
    rulesConfig.setCategoryType(compareOptions.getCategoryType());
    rulesConfig.setPluginJarUrl(compareOptions.getPluginJarUrl());
    rulesConfig.setDecompressConfigMap(
        decompressConfigConvert(compareOptions.getDecompressConfigList()));
    rulesConfig.setInclusions(compareOptions.getInclusions() == null ? null
        : new ArrayList<>(compareOptions.getInclusions()));
    rulesConfig.setExpressionExclusions(
        ExpressionNodeParser.doParse(compareOptions.getExclusions()));
    rulesConfig.setExclusions(
        ExpressionNodeParser.doConvertNameNode(compareOptions.getExclusions()));
    rulesConfig.setReferenceEntities(referenceConfigConvert(compareOptions.getReferenceConfig()));
    rulesConfig.setListSortEntities(listSortConfigConvert(compareOptions.getListSortConfig(),
        rulesConfig.getReferenceEntities()));
    if (compareOptions.getSelectIgnoreCompare() != null) {
      rulesConfig.setSelectIgnoreCompare(compareOptions.getSelectIgnoreCompare());
    }
    if (compareOptions.getOnlyCompareCoincidentColumn() != null) {
      rulesConfig.setOnlyCompareCoincidentColumn(compareOptions.getOnlyCompareCoincidentColumn());
    }

    // if CompareOptions exist nameToLower or nullEqualsEmpty, override GlobalOptions
    if (compareOptions.getNameToLower() != null) {
      rulesConfig.setNameToLower(compareOptions.getNameToLower());
    }
    if (compareOptions.getNullEqualsEmpty() != null) {
      rulesConfig.setNullEqualsEmpty(compareOptions.getNullEqualsEmpty());
    }
    if (compareOptions.getIgnoredTimePrecision() != null) {
      rulesConfig.setIgnoredTimePrecision(compareOptions.getIgnoredTimePrecision());
    }
    if (compareOptions.getNullEqualsNotExist() != null) {
      rulesConfig.setNullEqualsNotExist(compareOptions.getNullEqualsNotExist());
    }
    if (compareOptions.getUuidIgnore() != null) {
      rulesConfig.setUuidIgnore(compareOptions.getUuidIgnore());
    }
  }

  private static Map<List<String>, DecompressConfig> decompressConfigConvert(
      List<DecompressConfig> decompressConfigList) {
    if (decompressConfigList == null || decompressConfigList.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<List<String>, DecompressConfig> result = new HashMap<>();
    for (DecompressConfig decompressConfig : decompressConfigList) {
      List<List<String>> nodePathList = decompressConfig.getNodePath();
      if (nodePathList == null) {
        continue;
      }
      for (List<String> nodePath : nodePathList) {
        if (nodePath == null || nodePath.isEmpty()) {
          continue;
        }
        result.put(nodePath, decompressConfig);
      }
    }
    return result;
  }

  private static List<ReferenceEntity> referenceConfigConvert(
      Map<List<String>, List<String>> referenceConfig) {
    if (referenceConfig == null) {
      return Collections.emptyList();
    }
    List<ReferenceEntity> referenceEntities = new ArrayList<>();
    referenceConfig.forEach((k, v) -> {
      if (k != null && !k.isEmpty() && v != null && !v.isEmpty()) {
        ReferenceEntity entity = new ReferenceEntity();
        entity.setFkNodePath(k);
        entity.setPkNodePath(v);
        // this maybe cause some problem
        entity.setPkNodeListPath(v.subList(0, v.size() - 1));
        referenceEntities.add(entity);
      }
    });
    return referenceEntities;
  }

  private static List<ListSortEntity> listSortConfigConvert(
      Map<List<String>, List<List<String>>> listKeyConfig, List<ReferenceEntity> references) {
    if (listKeyConfig == null) {
      return Collections.emptyList();
    }

    Map<List<String>, ReferenceEntity> pkNodePath2ReferenceMap = new HashMap<>();
    if (references != null) {
      for (ReferenceEntity referenceEntity : references) {
        pkNodePath2ReferenceMap.put(referenceEntity.getPkNodeListPath(), referenceEntity);
      }
    }

    List<ListSortEntity> listKeyEntities = new ArrayList<>();
    listKeyConfig.forEach((k, v) -> {
      if (k != null && !k.isEmpty() && v != null && !v.isEmpty()) {
        ListSortEntity listSortEntity = new ListSortEntity();
        listSortEntity.setListNodepath(k);
        listSortEntity.setKeys(v);
        ReferenceEntity entity = pkNodePath2ReferenceMap.get(listSortEntity.getListNodepath());
        if (entity != null) {
          List<String> pkNodeRelativePath = entity.getPkNodePath()
              .subList(entity.getPkNodeListPath().size(), entity.getPkNodePath().size());
          listSortEntity.setReferenceNodeRelativePath(pkNodeRelativePath);
        }
        listKeyEntities.add(listSortEntity);
      }
    });
    return listKeyEntities;
  }


}
