package com.arextest.diff.compare;

import com.arextest.diff.model.compare.IndexPair;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.parse.MsgStructure;
import com.google.common.collect.Lists;

import java.util.*;

public class CompareContext {

    public Object baseObj;
    public Object testObj;

    public List<List<String>> exclusions;

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
    // store the pkList has compared the paired index, because of the situation that the paired indexes compared by the reference
    public Map<List<String>, List<IndexPair>> pkListIndexPair;
    public HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = new HashMap<>();
    public HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = new HashMap<>();

    public List<LogEntity> logs;

    public MsgStructure baseMsgStructure;
    public MsgStructure testMsgStructure;

    public byte ignoreReferenceNotFound = 0;

    public boolean notDistinguishNullAndEmpty = false;

    public CompareContext() {
        this.currentTraceLeft = Lists.newArrayList();
        this.currentTraceRight = Lists.newArrayList();
        this.currentTraceLeftForShow = Lists.newArrayList();
        this.currentTraceRightForShow = Lists.newArrayList();
    }

    public Object getBaseObj() {
        return baseObj;
    }

    public void setBaseObj(Object baseObj) {
        this.baseObj = baseObj;
    }

    public Object getTestObj() {
        return testObj;
    }

    public void setTestObj(Object testObj) {
        this.testObj = testObj;
    }

    public void setExclusions(List<List<String>> exclusions){
        this.exclusions = exclusions;
    }

    public List<List<String>> getExclusions(){
        return exclusions;
    }

    public Map<List<String>, Object> getRefPkListNodeCacheLeft() {
        return refPkListNodeCacheLeft;
    }

    public void setRefPkListNodeCacheLeft(Map<List<String>, Object> refPkListNodeCacheLeft) {
        this.refPkListNodeCacheLeft = refPkListNodeCacheLeft;
    }

    public Map<List<String>, Object> getRefPkListNodeCacheRight() {
        return refPkListNodeCacheRight;
    }

    public void setRefPkListNodeCacheRight(Map<List<String>, Object> refPkListNodeCacheRight) {
        this.refPkListNodeCacheRight = refPkListNodeCacheRight;
    }

    public List<String> getCurrentListKeysLeft() {
        return currentListKeysLeft;
    }

    public void setCurrentListKeysLeft(List<String> currentListKeysLeft) {
        this.currentListKeysLeft = currentListKeysLeft;
    }

    public List<String> getCurrentListKeysRight() {
        return currentListKeysRight;
    }

    public void setCurrentListKeysRight(List<String> currentListKeysRight) {
        this.currentListKeysRight = currentListKeysRight;
    }

    public List<List<NodeEntity>> getCurrentTraceLeft() {
        return currentTraceLeft;
    }

    public void setCurrentTraceLeft(List<List<NodeEntity>> currentTraceLeft) {
        this.currentTraceLeft = currentTraceLeft;
    }

    public List<List<NodeEntity>> getCurrentTraceRight() {
        return currentTraceRight;
    }

    public void setCurrentTraceRight(List<List<NodeEntity>> currentTraceRight) {
        this.currentTraceRight = currentTraceRight;
    }

    public List<List<NodeEntity>> getCurrentTraceLeftForShow() {
        return currentTraceLeftForShow;
    }

    public void setCurrentTraceLeftForShow(List<List<NodeEntity>> currentTraceLeftForShow) {
        this.currentTraceLeftForShow = currentTraceLeftForShow;
    }

    public List<List<NodeEntity>> getCurrentTraceRightForShow() {
        return currentTraceRightForShow;
    }

    public void setCurrentTraceRightForShow(List<List<NodeEntity>> currentTraceRightForShow) {
        this.currentTraceRightForShow = currentTraceRightForShow;
    }

    public List<NodeEntity> getCurrentNodeLeft() {
        return currentNodeLeft;
    }

    public void setCurrentNodeLeft(List<NodeEntity> currentNodeLeft) {
        this.currentNodeLeft = currentNodeLeft;
    }

    public List<NodeEntity> getCurrentNodeRight() {
        return currentNodeRight;
    }

    public void setCurrentNodeRight(List<NodeEntity> currentNodeRight) {
        this.currentNodeRight = currentNodeRight;
    }

    public List<ReferenceEntity> getResponseReferences() {
        return responseReferences;
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

    public HashSet<List<String>> getPkNodePaths() {
        return pkNodePaths;
    }

    public void setPkNodePaths(HashSet<List<String>> pkNodePaths) {
        this.pkNodePaths = pkNodePaths;
    }

    public Map<List<String>, List<IndexPair>> getPkListIndexPair() {
        return pkListIndexPair;
    }

    public void setPkListIndexPair(Map<List<String>, List<IndexPair>> pkListIndexPair) {
        this.pkListIndexPair = pkListIndexPair;
    }

    public HashMap<List<NodeEntity>, HashMap<Integer, String>> getListIndexKeysLeft() {
        return listIndexKeysLeft;
    }

    public void setListIndexKeysLeft(HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft) {
        this.listIndexKeysLeft = listIndexKeysLeft;
    }

    public HashMap<List<NodeEntity>, HashMap<Integer, String>> getListIndexKeysRight() {
        return listIndexKeysRight;
    }

    public void setListIndexKeysRight(HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight) {
        this.listIndexKeysRight = listIndexKeysRight;
    }

    public List<LogEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEntity> logs) {
        this.logs = logs;
    }

    public MsgStructure getBaseMsgStructure() {
        return baseMsgStructure;
    }

    public void setBaseMsgStructure(MsgStructure baseMsgStructure) {
        this.baseMsgStructure = baseMsgStructure;
    }

    public MsgStructure getTestMsgStructure() {
        return testMsgStructure;
    }

    public void setTestMsgStructure(MsgStructure testMsgStructure) {
        this.testMsgStructure = testMsgStructure;
    }

    public byte getIgnoreReferenceNotFound() {
        return ignoreReferenceNotFound;
    }

    public void setIgnoreReferenceNotFound(byte ignoreReferenceNotFound) {
        this.ignoreReferenceNotFound = ignoreReferenceNotFound;
    }

    public boolean isNotDistinguishNullAndEmpty() {
        return notDistinguishNullAndEmpty;
    }

    public void setNotDistinguishNullAndEmpty(boolean notDistinguishNullAndEmpty) {
        this.notDistinguishNullAndEmpty = notDistinguishNullAndEmpty;
    }
}
