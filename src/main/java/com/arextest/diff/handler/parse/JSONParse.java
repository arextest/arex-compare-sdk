package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.utils.NameConvertUtil;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.JSONParseUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class JSONParse {

    public Map<String, List<String>> doHandler(RulesConfig rulesConfig, Object baseObj, Object testObj) throws ExecutionException, InterruptedException {

        StringAndCompressParse baseParse = new StringAndCompressParse();
        StringAndCompressParse testParse = new StringAndCompressParse();

        Callable<Map<List<NodeEntity>, String>> callable1 = () -> {
            baseParse.setNameToLower(rulesConfig.isNameToLower());
            baseParse.setDecompressServices(rulesConfig.getDecompressServices());
            baseParse.setDecompressConfig(convertDecompressConfig(rulesConfig.getDecompressConfig()));
            baseParse.getJSONParse(baseObj, baseObj);
            // Convert field names in JSONObject to lowercase
            if (rulesConfig.isNameToLower()) {
                NameConvertUtil.nameConvert(baseObj);
            }
            return baseParse.getOriginal();
        };

        Callable<Map<List<NodeEntity>, String>> callable2 = () -> {
            testParse.setNameToLower(rulesConfig.isNameToLower());
            testParse.setDecompressServices(rulesConfig.getDecompressServices());
            testParse.setDecompressConfig(convertDecompressConfig(rulesConfig.getDecompressConfig()));
            testParse.getJSONParse(testObj, testObj);
            // Convert field names in JSONObject to lowercase
            if (rulesConfig.isNameToLower()) {
                NameConvertUtil.nameConvert(testObj);
            }
            return testParse.getOriginal();
        };

        Map<List<NodeEntity>, String> baseOriginal = TaskThreadFactory.jsonObjectThreadPool.submit(callable1).get();
        Map<List<NodeEntity>, String> testOriginal = TaskThreadFactory.jsonObjectThreadPool.submit(callable2).get();

        return JSONParseUtil.getTotalParses(baseOriginal, testOriginal);

    }

    private Map<String, String> convertDecompressConfig(Map<String, List<String>> decompressConfig) {
        if (decompressConfig == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        decompressConfig.forEach((k, v) -> {
            if (v != null) {
                v.forEach(item -> {
                    result.put(item, k);
                });
            }
        });
        return result;
    }
}
