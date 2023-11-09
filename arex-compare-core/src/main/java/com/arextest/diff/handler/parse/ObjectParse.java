package com.arextest.diff.handler.parse;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.DecompressConfig;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.DecompressUtil;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectParse {

  private static Logger LOGGER = LoggerFactory.getLogger(ObjectParse.class);

  public MsgObjCombination doHandler(RulesConfig rulesConfig) throws Exception {

    MsgObjCombination response = new MsgObjCombination();

    Object obj1 = null, obj2 = null;
    Callable<Object> callable1 = () -> msgToObj(rulesConfig.getBaseMsg(), rulesConfig);
    Callable<Object> callable2 = () -> msgToObj(rulesConfig.getTestMsg(), rulesConfig);

    obj1 = TaskThreadFactory.jsonObjectThreadPool.submit(callable1).get();
    obj2 = TaskThreadFactory.jsonObjectThreadPool.submit(callable2).get();

    MutablePair<Object, Object> objectObjectMutablePair = compatibleDiffType(obj1, obj2);
    response.setBaseObj(objectObjectMutablePair.getLeft());
    response.setTestObj(objectObjectMutablePair.getRight());
    return response;

  }

  public Object msgToObj(String msg, RulesConfig rulesConfig) throws JsonProcessingException {
    Object obj = null;
    if (StringUtil.isEmpty(msg)) {
      return obj;
    }

    // process the msg
    String pluginJarUrl = rulesConfig.getPluginJarUrl();
    Map<List<String>, DecompressConfig> decompressConfigMap = rulesConfig.getDecompressConfigMap();
    if (decompressConfigMap != null && decompressConfigMap.containsKey(Constant.ROOT_PATH)) {
      try {
        msg = DecompressUtil.decompressPlugin(pluginJarUrl,
            decompressConfigMap.get(Constant.ROOT_PATH), msg);
      } catch (Throwable throwable) {
        LOGGER.error("decompress root error, msg:{}", msg, throwable);
      }
    }

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

  private MutablePair<Object, Object> compatibleDiffType(Object obj1, Object obj2)
      throws Exception {
    MutablePair<Object, Object> result = new MutablePair<>();
    if (obj1 == null || obj2 == null || !obj1.getClass().equals(obj2.getClass())) {
      throw new Exception("The JSON types corresponding to baseMsg and testMsg are inconsistent.");
    }
    result.setLeft(obj1);
    result.setRight(obj2);
    return result;
  }
}
