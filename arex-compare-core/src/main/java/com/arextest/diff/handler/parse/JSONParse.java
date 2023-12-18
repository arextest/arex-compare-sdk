package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.JSONParseUtil;
import com.arextest.diff.utils.NameConvertUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class JSONParse {

  public Map<String, List<String>> doHandler(RulesConfig rulesConfig, Object baseObj,
      Object testObj) throws ExecutionException, InterruptedException {

    CompletableFuture<Map<List<NodeEntity>, String>> mapCompletableFuture1 =
        CompletableFuture.supplyAsync(
            () -> this.getJSONParseResult(baseObj,
                rulesConfig), TaskThreadFactory.jsonObjectThreadPool);
    CompletableFuture<Map<List<NodeEntity>, String>> mapCompletableFuture2 =
        CompletableFuture.supplyAsync(
            () -> this.getJSONParseResult(testObj,
                rulesConfig), TaskThreadFactory.jsonObjectThreadPool);
    CompletableFuture.allOf(mapCompletableFuture1, mapCompletableFuture2).join();

    return JSONParseUtil.getTotalParses(mapCompletableFuture1.get(), mapCompletableFuture2.get());
  }

  public Map<List<NodeEntity>, String> getJSONParseResult(Object obj, RulesConfig rulesConfig) {

    StringAndCompressParse stringAndCompressParse = new StringAndCompressParse();
    stringAndCompressParse.setNameToLower(rulesConfig.isNameToLower());
    stringAndCompressParse.setPluginJarUrl(rulesConfig.getPluginJarUrl());
    stringAndCompressParse.setDecompressConfig(rulesConfig.getDecompressConfigMap());
    stringAndCompressParse.getJSONParse(obj, obj);
    // Convert field names in JSONObject to lowercase
    if (rulesConfig.isNameToLower()) {
      NameConvertUtil.nameConvert(obj);
    }
    return stringAndCompressParse.getOriginal();
  }
}
