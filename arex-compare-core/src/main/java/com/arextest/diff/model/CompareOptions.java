package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.script.ScriptCompareConfig;
import com.arextest.diff.utils.ListUti;
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
   * the config to decompress key：The bean name of the decompression method which is implement the
   * DecompressService interface, you can use an alias value：the collection of the node path need to
   * decompress
   */
  private List<DecompressConfig> decompressConfigList;

  /**
   * the config to transform the message key：The bean name of the transform method which is
   * implement the DecompressService interface, you can use an alias value：the collection of the
   * node path need to transform
   */
  private List<TransformConfig> transformConfigList;

  /**
   * reference config key：the node path which is foreign key path For the list node which is alike
   * [a,b,c],[1,2,3]。a is described as "%value%" value: the node path which is primary key.
   */
  private Map<List<String>, List<String>> referenceConfig;

  /**
   * ordering rules for list nodes key：the list node path value：The primary key formed by the child
   * nodes under the list
   */
  private Map<List<String>, List<List<String>>> listSortConfig;


  private List<ScriptCompareConfig> scriptCompareConfigList;

  /**
   * change the message and configuration to lowercase, for the inconsistency between the actual
   * message and the contract case
   */
  private Boolean nameToLower = null;

  /**
   * This option is true, The null, CollectionUtils.isEmpty and Strings.empty are equal for
   * example：the baseMsg: {"age":""} is consistent with testMsg: "{\"age\":null}"
   */
  private Boolean nullEqualsEmpty = null;
  /**
   * This option is true, the select statement does not compare
   */
  private Boolean selectIgnoreCompare = null;

  /**
   * only compare the overlapping columns ignore the non-overlapping columns This configuration
   * works only when compareType is CompareType.DATABASE
   */
  private Boolean onlyCompareCoincidentColumn = null;

  /**
   * This refers to ignoring the precision of specified time fields when comparing them, which means
   * that if the difference between two fields is less than or equal to a certain parameter, they
   * are considered to be no error. Unit of time: mm
   */
  private Long ignoredTimePrecision;

  /**
   * This option is true, The "null" and the situation that the field is not existed are equal for
   * example：the baseMsg: {"age":null} is consistent with testMsg: "{}"
   */
  private Boolean nullEqualsNotExist;

  /**
   * This option is true, the uuid is ignored when comparing
   */
  private Boolean uuidIgnore;

  /**
   * This option is true, the ipV4 and ipV6 is ignored when comparing
   */
  private Boolean ipIgnore;

  /**
   * This option is true, only compare the list elements which are existed in the baseMsg and
   * testMsg
   */
  private Boolean onlyCompareExistListElements;

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

  public CompareOptions putDecompressConfig(DecompressConfig decompressConfig) {
    if (decompressConfig == null || StringUtil.isEmpty(decompressConfig.getName())) {
      return this;
    }
    if (this.decompressConfigList == null) {
      this.decompressConfigList = new ArrayList<>();
    }
    this.decompressConfigList.add(decompressConfig);
    return this;
  }

  public CompareOptions putDecompressConfig(Collection<DecompressConfig> decompressConfigList) {
    if (decompressConfigList == null || decompressConfigList.isEmpty()) {
      return this;
    }
    if (this.decompressConfigList == null) {
      this.decompressConfigList = new ArrayList<>();
    }
    this.decompressConfigList.addAll(decompressConfigList);
    return this;
  }

  public CompareOptions putTransformConfig(TransformConfig transFormConfig) {
    if (transFormConfig == null || ListUti.isEmpty(transFormConfig.getNodePath())) {
      return this;
    }
    if (this.transformConfigList == null) {
      this.transformConfigList = new ArrayList<>();
    }
    this.transformConfigList.add(transFormConfig);
    return this;
  }

  public CompareOptions putTransformConfig(Collection<TransformConfig> transformConfigList) {
    if (transformConfigList == null || transformConfigList.isEmpty()) {
      return this;
    }
    if (this.transformConfigList == null) {
      this.transformConfigList = new ArrayList<>();
    }
    this.transformConfigList.addAll(transformConfigList);
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

  public CompareOptions putScriptCompareConfig(ScriptCompareConfig scriptCompareConfig) {
    if (scriptCompareConfig == null || ListUti.isEmpty(scriptCompareConfig.getNodePath())) {
      return this;
    }
    if (this.scriptCompareConfigList == null) {
      this.scriptCompareConfigList = new ArrayList<>();
    }
    this.scriptCompareConfigList.add(scriptCompareConfig);
    return this;
  }

  public CompareOptions putScriptCompareConfig(Collection<ScriptCompareConfig> scriptCompareConfigList) {
    if (scriptCompareConfigList == null || scriptCompareConfigList.isEmpty()) {
      return this;
    }
    if (this.scriptCompareConfigList == null) {
      this.scriptCompareConfigList = new ArrayList<>();
    }
    this.scriptCompareConfigList.addAll(scriptCompareConfigList);
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

  public CompareOptions putUuidIgnore(Boolean uuidIgnore) {
    this.uuidIgnore = uuidIgnore;
    return this;
  }

  public CompareOptions putIpIgnore(Boolean ipIgnore) {
    this.ipIgnore = ipIgnore;
    return this;
  }

  public CompareOptions putOnlyCompareExistListElements(Boolean onlyCompareExistListElements) {
    this.onlyCompareExistListElements = onlyCompareExistListElements;
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

  public List<DecompressConfig> getDecompressConfigList() {
    return decompressConfigList;
  }

  public List<TransformConfig> getTransFormConfigList() {
    return transformConfigList;
  }

  public Map<List<String>, List<String>> getReferenceConfig() {
    return referenceConfig;
  }

  public Map<List<String>, List<List<String>>> getListSortConfig() {
    return listSortConfig;
  }

  public List<ScriptCompareConfig> getScriptCompareConfigList() {
    return scriptCompareConfigList;
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

  public Boolean getUuidIgnore() {
    return uuidIgnore;
  }

  public Boolean getIpIgnore() {
    return ipIgnore;
  }

  public Boolean getOnlyCompareExistListElements() {
    return onlyCompareExistListElements;
  }

}
