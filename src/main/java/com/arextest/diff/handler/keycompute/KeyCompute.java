package com.arextest.diff.handler.keycompute;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.NodeEntity;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;


public class KeyCompute {

    public KeyComputeResponse doHandler(RulesConfig rulesConfig, Object baseObj, Object testObj) throws ExecutionException, InterruptedException {

        List<ReferenceEntity> allReferenceEntities = rulesConfig.getReferenceEntities();
        List<ListSortEntity> listSortConfig = rulesConfig.getListSortEntities();

        Callable<HashMap<List<NodeEntity>, HashMap<Integer, String>>> callable1 = () -> {
            ListKeyProcess keyProcessLeft = new ListKeyProcess(allReferenceEntities, listSortConfig);
            keyProcessLeft.computeAllListKey(baseObj);
            return keyProcessLeft.getListIndexKeys();
        };

        Callable<HashMap<List<NodeEntity>, HashMap<Integer, String>>> callable2 = () -> {
            ListKeyProcess keyProcessRight = new ListKeyProcess(allReferenceEntities, listSortConfig);
            keyProcessRight.computeAllListKey(testObj);
            return keyProcessRight.getListIndexKeys();
        };

        HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = TaskThreadFactory.jsonObjectThreadPool.submit(callable1).get();
        HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = TaskThreadFactory.jsonObjectThreadPool.submit(callable2).get();

        KeyComputeResponse response = new KeyComputeResponse();
        response.setAllReferenceEntities(allReferenceEntities);
        response.setListSortEntities(listSortConfig);
        response.setListIndexKeysLeft(listIndexKeysLeft);
        response.setListIndexKeysRight(listIndexKeysRight);
        return response;
    }


}
