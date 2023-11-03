package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.JSONParseUtil;
import com.arextest.diff.utils.NameConvertUtil;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class JSONParse {

  public Map<String, List<String>> doHandler(RulesConfig rulesConfig, Object baseObj,
      Object testObj) throws ExecutionException, InterruptedException {

    Callable<Map<List<NodeEntity>, String>> callable1 = () -> this.getJSONParseResult(baseObj,
        rulesConfig);

    Callable<Map<List<NodeEntity>, String>> callable2 = () -> this.getJSONParseResult(testObj,
        rulesConfig);

    Map<List<NodeEntity>, String> baseOriginal = TaskThreadFactory.jsonObjectThreadPool.submit(
        callable1).get();
    Map<List<NodeEntity>, String> testOriginal = TaskThreadFactory.jsonObjectThreadPool.submit(
        callable2).get();

    return JSONParseUtil.getTotalParses(baseOriginal, testOriginal);
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
