package com.arextest.diff.handler;

import com.arextest.diff.compare.CompareContext;
import com.arextest.diff.compare.GenericCompare;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.parse.MsgStructure;
import org.apache.commons.lang3.tuple.MutablePair;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CompareHandler {

    public List<LogEntity> doHandler(RulesConfig rulesConfig, KeyComputeResponse keyComputeResponse, CompletableFuture<MutablePair<MsgStructure, MsgStructure>> msgStructureFuture,
                                     Object baseObj, Object testObj) throws JSONException {
        CompareContext compareContext = new CompareContext();
        List<LogEntity> logs = new ArrayList<>();
        compareContext.setLogs(logs);
        compareContext.setBaseObj(baseObj);
        compareContext.setTestObj(testObj);
        compareContext.setExclusions(rulesConfig.getExclusions());
        compareContext.setIgnoreNodeSet(rulesConfig.getIgnoreNodeSet());
        compareContext.setResponseReferences(keyComputeResponse.getAllReferenceEntities());
        compareContext.setListIndexKeysLeft(keyComputeResponse.getListIndexKeysLeft());
        compareContext.setListIndexKeysRight(keyComputeResponse.getListIndexKeysRight());
        compareContext.setNotDistinguishNullAndEmpty(rulesConfig.isNullEqualsEmpty());
        if (msgStructureFuture != null) {
            MutablePair<MsgStructure, MsgStructure> msgStructureMutablePair = msgStructureFuture.join();
            compareContext.setBaseMsgStructure(msgStructureMutablePair.getLeft());
            compareContext.setTestMsgStructure(msgStructureMutablePair.getRight());
        }
        GenericCompare.jsonCompare(baseObj, testObj, compareContext);
        return logs;
    }
}
