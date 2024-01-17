package com.arextest.diff.handler.keycompute;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.NodeEntity;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyCompute {

  private static final Logger LOGGER = LoggerFactory.getLogger(KeyCompute.class);

  public KeyComputeResponse doHandler(RulesConfig rulesConfig, Object baseObj, Object testObj)
      throws ExecutionException, InterruptedException, TimeoutException {

    List<ReferenceEntity> allReferenceEntities = rulesConfig.getReferenceEntities();
    List<ListSortEntity> listSortConfig = rulesConfig.getListSortEntities();

    CompletableFuture<HashMap<List<NodeEntity>, HashMap<Integer, String>>> future1 =
        CompletableFuture.supplyAsync(() -> {
          ListKeyProcess keyProcessLeft = new ListKeyProcess(allReferenceEntities, listSortConfig);
          keyProcessLeft.computeAllListKey(baseObj);
          return keyProcessLeft.getListIndexKeys();
        }, TaskThreadFactory.jsonObjectThreadPool);

    CompletableFuture<HashMap<List<NodeEntity>, HashMap<Integer, String>>> future2 =
        CompletableFuture.supplyAsync(() -> {
          ListKeyProcess keyProcessLeft = new ListKeyProcess(allReferenceEntities, listSortConfig);
          keyProcessLeft.computeAllListKey(testObj);
          return keyProcessLeft.getListIndexKeys();
        }, TaskThreadFactory.jsonObjectThreadPool);

    CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2);
    try {
      combinedFuture.get(Constant.KEY_COMPUTE_WAIT_MINUTES_TIME, TimeUnit.MINUTES);
    } catch (TimeoutException e) {
      LOGGER.error("KeyCompute doHandler TimeoutException", e);
      throw e;
    }

    HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft = future1.get();
    HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight = future2.get();

    KeyComputeResponse response = new KeyComputeResponse();
    response.setAllReferenceEntities(allReferenceEntities);
    response.setListSortEntities(listSortConfig);
    response.setListIndexKeysLeft(listIndexKeysLeft);
    response.setListIndexKeysRight(listIndexKeysRight);
    return response;
  }

}
