package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.utils.StringUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareOptions {

    /**
     * @see CategoryType
     */
    private String categoryType;

    /**
     * The url address of the plug-in jar which is specified by the interface http or absolute path.
     * The decompressService which is loaded from this pluginJarUrl is the level of each compare.
     */
    private String pluginJarUrl;

    /**
     * the collection of the node path chosen to compare
     */
    private Set<List<String>> inclusions;

    /**
     * the collection of the node path which is ignore
     */
    private Set<List<String>> exclusions;

    /**
     * the config to decompress
     * key：The bean name of the decompression method which is implement the DecompressService interface, you can use an alias
     * value：the collection of the node path need to decompress
     */
    private List<DeCompressConfig> deCompressConfigList;

    /**
     * reference config
     * key：the node path which is foreign key path
     * For the list node which is alike [a,b,c],[1,2,3]。a is described as "%value%"
     * value: the node path which is primary key.
     */
    private Map<List<String>, List<String>> referenceConfig;

    /**
     * ordering rules for list nodes
     * key：the list node path
     * value：The primary key formed by the child nodes under the list
     */
    private Map<List<String>, List<List<String>>> listSortConfig;

    /**
     * change the message and configuration to lowercase, for the inconsistency between the actual message and the contract case
     */
    private Boolean nameToLower = null;

    /**
     * This option is true, The null, CollectionUtils.isEmpty and Strings.empty are equal
     * for example：the baseMsg: {"age":""} is consistent with testMsg: "{\"age\":null}"
     */
    private Boolean nullEqualsEmpty = null;
    /**
     * This option is tru, the select statement does not compare
     */
    private Boolean selectIgnoreCompare = null;

    /**
     * only compare the overlapping columns
     * ignore the non-overlapping columns
     * This configuration works only when compareType is CompareType.DATABASE
     */
    private Boolean onlyCompareCoincidentColumn = null;

    /**
     * This refers to ignoring the precision of specified time fields when comparing them,
     * which means that if the difference between two fields is less than or equal to a certain parameter,
     * they are considered to be no error.
     * Unit of time: mm
     */
    private Long ignoredTimePrecision;

    /**
     * This option is true, The "null" and the situation that the field is not existed are equal
     * for example：the baseMsg: {"age":null} is consistent with testMsg: "{}"
     */
    private Boolean nullEqualsNotExist;

    public CompareOptions() {
    }

    public static CompareOptions options() {
        return new CompareOptions();
    }

    public CompareOptions putCategoryType(String categoryType) {
        this.categoryType = categoryType;
        return this;
    }

    public CompareOptions putPluginJarUrl(String pluginJarUrl) {
        this.pluginJarUrl = pluginJarUrl;
        return this;
    }

    public CompareOptions putInclusions(List<String> path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        if (this.inclusions == null) {
            this.inclusions = new HashSet<>();
        }
        this.inclusions.add(path);
        return this;
    }

    public CompareOptions putInclusions(Collection<List<String>> paths) {
        if (paths == null || paths.isEmpty()) {
            return this;
        }
        if (this.inclusions == null) {
            this.inclusions = new HashSet<>();
        }
        this.inclusions.addAll(paths);
        return this;
    }

    public CompareOptions putExclusions(List<String> path) {
        if (path == null || path.isEmpty()) {
            return this;
        }
        if (this.exclusions == null) {
            this.exclusions = new HashSet<>();
        }
        this.exclusions.add(path);
        return this;
    }

    public CompareOptions putExclusions(Collection<List<String>> paths) {
        if (paths == null || paths.isEmpty()) {
            return this;
        }
        if (this.exclusions == null) {
            this.exclusions = new HashSet<>();
        }
        this.exclusions.addAll(paths);
        return this;
    }

    public CompareOptions putDecompressConfig(DeCompressConfig deCompressConfig) {
        if (deCompressConfig == null || StringUtil.isEmpty(deCompressConfig.getName())) {
            return this;
        }
        if (this.deCompressConfigList == null) {
            this.deCompressConfigList = new ArrayList<>();
        }
        this.deCompressConfigList.add(deCompressConfig);
        return this;
    }

    public CompareOptions putDecompressConfig(Collection<DeCompressConfig> deCompressConfigList) {
        if (deCompressConfigList == null || deCompressConfigList.isEmpty()) {
            return this;
        }
        if (this.deCompressConfigList == null) {
            this.deCompressConfigList = new ArrayList<>();
        }
        for (DeCompressConfig deCompressConfig : deCompressConfigList) {
            this.putDecompressConfig(deCompressConfig);
        }
        return this;
    }

    public CompareOptions putReferenceConfig(List<String> fkNodePath, List<String> pkNodePath) {
        if (this.referenceConfig == null) {
            this.referenceConfig = new HashMap<>();
        }
        this.referenceConfig.put(fkNodePath, pkNodePath);
        return this;
    }

    public CompareOptions putReferenceConfig(Map<List<String>, List<String>> referenceConfig) {
        if (referenceConfig == null || referenceConfig.isEmpty()) {
            return this;
        }
        if (this.referenceConfig == null) {
            this.referenceConfig = new HashMap<>();
        }
        this.referenceConfig.putAll(referenceConfig);
        return this;
    }

    public CompareOptions putListSortConfig(List<String> listNodePath, List<List<String>> key) {
        if (this.listSortConfig == null) {
            this.listSortConfig = new HashMap<>();
        }
        this.listSortConfig.put(listNodePath, key);
        return this;
    }

    public CompareOptions putListSortConfig(Map<List<String>, List<List<String>>> listKeyConfig) {
        if (listKeyConfig == null || listKeyConfig.isEmpty()) {
            return this;
        }
        if (this.listSortConfig == null) {
            this.listSortConfig = new HashMap<>();
        }
        this.listSortConfig.putAll(listKeyConfig);
        return this;
    }

    public CompareOptions putNameToLower(Boolean nameToLower) {
        this.nameToLower = nameToLower;
        return this;
    }

    public CompareOptions putNullEqualsEmpty(Boolean nullEqualsEmpty) {
        this.nullEqualsEmpty = nullEqualsEmpty;
        return this;
    }

    public CompareOptions putSelectIgnoreCompare(Boolean selectIgnoreCompare) {
        this.selectIgnoreCompare = selectIgnoreCompare;
        return this;
    }

    public CompareOptions putOnlyCompareCoincidentColumn(Boolean onlyCompareCoincidentColumn) {
        this.onlyCompareCoincidentColumn = onlyCompareCoincidentColumn;
        return this;
    }

    public CompareOptions putIgnoredTimePrecision(Long ignoredTimePrecision) {
        this.ignoredTimePrecision = ignoredTimePrecision;
        return this;
    }

    public CompareOptions putNullEqualsNotExist(Boolean nullEqualsNotExist) {
        this.nullEqualsNotExist = nullEqualsNotExist;
        return this;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String getPluginJarUrl() {
        return pluginJarUrl;
    }

    public Set<List<String>> getInclusions() {
        return inclusions;
    }

    public Set<List<String>> getExclusions() {
        return exclusions;
    }

    public List<DeCompressConfig> getDeCompressConfigList() {
        return deCompressConfigList;
    }

    public Map<List<String>, List<String>> getReferenceConfig() {
        return referenceConfig;
    }

    public Map<List<String>, List<List<String>>> getListSortConfig() {
        return listSortConfig;
    }

    public Boolean getNameToLower() {
        return nameToLower;
    }

    public Boolean getNullEqualsEmpty() {
        return nullEqualsEmpty;
    }

    public Boolean getSelectIgnoreCompare() {
        return selectIgnoreCompare;
    }

    public Boolean getOnlyCompareCoincidentColumn() {
        return onlyCompareCoincidentColumn;
    }

    public Long getIgnoredTimePrecision() {
        return ignoredTimePrecision;
    }

    public Boolean getNullEqualsNotExist() {
        return nullEqualsNotExist;
    }
}
