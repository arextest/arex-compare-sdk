package com.arextest.diff.model;

import com.arextest.diff.model.TransformConfig.TransformMethod;
import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.model.script.ScriptCompareConfig.ScriptMethod;
import com.arextest.diff.model.script.ScriptSandbox;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public class RulesConfig {

  private ScriptSandbox scriptSandbox;

  /**
   * @see CategoryType
   */
  private String categoryType;

  private String baseMsg;

  private String testMsg;

  private boolean quickCompare;

  /**
   * to receive the field "pluginJarUrl" in CompareOptions
   */
  private String pluginJarUrl;

  private Map<List<String>, List<TransformMethod>> transformConfigMap;

  private List<List<String>> inclusions;
  private List<List<ExpressionNodeEntity>> exclusions;

  private Set<String> ignoreNodeSet;

  private List<ReferenceEntity> referenceEntities = Collections.emptyList();

  private List<ListSortEntity> listSortEntities = Collections.emptyList();

  private Map<List<String>, ScriptMethod> scriptCompareConfigMap;

  private boolean nameToLower;

  private boolean nullEqualsEmpty;

  private boolean selectIgnoreCompare;

  private boolean onlyCompareCoincidentColumn;

  private long ignoredTimePrecision;

  private boolean nullEqualsNotExist;

  private boolean uuidIgnore;

  private boolean ipIgnore;

  private boolean onlyCompareExistListElements;

  //region: inner processed class
  private List<List<ExpressionNodeEntity>> expressionExclusions;

  private Map<LinkedList<LinkedList<ExpressionNodeEntity>>, LinkedList<LinkedList<ExpressionNodeEntity>>> conditionExclusions;

  //endregion

  public RulesConfig() {
  }

  public ScriptSandbox getScriptSandbox() {
    return scriptSandbox;
  }

  public void setScriptSandbox(ScriptSandbox scriptSandbox) {
    this.scriptSandbox = scriptSandbox;
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

  public boolean isQuickCompare() {
    return quickCompare;
  }

  public void setQuickCompare(boolean quickCompare) {
    this.quickCompare = quickCompare;
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


  public List<List<ExpressionNodeEntity>> getExclusions() {
    return exclusions;
  }

  public void setExclusions(List<List<ExpressionNodeEntity>> exclusions) {
    this.exclusions = exclusions;
  }

  public Set<String> getIgnoreNodeSet() {
    return ignoreNodeSet;
  }

  public void setIgnoreNodeSet(Set<String> ignoreNodeSet) {
    this.ignoreNodeSet = ignoreNodeSet;
  }

  public Map<List<String>, List<TransformMethod>> getTransformConfigMap() {
    return transformConfigMap;
  }

  public void setTransformConfigMap(
      Map<List<String>, List<TransformMethod>> transformConfigMap) {
    this.transformConfigMap = transformConfigMap;
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

  public Map<List<String>, ScriptMethod> getScriptCompareConfigMap() {
    return scriptCompareConfigMap;
  }

  public void setScriptCompareConfigMap(
      Map<List<String>, ScriptMethod> scriptCompareConfigMap) {
    this.scriptCompareConfigMap = scriptCompareConfigMap;
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

  public boolean isUuidIgnore() {
    return uuidIgnore;
  }

  public void setUuidIgnore(boolean uuidIgnore) {
    this.uuidIgnore = uuidIgnore;
  }

  public boolean isIpIgnore() {
    return ipIgnore;
  }

  public void setIpIgnore(boolean ipIgnore) {
    this.ipIgnore = ipIgnore;
  }

  public boolean isOnlyCompareExistListElements() {
    return onlyCompareExistListElements;
  }

  public void setOnlyCompareExistListElements(boolean onlyCompareExistListElements) {
    this.onlyCompareExistListElements = onlyCompareExistListElements;
  }

  public List<List<ExpressionNodeEntity>> getExpressionExclusions() {
    return expressionExclusions;
  }

  public void setExpressionExclusions(List<List<ExpressionNodeEntity>> expressionExclusions) {
    this.expressionExclusions = expressionExclusions;
  }

  public Map<LinkedList<LinkedList<ExpressionNodeEntity>>, LinkedList<LinkedList<ExpressionNodeEntity>>> getConditionExclusions() {
    return conditionExclusions;
  }

  public void setConditionExclusions(
      Map<LinkedList<LinkedList<ExpressionNodeEntity>>, LinkedList<LinkedList<ExpressionNodeEntity>>> conditionExclusions) {
    this.conditionExclusions = conditionExclusions;
  }
}
