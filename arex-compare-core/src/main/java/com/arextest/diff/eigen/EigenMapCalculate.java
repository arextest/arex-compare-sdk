package com.arextest.diff.eigen;

import com.arextest.diff.handler.log.filterrules.TimePrecisionFilter;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.utils.IgnoreUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EigenMapCalculate {

  private static final int OFFSET_BASIS = 0x811C9DC5; // 2166136261
  private static final int FNV_PRIME = 16777619;

  private static TimePrecisionFilter timePrecisionFilter = new TimePrecisionFilter(0);

  public EigenResult doCalculate(Object obj, RulesConfig rulesConfig, Map<Integer, Long> eigenMap) {

    EigenResult eigenResult = new EigenResult();
    if (obj == null || obj instanceof String) {
      eigenMap = new HashMap<>();
      eigenMap.put(0, this.valueHash(rulesConfig.getBaseMsg()));
      eigenResult.setEigenMap(eigenMap);
      return eigenResult;
    }

    CalculateContext calculateContext = new CalculateContext();
    calculateContext.ignoreNodeSet = rulesConfig.getIgnoreNodeSet();
    calculateContext.exclusions = rulesConfig.getExclusions();
    calculateContext.expressionExclusions = rulesConfig.getExpressionExclusions();

    calculateContext.nodePath = new LinkedList<>();
    calculateContext.currentNodeEntity = new LinkedList<>();
    doCalculateJsonNode(obj, calculateContext, eigenMap);
    eigenResult.setEigenMap(eigenMap);
    return eigenResult;
  }


  private void doCalculateJsonNode(Object obj, CalculateContext calculateContext,
      Map<Integer, Long> eigenMap) {

    // ignore by node name and node path
    if (IgnoreUtil.ignoreProcessorEngine(calculateContext.nodePath,
        calculateContext.currentNodeEntity,
        calculateContext.exclusions, calculateContext.expressionExclusions,
        calculateContext.ignoreNodeSet)) {
      return;
    }

    if (obj instanceof ObjectNode) {
      ObjectNode objectNode = (ObjectNode) obj;
      Iterator<String> stringIterator = objectNode.fieldNames();
      while (stringIterator.hasNext()) {
        String fieldName = stringIterator.next();
        JsonNode jsonNode = objectNode.get(fieldName);

        int lastHash = calculateContext.lastHash;
        calculateContext.nodePath.addLast(fieldName);
        calculateContext.currentNodeEntity.addLast(new NodeEntity(fieldName, 0));
        calculateContext.lastHash = this.pathHashWithLastHash(fieldName, lastHash);
        this.doCalculateJsonNode(jsonNode, calculateContext, eigenMap);
        calculateContext.lastHash = lastHash;
        calculateContext.nodePath.removeLast();
        calculateContext.currentNodeEntity.removeLast();
      }


    } else if (obj instanceof ArrayNode) {
      ArrayNode arrayNode = (ArrayNode) obj;
      for (int i = 0; i < arrayNode.size(); i++) {
        JsonNode jsonNode = arrayNode.get(i);
        calculateContext.currentNodeEntity.addLast(new NodeEntity(null, i));
        int lastHash = calculateContext.lastHash;
        this.doCalculateJsonNode(jsonNode, calculateContext, eigenMap);
        calculateContext.lastHash = lastHash;
        calculateContext.currentNodeEntity.removeLast();
      }
    } else {
      // calculate eigen value
      String value = obj == null ? null : obj.toString();

      // process time
      Instant instant = timePrecisionFilter.identifyTime(value);
      if (instant != null) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());
        value = formatter.format(instant);
      }
      int pathHash = calculateContext.lastHash;
      long valueHash = this.valueHash(value);
      eigenMap.put(pathHash, eigenMap.getOrDefault(pathHash, 0L) + valueHash);
    }
  }

  private int pathHashWithLastHash(String nodeName, int lastHash) {
    int key = lastHash;
    for (byte c : nodeName.getBytes()) {
      key = (key ^ c) * FNV_PRIME;
    }
    return Math.abs(key);
  }

  // FNV-1a hash function, think about null and empty string
  private long valueHash(String value) {
    if (value == null) {
      return 1;
    }
    if (value.isEmpty()) {
      return 2;
    }

    int key = OFFSET_BASIS;
    for (byte c : value.getBytes()) {
      key = (key ^ c) * FNV_PRIME;
    }
    key += key << 13;
    key ^= key >> 7;
    key += key << 3;
    key ^= key >> 17;
    key += key << 5;
    return Math.abs(key);
  }


  private static class CalculateContext {

    private LinkedList<String> nodePath;
    private LinkedList<NodeEntity> currentNodeEntity;

    private int lastHash = OFFSET_BASIS;

    private Set<String> ignoreNodeSet;
    private List<List<ExpressionNodeEntity>> exclusions;
    private List<List<ExpressionNodeEntity>> expressionExclusions;

    public CalculateContext() {
    }
  }
}
