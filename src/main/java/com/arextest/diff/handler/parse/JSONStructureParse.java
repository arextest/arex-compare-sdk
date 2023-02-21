package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.parse.MsgStructure;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class JSONStructureParse {

    public CompletableFuture<MutablePair<MsgStructure, MsgStructure>> doHandler(Object baseObj, Object testObj) {
        CompletableFuture<MsgStructure> future1 = CompletableFuture.supplyAsync(() -> {
            MsgStructure msgStructure = new MsgStructure();
            getPathMap(baseObj, msgStructure);
            return msgStructure;
        }, TaskThreadFactory.structureHandlerThreadPool);

        CompletableFuture<MsgStructure> future2 = CompletableFuture.supplyAsync(() -> {
            MsgStructure msgStructure = new MsgStructure();
            getPathMap(testObj, msgStructure);
            return msgStructure;
        }, TaskThreadFactory.structureHandlerThreadPool);
        return future1.thenCombine(future2, MutablePair::new);
    }

    private void getPathMap(Object obj, MsgStructure msgStructure) {
        if (obj == null || obj instanceof NullNode) {
            return;
        }

        if (obj instanceof ObjectNode) {
            ObjectNode jsonObject = (ObjectNode) obj;
            List<String> names = JacksonHelperUtil.getNames(jsonObject);

            for (String fieldName : names) {
                MsgStructure tempMsgStructure = new MsgStructure(fieldName);
                msgStructure.getNode().put(fieldName, tempMsgStructure);
                Object objFieldValue = jsonObject.get(fieldName);
                getPathMap(objFieldValue, tempMsgStructure);
            }
        } else if (obj instanceof ArrayNode) {
            ArrayNode objArray = (ArrayNode) obj;
            for (int i = 0; i < objArray.size(); i++) {
                Object element = objArray.get(i);
                getPathMap(element, msgStructure);
            }
        }
    }
}
