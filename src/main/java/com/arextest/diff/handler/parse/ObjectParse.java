package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.MutablePair;

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

    private Object msgToObj(String msg) throws JsonProcessingException {
        Object obj = null;
        if (StringUtil.isEmpty(msg)) {
            return obj;
        }
        if (msg.startsWith("[")) {
            obj = JacksonHelperUtil.objectMapper.readValue(msg, ArrayNode.class);
        } else {
            obj = JacksonHelperUtil.objectMapper.readValue(msg, ObjectNode.class);
        }
        return obj;
    }

    private MutablePair<Object, Object> compatibleDiffType(Object obj1, Object obj2) throws Exception {
        MutablePair<Object, Object> result = new MutablePair<>();
        if (obj1 == null || obj2 == null || !obj1.getClass().equals(obj2.getClass())) {
            throw new Exception("The JSON types corresponding to baseMsg and testMsg are inconsistent.");
        }
        result.setLeft(obj1);
        result.setRight(obj2);
        return result;
    }
}
