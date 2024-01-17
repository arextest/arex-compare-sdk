package com.arextest.diff.handler;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WhitelistHandler {

  public MsgObjCombination doHandler(Object baseObj, Object testObj, List<List<String>> whiteList)
      throws ExecutionException, InterruptedException {
    if (whiteList != null && !whiteList.isEmpty()) {

      CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(
          () -> getInclusionsTask(baseObj, whiteList), TaskThreadFactory.jsonObjectThreadPool);
      CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(
          () -> getInclusionsTask(testObj, whiteList), TaskThreadFactory.jsonObjectThreadPool);
      CompletableFuture.allOf(future1, future2).join();

      return new MsgObjCombination(future1.get(), future2.get());
    }
    return new MsgObjCombination(baseObj, testObj);
  }

  private Object getInclusionsTask(Object obj, List<List<String>> whiteList) {
    Object whiteObj = obj instanceof ObjectNode
        ? JacksonHelperUtil.getObjectNode()
        : JacksonHelperUtil.getArrayNode();
    if (obj == null) {
      return null;
    }
    for (List<String> white : whiteList) {
      getInclusionsObj(obj, whiteObj, white);
    }
    return whiteObj;
  }

  private void getInclusionsObj(Object obj, Object whiteObj, List<String> white) {
    for (int i = 0; i < white.size(); i++) {
      String nodePath = white.get(i);
      Object tempObj = null;
      Object tempWhiteObj = null;
      if (obj instanceof ObjectNode) {
        ObjectNode jsonObject = ((ObjectNode) obj);
        ObjectNode jsonWhiteObj = ((ObjectNode) whiteObj);
        if (Objects.equals(nodePath, Constant.DYNAMIC_PATH)) {
          List<String> names = JacksonHelperUtil.getNames(jsonObject);
          for (String name : names) {
            tempObj = jsonObject.get(name);
            if (i != white.size() - 1) {
              tempWhiteObj = jsonWhiteObj.get(name);
              if (tempWhiteObj != null) {
                getInclusionsObj(tempObj, tempWhiteObj, white.subList(i + 1, white.size()));
              } else {
                if (tempObj instanceof ObjectNode) {
                  tempWhiteObj = JacksonHelperUtil.getObjectNode();
                  getInclusionsObj(tempObj, tempWhiteObj, white.subList(i + 1, white.size()));
                  jsonWhiteObj.set(name, (ObjectNode) tempWhiteObj);
                } else if (tempObj instanceof ArrayNode) {
                  tempWhiteObj = JacksonHelperUtil.getArrayNode();
                  getInclusionsObj(tempObj, tempWhiteObj, white.subList(i + 1, white.size()));
                  jsonWhiteObj.set(name, (ArrayNode) tempWhiteObj);
                }
              }
            } else {
              jsonWhiteObj.set(name, (JsonNode) tempObj);
            }
          }
          return;
        } else {
          tempObj = jsonObject.get(nodePath);
          if (tempObj == null) {
            return;
          }
          tempWhiteObj = jsonWhiteObj.get(nodePath);
          if (tempWhiteObj == null) {
            if (tempObj instanceof ObjectNode) {
              tempWhiteObj = JacksonHelperUtil.getObjectNode();
              jsonWhiteObj.set(nodePath, (ObjectNode) tempWhiteObj);
            } else if (tempObj instanceof ArrayNode) {
              tempWhiteObj = JacksonHelperUtil.getArrayNode();
              jsonWhiteObj.set(nodePath, (ArrayNode) tempWhiteObj);
            }
          }
        }
      } else if (obj instanceof ArrayNode) {
        ArrayNode objArr = (ArrayNode) obj;
        ArrayNode whiteObjArr = (ArrayNode) whiteObj;
        for (int j = 0; j < objArr.size(); j++) {
          tempObj = objArr.get(j);
          tempWhiteObj = whiteObjArr.get(j);
          if (tempWhiteObj != null) {
            getInclusionsObj(tempObj, tempWhiteObj, white.subList(i, white.size()));
          } else {
            if (tempObj instanceof ObjectNode) {
              tempWhiteObj = JacksonHelperUtil.getObjectNode();
              getInclusionsObj(tempObj, tempWhiteObj, white.subList(i, white.size()));
              whiteObjArr.add((ObjectNode) tempWhiteObj);
            } else if (tempObj instanceof ArrayNode) {
              tempWhiteObj = JacksonHelperUtil.getArrayNode();
              getInclusionsObj(tempObj, tempWhiteObj, white.subList(i, white.size()));
              whiteObjArr.add((ArrayNode) tempWhiteObj);
            }
          }
        }
        return;
      } else {
        return;
      }
      if (i == white.size() - 1) {
        if (whiteObj instanceof ObjectNode) {
          ((ObjectNode) whiteObj).set(nodePath, (JsonNode) tempObj);
        }
      }
      obj = tempObj;
      whiteObj = tempWhiteObj;
    }
  }


}
