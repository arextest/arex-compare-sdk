package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RulesConfig {

    /**
     * @see CategoryType
     */
    private String categoryType;

    private String baseMsg;

    private String testMsg;

    /**
     * to receive the field "pluginJarUrl" in CompareOptions
     */
    private String pluginJarUrl;

    private Map<List<String>, DecompressConfig> decompressConfigMap;

    private List<List<String>> inclusions;

    private List<List<String>> exclusions;

    private Set<String> ignoreNodeSet;

    private List<ReferenceEntity> referenceEntities = Collections.emptyList();

    private List<ListSortEntity> listSortEntities = Collections.emptyList();

    private boolean nameToLower;

    private boolean nullEqualsEmpty;

    private boolean selectIgnoreCompare;

    private boolean onlyCompareCoincidentColumn;

    private long ignoredTimePrecision;

    private boolean nullEqualsNotExist;

    public RulesConfig() {
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getBaseMsg() {
        return baseMsg;
    }

    public void setBaseMsg(String baseMsg) {
        this.baseMsg = baseMsg;
    }

    public String getTestMsg() {
        return testMsg;
    }

    public void setTestMsg(String testMsg) {
        this.testMsg = testMsg;
    }

    public String getPluginJarUrl() {
        return pluginJarUrl;
    }

    public void setPluginJarUrl(String pluginJarUrl) {
        this.pluginJarUrl = pluginJarUrl;
    }

    public List<List<String>> getInclusions() {
        return inclusions;
    }

    public void setInclusions(List<List<String>> inclusions) {
        this.inclusions = inclusions;
    }

    public List<List<String>> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<List<String>> exclusions) {
        this.exclusions = exclusions;
    }

    public Set<String> getIgnoreNodeSet() {
        return ignoreNodeSet;
    }

    public void setIgnoreNodeSet(Set<String> ignoreNodeSet) {
        this.ignoreNodeSet = ignoreNodeSet;
    }


    public Map<List<String>, DecompressConfig> getDecompressConfigMap() {
        return decompressConfigMap;
    }

    public void setDecompressConfigMap(Map<List<String>, DecompressConfig> decompressConfigMap) {
        this.decompressConfigMap = decompressConfigMap;
    }

    public List<ReferenceEntity> getReferenceEntities() {
        return referenceEntities;
    }

    public void setReferenceEntities(List<ReferenceEntity> referenceEntities) {
        this.referenceEntities = referenceEntities;
    }

    public List<ListSortEntity> getListSortEntities() {
        return listSortEntities;
    }

    public void setListSortEntities(List<ListSortEntity> listSortEntities) {
        this.listSortEntities = listSortEntities;
    }

    public boolean isNameToLower() {
        return nameToLower;
    }

    public void setNameToLower(boolean nameToLower) {
        this.nameToLower = nameToLower;
    }

    public boolean isNullEqualsEmpty() {
        return nullEqualsEmpty;
    }

    public void setNullEqualsEmpty(boolean nullEqualsEmpty) {
        this.nullEqualsEmpty = nullEqualsEmpty;
    }

    public boolean isSelectIgnoreCompare() {
        return selectIgnoreCompare;
    }

    public void setSelectIgnoreCompare(boolean selectIgnoreCompare) {
        this.selectIgnoreCompare = selectIgnoreCompare;
    }

    public boolean isOnlyCompareCoincidentColumn() {
        return onlyCompareCoincidentColumn;
    }

    public void setOnlyCompareCoincidentColumn(boolean onlyCompareCoincidentColumn) {
        this.onlyCompareCoincidentColumn = onlyCompareCoincidentColumn;
    }

    public long getIgnoredTimePrecision() {
        return ignoredTimePrecision;
    }

    public void setIgnoredTimePrecision(long ignoredTimePrecision) {
        this.ignoredTimePrecision = ignoredTimePrecision;
    }

    public boolean isNullEqualsNotExist() {
        return nullEqualsNotExist;
    }

    public void setNullEqualsNotExist(boolean nullEqualsNotExist) {
        this.nullEqualsNotExist = nullEqualsNotExist;
    }
}
