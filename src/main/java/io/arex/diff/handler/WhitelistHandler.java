package io.arex.diff.handler;

import io.arex.diff.factory.TaskThreadFactory;
import io.arex.diff.model.enumeration.Constant;
import io.arex.diff.model.parse.MsgObjCombination;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class WhitelistHandler {

    public MsgObjCombination doHandler(Object baseObj, Object testObj, List<List<String>> whiteList) throws ExecutionException, InterruptedException {
        if (whiteList != null && !whiteList.isEmpty()) {
            Callable<Object> callable1 = () -> getInclusionsTask(baseObj, whiteList);
            Callable<Object> callable2 = () -> getInclusionsTask(testObj, whiteList);
            Object baseNewObj = TaskThreadFactory.jsonObjectThreadPool.submit(callable1).get();
            Object testNewObj = TaskThreadFactory.jsonObjectThreadPool.submit(callable2).get();
            return new MsgObjCombination(baseNewObj, testNewObj);
        }
        return new MsgObjCombination(baseObj, testObj);
    }

    private Object getInclusionsTask(Object obj, List<List<String>> whiteList) throws JSONException {
        Object whiteObj = obj instanceof JSONObject ? new JSONObject() : new JSONArray();
        if (obj == null) {
            return null;
        }
        for (List<String> white : whiteList) {
            getInclusionsObj(obj, whiteObj, white);
        }
        return whiteObj;
    }

    private void getInclusionsObj(Object obj, Object whiteObj, List<String> white) throws JSONException {
        for (int i = 0; i < white.size(); i++) {
            String nodePath = white.get(i);
            Object tempObj = null;
            Object tempWhiteObj = null;
            if (obj instanceof JSONObject) {
                JSONObject jsonObject = ((JSONObject) obj);
                JSONObject jsonWhiteObj = ((JSONObject) whiteObj);
                if (Objects.equals(nodePath, Constant.DYNAMIC_PATH)) {
                    String[] names = JSONObject.getNames(jsonObject);
                    if (names == null) {
                        names = new String[0];
                    }
                    for (String name : names) {
                        tempObj = jsonObject.get(name);
                        if (i != white.size() - 1) {
                            try {
                                tempWhiteObj = jsonWhiteObj.get(name);
                                getInclusionsObj(tempObj, tempWhiteObj, white.subList(i + 1, white.size()));
                            } catch (JSONException e) {
                                if (jsonObject.get(name) instanceof JSONObject) {
                                    tempWhiteObj = new JSONObject();
                                    getInclusionsObj(tempObj, tempWhiteObj, white.subList(i + 1, white.size()));
                                    jsonWhiteObj.put(name, tempWhiteObj);
                                } else if (jsonObject.get(name) instanceof JSONArray) {
                                    tempWhiteObj = new JSONArray();
                                    getInclusionsObj(tempObj, tempWhiteObj, white.subList(i + 1, white.size()));
                                    jsonWhiteObj.put(name, tempWhiteObj);
                                }
                            }
                        } else {
                            jsonWhiteObj.put(name, tempObj);
                        }
                    }
                    return;
                } else {
                    try {
                        tempObj = jsonObject.get(nodePath);
                        try {
                            tempWhiteObj = jsonWhiteObj.get(nodePath);
                        } catch (JSONException e) {
                            if (tempObj instanceof JSONObject) {
                                tempWhiteObj = new JSONObject();
                                jsonWhiteObj.put(nodePath, tempWhiteObj);
                            } else if (tempObj instanceof JSONArray) {
                                tempWhiteObj = new JSONArray();
                                jsonWhiteObj.put(nodePath, tempWhiteObj);
                            }
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            } else if (obj instanceof JSONArray) {
                JSONArray objArr = (JSONArray) obj;
                JSONArray whiteObjArr = (JSONArray) whiteObj;
                for (int j = 0; j < objArr.length(); j++) {
                    tempObj = objArr.get(j);
                    try {
                        tempWhiteObj = whiteObjArr.get(j);
                        getInclusionsObj(tempObj, tempWhiteObj, white.subList(i, white.size()));
                    } catch (JSONException e) {
                        if (tempObj instanceof JSONObject) {
                            tempWhiteObj = new JSONObject();
                            getInclusionsObj(tempObj, tempWhiteObj, white.subList(i, white.size()));
                            whiteObjArr.put(j, tempWhiteObj);
                        } else if (tempObj instanceof JSONArray) {
                            tempWhiteObj = new JSONArray();
                            getInclusionsObj(tempObj, tempWhiteObj, white.subList(i, white.size()));
                            whiteObjArr.put(j, tempWhiteObj);
                        }
                    }
                }
                return;
            } else {
                return;
            }
            if (i == white.size() - 1) {
                if (whiteObj instanceof JSONObject) {
                    ((JSONObject) whiteObj).put(nodePath, tempObj);
                }
            }
            obj = tempObj;
            whiteObj = tempWhiteObj;
        }
    }


}
