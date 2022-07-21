package com.arextest.diff.utils;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.GlobalOptions;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;

import java.util.*;
import java.util.stream.Collectors;

public class OptionsToRulesAdapter {

    public static RulesConfig optionsToConfig(String baseMsg, String testMsg, CompareOptions compareOptions, GlobalOptions globalOptions) {
        RulesConfig rulesConfig = new RulesConfig();
        rulesConfig.setBaseMsg(baseMsg);
        rulesConfig.setTestMsg(testMsg);

        globalOptionsToRules(globalOptions, rulesConfig);
        optionsToRules(compareOptions, rulesConfig);
        if (rulesConfig.isNameToLower()) {
            configToLower(rulesConfig);
        }
        return rulesConfig;
    }

    private static void configToLower(RulesConfig rulesConfig) {
        rulesConfig.setInclusions(listListToLower(rulesConfig.getInclusions()));
        rulesConfig.setExclusions(listListToLower(rulesConfig.getExclusions()));
        rulesConfig.setDecompressConfig(mapKeyToLower(rulesConfig.getDecompressConfig()));
        referenceToLower(rulesConfig.getReferenceEntities());
        keyConfigToLower(rulesConfig.getListSortEntities());
    }

    private static void globalOptionsToRules(GlobalOptions globalOptions, RulesConfig rulesConfig) {
        if (globalOptions == null) {
            return;
        }
        rulesConfig.setDecompressServices(globalOptions.getDecompressServices());
        rulesConfig.setNameToLower(globalOptions.isNameToLower());
        rulesConfig.setNullEqualsEmpty(globalOptions.isNullEqualsEmpty());
    }

    private static void optionsToRules(CompareOptions compareOptions, RulesConfig rulesConfig) {
        if (compareOptions == null) {
            return;
        }
        rulesConfig.setInclusions(setToListConvert(compareOptions.getInclusions()));
        rulesConfig.setExclusions(setToListConvert(compareOptions.getExclusions()));
        rulesConfig.setDecompressConfig(compareOptions.getDecompressConfig());
        rulesConfig.setReferenceEntities(referenceConfigConvert(compareOptions.getReferenceConfig()));
        rulesConfig.setListSortEntities(listSortConfigConvert(compareOptions.getListSortConfig(), rulesConfig.getReferenceEntities()));
        // if CompareOptions exist nameToLower or nullEqualsEmpty, override GlobalOptions
        if (compareOptions.getNameToLower() != null) {
            rulesConfig.setNameToLower(compareOptions.getNameToLower());
        }
        if (compareOptions.getNullEqualsEmpty() != null) {
            rulesConfig.setNullEqualsEmpty(compareOptions.getNullEqualsEmpty());
        }
    }

    private static Map<String, List<String>> mapKeyToLower(Map<String, List<String>> map) {
        if (map == null) {
            return null;
        }
        Map<String, List<String>> result = new HashMap<>();
        map.forEach((k, v) -> {
            if (k != null && v != null) {
                result.put(k, v.stream().map(String::toLowerCase).collect(Collectors.toList()));
            }
        });
        return result;
    }

    private static List<List<String>> listListToLower(List<List<String>> lists) {
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

    private static Set<String> setToLower(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.stream().filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static List<String> listToLower(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.stream().filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private static void referenceToLower(List<ReferenceEntity> referenceEntities) {
        referenceEntities.forEach(item -> {
            item.setPkNodePath(listToLower(item.getPkNodePath()));
            item.setPkNodeListPath(listToLower(item.getPkNodeListPath()));
            item.setFkNodePath(listToLower(item.getFkNodePath()));
        });
    }

    private static void keyConfigToLower(List<ListSortEntity> keyEntities) {
        keyEntities.forEach(item -> {
            item.setListNodepath(listToLower(item.getListNodepath()));
            item.setKeys(listListToLower(item.getKeys()));
            item.setReferenceNodeRelativePath(listToLower(item.getReferenceNodeRelativePath()));
        });
    }

    private static List<List<String>> setToListConvert(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        List<List<String>> result = new ArrayList<>();
        for (String path : set) {
            if (StringUtil.isEmpty(path)) {
                continue;
            }
            List<String> collect = Arrays.stream(path.split("\\\\")).collect(Collectors.toList());
            result.add(collect);
        }
        return result;
    }

    private static List<List<String>> ignoredNodePathsConvert(List<String> ignoreNodePaths) {
        if (ignoreNodePaths == null) {
            return null;
        }
        List<List<String>> result = new ArrayList<>();
        ignoreNodePaths.stream().filter(Objects::nonNull).forEach(item -> {
            List<String> collect = Arrays.stream(item.split("\\\\")).collect(Collectors.toList());
            if (collect != null && !collect.isEmpty()) {
                result.add(collect);
            }
        });
        return result;
    }

    private static List<ReferenceEntity> referenceConfigConvert(Map<String, String> referenceConfig) {
        if (referenceConfig == null) {
            return Collections.emptyList();
        }
        List<ReferenceEntity> referenceEntities = new ArrayList<>();
        referenceConfig.forEach((k, v) -> {
            if (!StringUtil.isEmpty(k) && !StringUtil.isEmpty(v)) {
                ReferenceEntity entity = new ReferenceEntity();
                entity.setFkNodePath(Arrays.asList(k.split("\\\\")));
                List<String> pkNodePath = Arrays.asList(v.split("\\\\"));
                entity.setPkNodePath(pkNodePath);
                // this maybe cause some problem
                entity.setPkNodeListPath(pkNodePath.subList(0, pkNodePath.size() - 1));
                referenceEntities.add(entity);
            }
        });
        return referenceEntities;
    }

    private static List<ListSortEntity> listSortConfigConvert(Map<String, String> listKeyConfig, List<ReferenceEntity> references) {
        if (listKeyConfig == null) {
            return Collections.emptyList();
        }

        Map<String, ReferenceEntity> pkNodePath2ListPathMap = new HashMap<>();
        if (references != null) {
            for (ReferenceEntity referenceEntity : references) {
                pkNodePath2ListPathMap.put(ListUti.convertToString2(referenceEntity.getPkNodeListPath()), referenceEntity);
            }
        }

        List<ListSortEntity> listKeyEntities = new ArrayList<>();
        listKeyConfig.forEach((k, v) -> {
            if (!StringUtil.isEmpty(v)) {
                ListSortEntity listSortEntity = new ListSortEntity();
                List<List<String>> keyNodePaths = new ArrayList<>();
                for (String keyNodePath : v.split(",")) {
                    keyNodePaths.add(Arrays.asList(keyNodePath.split("\\\\")));
                }
                listSortEntity.setListNodepath(Arrays.asList(k.split("\\\\")));
                listSortEntity.setKeys(keyNodePaths);
                ReferenceEntity entity = pkNodePath2ListPathMap.get(ListUti.convertToString2(listSortEntity.getListNodepath()));
                if (entity != null) {
                    List<String> pkNodeRelativePath = entity.getPkNodePath().subList(entity.getPkNodeListPath().size(), entity.getPkNodePath().size());
                    listSortEntity.setReferenceNodeRelativePath(pkNodeRelativePath);
                }
                listKeyEntities.add(listSortEntity);
            }
        });
        return listKeyEntities;
    }


}
