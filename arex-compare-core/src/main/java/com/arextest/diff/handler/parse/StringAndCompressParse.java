package com.arextest.diff.handler.parse;

import com.arextest.diff.model.TransformConfig.TransformMethod;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.ListUti;
import com.arextest.diff.utils.StringUtil;
import com.arextest.diff.utils.TransformUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringAndCompressParse {

  private static final Logger LOGGER = LoggerFactory.getLogger(StringAndCompressParse.class);

  private List<NodeEntity> currentNode = new ArrayList<>();

  private Map<List<NodeEntity>, String> original = new HashMap<>();

  private boolean nameToLower;

  private String pluginJarUrl;
  private Map<List<String>, List<TransformMethod>> transFormConfigMap;

  public Map<List<NodeEntity>, String> getOriginal() {
    return original;
  }

  public void setNameToLower(boolean nameToLower) {
    this.nameToLower = nameToLower;
  }

  public void setTransFormConfigMap(
      Map<List<String>, List<TransformMethod>> transFormConfigMap) {
    this.transFormConfigMap = transFormConfigMap;
  }

  public void setPluginJarUrl(String pluginJarUrl) {
    this.pluginJarUrl = pluginJarUrl;
  }

  public void getJSONParse(Object obj, Object preObj, String currentName) {
    if (obj == null || obj instanceof NullNode) {
      return;
    }

    if (obj instanceof ObjectNode) {
      ObjectNode jsonObject = (ObjectNode) obj;
      List<String> names = JacksonHelperUtil.getNames(jsonObject);
      for (String fieldName : names) {
        currentNode.add(new NodeEntity(nameToLower ? fieldName.toLowerCase() : fieldName, 0));
        Object objFieldValue = jsonObject.get(fieldName);
        getJSONParse(objFieldValue, obj, fieldName);
        ListUti.removeLast(currentNode);
      }
    } else if (obj instanceof ArrayNode) {
      ArrayNode objArray = (ArrayNode) obj;
      for (int i = 0; i < objArray.size(); i++) {
        currentNode.add(new NodeEntity(null, i));
        Object element = objArray.get(i);
        getJSONParse(element, obj, String.valueOf(i));
        ListUti.removeLast(currentNode);
      }

    } else {

      String value = ((JsonNode) obj).asText();

      MutablePair<JsonNode, Boolean> objectBooleanPair = null;
      if (transFormConfigMap == null || transFormConfigMap.isEmpty()) {
        objectBooleanPair = processStringParse(value, preObj);
      } else {
        List<String> nodePath = ListUti.convertToStringList(currentNode);
        if (transFormConfigMap.containsKey(nodePath)) {
          List<TransformMethod> transformMethodList = this.transFormConfigMap.get(nodePath);
          objectBooleanPair = processCompress(value, this.pluginJarUrl, transformMethodList,
              preObj);
        } else {
          objectBooleanPair = processStringParse(value, preObj);
        }
      }

      if (objectBooleanPair.getKey() == null) {
        return;
      }
      if (Objects.equals(objectBooleanPair.getValue(), Boolean.TRUE)) {
        getJSONParse(objectBooleanPair.getKey(), preObj, null);
      }

      if (preObj instanceof ObjectNode) {
        ((ObjectNode) preObj).set(currentName, objectBooleanPair.getKey());
        original.put(new ArrayList<>(currentNode), value);
      } else if (preObj instanceof ArrayNode) {
        ((ArrayNode) preObj).set(Integer.parseInt(currentName), objectBooleanPair.getKey());
        original.put(new ArrayList<>(currentNode), value);
      }
    }

  }

  private String getCurrentName(List<NodeEntity> currentNode) {
    NodeEntity nodeEntity = currentNode.get(currentNode.size() - 1);
    if (nodeEntity.getNodeName() != null) {
      return nodeEntity.getNodeName();
    } else {
      return String.valueOf(nodeEntity.getIndex());
    }
  }

  /**
   * @param value
   * @param pluginJarUrl
   * @param transformMethodList
   * @param preObj
   * @return Pair<Object, Boolean>, the same define processStringParse() k: show weather
   * successfully process，if return null:fail,if return not null:success v: show Object instanceof
   * JSONObject or JSONArray which need to further performance getJSONParse
   */
  private MutablePair<JsonNode, Boolean> processCompress(String value, String pluginJarUrl,
      List<TransformMethod> transformMethodList, Object preObj) {
    String unCompressStr;
    try {
      unCompressStr = TransformUtil.transformPlugin(pluginJarUrl, transformMethodList, value);
    } catch (Throwable e) {
      LOGGER.warn("decompress fail, value:{}", value, e);
      return new MutablePair<>(null, Boolean.FALSE);
    }

    MutablePair<JsonNode, Boolean> objectBooleanPair = processStringParse(unCompressStr, preObj);
    if (objectBooleanPair.getKey() == null) {
      if (StringUtil.isEmpty(unCompressStr)) {
        return new MutablePair<>(null, Boolean.FALSE);
      } else {
        return new MutablePair<>(new TextNode(unCompressStr), Boolean.FALSE);
      }
    } else {
      return objectBooleanPair;
    }
  }

  private MutablePair<JsonNode, Boolean> processStringParse(String value, Object preObj) {

    JsonNode objTemp = null;

    if (StringUtil.isEmpty(value)) {
      return new MutablePair<>(null, Boolean.FALSE);
    }

    if (value.startsWith("{") && value.endsWith("}")) {
      try {
        objTemp = JacksonHelperUtil.objectMapper.readValue(value, ObjectNode.class);
      } catch (JsonProcessingException e) {
      }
    } else if (value.startsWith("[") && value.endsWith("]")) {
      try {
        objTemp = JacksonHelperUtil.objectMapper.readValue(value, ArrayNode.class);
      } catch (JsonProcessingException e) {
      }
    }
    return objTemp == null ? new MutablePair<>(null, Boolean.FALSE)
        : new MutablePair<>(objTemp, Boolean.TRUE);
  }

}
