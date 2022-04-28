package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.utils.StringUtil;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.parse.MsgObjCombination;
import org.apache.commons.lang3.tuple.MutablePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Callable;

public class ObjectParse {

    public MsgObjCombination doHandler(RulesConfig rulesConfig) throws Exception {

        MsgObjCombination response = new MsgObjCombination();

        Object obj1 = null, obj2 = null;
        Callable<Object> callable1 = () -> msgToObj(rulesConfig.getBaseMsg());
        Callable<Object> callable2 = () -> msgToObj(rulesConfig.getTestMsg());

        obj1 = TaskThreadFactory.jsonObjectThreadPool.submit(callable1).get();
        obj2 = TaskThreadFactory.jsonObjectThreadPool.submit(callable2).get();

        MutablePair<Object, Object> objectObjectMutablePair = compatibleDiffType(obj1, obj2);
        response.setBaseObj(objectObjectMutablePair.getLeft());
        response.setTestObj(objectObjectMutablePair.getRight());
        return response;

    }

    private Object msgToObj(String msg) throws JSONException {
        Object obj = null;
        if (StringUtil.isEmpty(msg)) {
            obj = null;
        } else if (msg.startsWith("[")) {
            obj = new JSONArray(msg);
        } else {
            obj = new JSONObject(msg);
        }
        return obj;
    }

    private MutablePair<Object, Object> compatibleDiffType(Object obj1, Object obj2) throws Exception {
        MutablePair<Object, Object> result = new MutablePair<>();
        if (obj1 == null && obj2 == null) {
            result.setLeft("");
            result.setRight("");
        } else if (obj1 == null) {
            if (obj2 instanceof JSONObject) {
                obj1 = new JSONObject();
            }else if (obj2 instanceof JSONArray){
                obj1 = new JSONArray();
            }else{
               throw new Exception("exist string");
            }
        }else if (obj2 == null){
            if (obj1 instanceof JSONObject){
                obj2 = new JSONObject();
            }else if (obj1 instanceof JSONArray){
                obj2 = new JSONArray();
            }else {
                throw new Exception("exist string");
            }
        }
        result.setLeft(obj1);
        result.setRight(obj2);
        return result;
    }
}
