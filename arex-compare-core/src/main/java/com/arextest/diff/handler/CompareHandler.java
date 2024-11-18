package com.arextest.diff.handler;

import com.arextest.diff.compare.GenericCompare;
import com.arextest.diff.handler.log.LogProcess;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.parse.MsgStructure;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.lang3.tuple.MutablePair;

public class CompareHandler {

  public List<LogEntity> doHandler(RulesConfig rulesConfig, KeyComputeResponse keyComputeResponse,
      CompletableFuture<MutablePair<MsgStructure, MsgStructure>> msgStructureFuture,
      Object baseObj, Object testObj, LogProcess logProcess) throws Exception {
    CompareContext compareContext = new CompareContext();

    List<LogEntity> logs = new ArrayList<>();
    compareContext.logs = logs;

    compareContext.baseObj = baseObj;
    compareContext.testObj = testObj;
    compareContext.listIndexKeysLeft = keyComputeResponse.getListIndexKeysLeft();
    compareContext.listIndexKeysRight = keyComputeResponse.getListIndexKeysRight();
    compareContext.setResponseReferences(keyComputeResponse.getAllReferenceEntities());

    compareContext.exclusions = rulesConfig.getExclusions();
    compareContext.conditionExclusions = rulesConfig.getConditionExclusions();
    compareContext.ignoreNodeSet = rulesConfig.getIgnoreNodeSet();

    compareContext.notDistinguishNullAndEmpty = rulesConfig.isNullEqualsEmpty();
    compareContext.nullEqualsNotExist = rulesConfig.isNullEqualsNotExist();
    compareContext.logProcess = logProcess;
    compareContext.quickCompare = rulesConfig.isQuickCompare();

    compareContext.scriptSandbox = rulesConfig.getScriptSandbox();
    compareContext.scriptCompareConfigMap = rulesConfig.getScriptCompareConfigMap();

    if (msgStructureFuture != null) {
      MutablePair<MsgStructure, MsgStructure> msgStructureMutablePair = msgStructureFuture.join();
      compareContext.baseMsgStructure = msgStructureMutablePair.getLeft();
      compareContext.testMsgStructure = msgStructureMutablePair.getRight();
    }
    GenericCompare.jsonCompare(baseObj, testObj, compareContext);
    return logs;
  }
}
