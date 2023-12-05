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

    response.setBaseObj(obj1);
    response.setTestObj(obj2);
    return response;

  }

  public Object msgToObj(String msg, RulesConfig rulesConfig) {
    if (StringUtil.isEmpty(msg)) {
      return msg;
    }

    Object obj = null;
    // process the msg
    String pluginJarUrl = rulesConfig.getPluginJarUrl();
    Map<List<String>, DecompressConfig> decompressConfigMap = rulesConfig.getDecompressConfigMap();
    if (decompressConfigMap != null && decompressConfigMap.containsKey(Constant.ROOT_PATH)) {
      try {
        String decompressMsg = DecompressUtil.decompressPlugin(pluginJarUrl,
            decompressConfigMap.get(Constant.ROOT_PATH), msg);
        if (!StringUtil.isEmpty(decompressMsg)) {
          msg = decompressMsg;
        } else {
          LOGGER.error("decompress root error");
        }
      } catch (Throwable throwable) {
        LOGGER.error("decompress root error", throwable);
      }
    }

    try {
      if (msg.startsWith("[")) {
        obj = JacksonHelperUtil.objectMapper.readValue(msg, ArrayNode.class);
      } else if (msg.startsWith("{")) {
        obj = JacksonHelperUtil.objectMapper.readValue(msg, ObjectNode.class);
      } else {
        obj = msg;
      }
    } catch (RuntimeException | JsonProcessingException e) {
      obj = msg;
    }
    return obj;
  }
}
