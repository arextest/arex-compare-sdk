package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.service.DecompressService;

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

    private List<List<String>> inclusions;

    private List<List<String>> exclusions;

    private Set<String> ignoreNodeSet;

    private Map<String, DecompressService> decompressServices;

    private Map<String, List<List<String>>> decompressConfig;

    private List<ReferenceEntity> referenceEntities = Collections.emptyList();

    private List<ListSortEntity> listSortEntities = Collections.emptyList();

    private boolean nameToLower;

    private boolean nullEqualsEmpty;

    private boolean sqlBodyParse;

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

    public Map<String, DecompressService> getDecompressServices() {
        return decompressServices;
    }

    public void setDecompressServices(Map<String, DecompressService> decompressServices) {
        this.decompressServices = decompressServices;
    }

    public Map<String, List<List<String>>> getDecompressConfig() {
        return decompressConfig;
    }

    public void setDecompressConfig(Map<String, List<List<String>>> decompressConfig) {
        this.decompressConfig = decompressConfig;
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

    public boolean isSqlBodyParse() {
        return sqlBodyParse;
    }

    public void setSqlBodyParse(boolean sqlBodyParse) {
        this.sqlBodyParse = sqlBodyParse;
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
