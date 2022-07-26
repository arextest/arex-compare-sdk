package com.arextest.diff.handler;

import com.arextest.diff.compare.CompareContext;
import com.arextest.diff.compare.GenericCompare;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.log.LogEntity;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CompareHandler {

    public List<LogEntity> doHandler(RulesConfig rulesConfig, KeyComputeResponse keyComputeResponse, Object baseObj, Object testObj) throws JSONException {
        // ReferenceCompare referenceCompare = new ReferenceCompare();
        // List<LogEntity> logs = new ArrayList<>();
        // referenceCompare.setLogs(logs);
        // referenceCompare.setBaseObj(baseObj);
        // referenceCompare.setTestObj(testObj);
        // referenceCompare.setResponseReferences(keyComputeResponse.getAllReferenceEntities());
        // referenceCompare.setListIndexKeysLeft(keyComputeResponse.getListIndexKeysLeft());
        // referenceCompare.setListIndexKeysRight(keyComputeResponse.getListIndexKeysRight());
        // referenceCompare.setNotDistinguishNullAndEmpty(rulesConfig.isNullEqualsEmpty());
        // referenceCompare.jsonCompare(baseObj, testObj);

        CompareContext compareContext = new CompareContext();
        List<LogEntity> logs = new ArrayList<>();
        compareContext.setLogs(logs);
        compareContext.setBaseObj(baseObj);
        compareContext.setTestObj(testObj);
        compareContext.setResponseReferences(keyComputeResponse.getAllReferenceEntities());
        compareContext.setListIndexKeysLeft(keyComputeResponse.getListIndexKeysLeft());
        compareContext.setListIndexKeysRight(keyComputeResponse.getListIndexKeysRight());
        compareContext.setNotDistinguishNullAndEmpty(rulesConfig.isNullEqualsEmpty());
        GenericCompare.jsonCompare(baseObj, testObj, compareContext);
        return logs;
    }
}
