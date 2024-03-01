package com.arextest.diff.model;

import com.arextest.diff.handler.decompress.TransformServiceBuilder;
import com.arextest.diff.utils.StringUtil;
import java.util.Set;

public class GlobalOptions {

  /**
   * The url address of the plug-in jar which is specified by the interface http or absolute path.
   * The decompressService which is loaded from this pluginJarUrl is the level of SYSTEM.
   */
  private String pluginJarUrl;

  /**
   * change the message and configuration to lowercase, for the inconsistency between the actual
   * message and the contract case
   */
  private Boolean nameToLower;

  /**
   * This option is true, The null, CollectionUtils.isEmpty and Strings.empty are equal for
   * example：the baseMsg: {"age":""} is consistent with testMsg: "{\"age\":null}"
   */
  private Boolean nullEqualsEmpty;

  /**
   * This refers to ignoring the precision of specified time fields when comparing them, which means
   * that if the difference between two fields is less than or equal to a certain parameter, they
   * are considered to be no error. Unit of time: mm
   */
  private long ignoredTimePrecision;

  /**
   * This option is true, The "null" and the situation that the field is not existed are equal for
   * example：the baseMsg: {"age":null} is consistent with testMsg: "{}"
   */
  private Boolean nullEqualsNotExist;

  /**
   * the nodenanme set which is ignored when comparing
   */
  private Set<String> ignoreNodeSet;

  /**
   * This option is true, the select statement does not compare
   */
  private Boolean selectIgnoreCompare;

  /**
   * only compare the overlapping columns ignore the non-overlapping columns This configuration
   * works only when compareType is CompareType.DATABASE
   */
  private Boolean onlyCompareCoincidentColumn;

  /**
   * This option is true, the uuid is ignored when comparing
   */
  private Boolean uuidIgnore;

  /**
   * This option is true, the ipV4 and ipV6 is ignored when comparing
   */
  private Boolean ipIgnore;

  public GlobalOptions() {
    this.nameToLower = false;
    this.nullEqualsEmpty = false;
  }

  public GlobalOptions putPluginJarUrl(String pluginJarUrl) {
    if (StringUtil.isEmpty(pluginJarUrl)) {
      return this;
    }
    this.pluginJarUrl = pluginJarUrl;
    TransformServiceBuilder.loadSystemDecompressService(pluginJarUrl);
    return this;
  }

  public GlobalOptions putNameToLower(Boolean nameToLower) {
    this.nameToLower = nameToLower;
    return this;
  }

  public GlobalOptions putNullEqualsEmpty(Boolean nullEqualsEmpty) {
    this.nullEqualsEmpty = nullEqualsEmpty;
    return this;
  }

  public GlobalOptions putIgnoredTimePrecision(long ignoredTimePrecision) {
    this.ignoredTimePrecision = ignoredTimePrecision;
    return this;
  }

  public GlobalOptions putNullEqualsNotExist(Boolean nullEqualsNotExist) {
    this.nullEqualsNotExist = nullEqualsNotExist;
    return this;
  }

  public GlobalOptions putIgnoreNodeSet(Set<String> ignoreNodeSet) {
    this.ignoreNodeSet = ignoreNodeSet;
    return this;
  }

  public GlobalOptions putSelectIgnoreCompare(Boolean selectIgnoreCompare) {
    this.selectIgnoreCompare = selectIgnoreCompare;
    return this;
  }

  public GlobalOptions putOnlyCompareCoincidentColumn(Boolean onlyCompareCoincidentColumn) {
    this.onlyCompareCoincidentColumn = onlyCompareCoincidentColumn;
    return this;
  }

  public GlobalOptions putUuidIgnore(Boolean uuidIgnore) {
    this.uuidIgnore = uuidIgnore;
    return this;
  }

  public GlobalOptions putIpIgnore(Boolean ipIgnore) {
    this.ipIgnore = ipIgnore;
    return this;
  }

  public String getPluginJarUrl() {
    return pluginJarUrl;
  }

  public Boolean isNameToLower() {
    return nameToLower;
  }

  public Boolean isNullEqualsEmpty() {
    return nullEqualsEmpty;
  }

  public long getIgnoredTimePrecision() {
    return ignoredTimePrecision;
  }

  public Boolean isNullEqualsNotExist() {
    return nullEqualsNotExist;
  }

  public Set<String> getIgnoreNodeSet() {
    return ignoreNodeSet;
  }

  public Boolean getSelectIgnoreCompare() {
    return selectIgnoreCompare;
  }

  public Boolean getOnlyCompareCoincidentColumn() {
    return onlyCompareCoincidentColumn;
  }

  public Boolean getUuidIgnore() {
    return uuidIgnore;
  }

  public Boolean getIpIgnore() {
    return ipIgnore;
  }

}
