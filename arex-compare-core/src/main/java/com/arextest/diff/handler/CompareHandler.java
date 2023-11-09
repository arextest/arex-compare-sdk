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
    compareContext.setLogs(logs);

    compareContext.setBaseObj(baseObj);
    compareContext.setTestObj(testObj);
    compareContext.setResponseReferences(keyComputeResponse.getAllReferenceEntities());
    compareContext.setListIndexKeysLeft(keyComputeResponse.getListIndexKeysLeft());
    compareContext.setListIndexKeysRight(keyComputeResponse.getListIndexKeysRight());

    compareContext.setExclusions(rulesConfig.getExclusions());
    compareContext.setIgnoreNodeSet(rulesConfig.getIgnoreNodeSet());
    compareContext.setNotDistinguishNullAndEmpty(rulesConfig.isNullEqualsEmpty());
    compareContext.setNullEqualsNotExist(rulesConfig.isNullEqualsNotExist());
    compareContext.setLogProcess(logProcess);
    compareContext.setQuickCompare(rulesConfig.isQuickCompare());

    if (msgStructureFuture != null) {
      MutablePair<MsgStructure, MsgStructure> msgStructureMutablePair = msgStructureFuture.join();
      compareContext.setBaseMsgStructure(msgStructureMutablePair.getLeft());
      compareContext.setTestMsgStructure(msgStructureMutablePair.getRight());
    }
    GenericCompare.jsonCompare(baseObj, testObj, compareContext);
    return logs;
  }
}
