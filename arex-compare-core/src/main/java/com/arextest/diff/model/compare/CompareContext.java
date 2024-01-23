package com.arextest.diff.model.compare;

import com.arextest.diff.handler.log.LogProcess;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.parse.MsgStructure;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareContext {

  // baseObj and testObj are the original objects to be compared
  public Object baseObj;
  public Object testObj;

  // currentBaseObj and currentTestObj are the objects being compared
  public Object currentBaseObj;
  public Object currentTestObj;

  public List<List<ExpressionNodeEntity>> exclusions;
  public List<List<ExpressionNodeEntity>> expressionExclusions;
  public Set<String> ignoreNodeSet;
  public boolean notDistinguishNullAndEmpty = false;
  public boolean nullEqualsNotExist = false;
  public LogProcess logProcess;
  public boolean quickCompare;


  public Map<List<String>, Object> refPkListNodeCacheLeft = new HashMap<>();
  public Map<List<String>, Object> refPkListNodeCacheRight = new HashMap<>();

  public List<String> currentListKeysLeft = new ArrayList<>();
  public List<String> currentListKeysRight = new ArrayList<>();
  // compare reference trace
  public List<List<NodeEntity>> currentTraceLeft;
  public List<List<NodeEntity>> currentTraceRight;

  public List<List<NodeEntity>> currentTraceLeftForShow;
  public List<List<NodeEntity>> currentTraceRightForShow;

  public List<NodeEntity> currentNodeLeft = new ArrayList<>();
  public List<NodeEntity> currentNodeRight = new ArrayList<>();

  public List<ReferenceEntity> responseReferences;
  public HashSet<List<String>> pkNodePaths;
  // store the pkList has compared the paired index,
  // because of the situation that the paired indexes compared by the reference
  public Map<List<String>, List<IndexPair>> pkListIndexPair;
  public HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = new HashMap<>();
  public HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = new HashMap<>();

  public List<LogEntity> logs;

  public MsgStructure baseMsgStructure;
  public MsgStructure testMsgStructure;

  public byte ignoreReferenceNotFound = 0;

  public CompareContext() {
    this.currentTraceLeft = new ArrayList<>();
    this.currentTraceRight = new ArrayList<>();
    this.currentTraceLeftForShow = new ArrayList<>();
    this.currentTraceRightForShow = new ArrayList<>();
  }

  public void setResponseReferences(List<ReferenceEntity> responseReferences) {
    this.responseReferences = responseReferences;
    HashSet<List<String>> pkNodePaths = new HashSet<>();
    pkListIndexPair = new HashMap<>();
    for (ReferenceEntity responseReference : responseReferences) {
      pkNodePaths.add(responseReference.getPkNodePath());
      pkListIndexPair.put(responseReference.getPkNodeListPath(), new ArrayList<>());
    }
    this.pkNodePaths = pkNodePaths;
  }


}
