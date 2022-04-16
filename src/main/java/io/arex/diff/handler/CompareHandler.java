package io.arex.diff.handler;

import io.arex.diff.compare.ReferenceCompare;
import io.arex.diff.model.RulesConfig;
import io.arex.diff.model.key.KeyComputeResponse;
import io.arex.diff.model.log.LogEntity;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class CompareHandler {

    public List<LogEntity> doHandler(RulesConfig rulesConfig, KeyComputeResponse keyComputeResponse, Object baseObj, Object testObj) throws JSONException {
        ReferenceCompare referenceCompare = new ReferenceCompare();
        List<LogEntity> logs = new ArrayList<>();
        referenceCompare.setLogs(logs);
        referenceCompare.setBaseObj(baseObj);
        referenceCompare.setTestObj(testObj);
        referenceCompare.setResponseReferences(keyComputeResponse.getAllReferenceEntities());
        referenceCompare.setListIndexKeysLeft(keyComputeResponse.getListIndexKeysLeft());
        referenceCompare.setListIndexKeysRight(keyComputeResponse.getListIndexKeysRight());
        referenceCompare.setNotDistinguishNullAndEmpty(rulesConfig.isNullEqualsEmpty());
        referenceCompare.jsonCompare(baseObj, testObj);
        return logs;
    }
}
