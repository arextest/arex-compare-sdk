package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.parse.MsgStructure;
import org.apache.commons.lang3.tuple.MutablePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
        CompletableFuture<MutablePair<MsgStructure, MsgStructure>> future3 = future1.thenCombine(future2, (r1, r2) -> new MutablePair(r1, r2));
        return future3;
    }

    private void getPathMap(Object obj, MsgStructure msgStructure) {
        if (obj == null || JSONObject.NULL.equals(obj)) {
            return;
        }

        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            String[] names = JSONObject.getNames(jsonObject);
            if (names == null) {
                names = new String[0];
            }

            for (String fieldName : names) {
                MsgStructure tempMsgStructure = new MsgStructure(fieldName);
                msgStructure.getNode().put(fieldName, tempMsgStructure);
                Object objFieldValue = jsonObject.get(fieldName);
                getPathMap(objFieldValue, tempMsgStructure);
            }
        } else if (obj instanceof JSONArray) {
            JSONArray objArray = (JSONArray) obj;
            for (int i = 0; i < objArray.length(); i++) {
                Object element = objArray.get(i);
                getPathMap(element, msgStructure);
            }
        }
    }
}
