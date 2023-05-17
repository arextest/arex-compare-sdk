package com.arextest.diff.handler.parse;

import com.arextest.diff.model.DeCompressConfig;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.DeCompressUtil;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.ListUti;
import com.arextest.diff.utils.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringAndCompressParse {

    private List<NodeEntity> currentNode = new ArrayList<>();

    private Map<List<NodeEntity>, String> original = new HashMap<>();

    private boolean nameToLower;

    private String pluginJarUrl;

    private Map<List<String>, DeCompressConfig> decompressConfig;

    public Map<List<NodeEntity>, String> getOriginal() {
        return original;
    }

    public void setNameToLower(boolean nameToLower) {
        this.nameToLower = nameToLower;
    }

    public void setDecompressConfig(Map<List<String>, DeCompressConfig> decompressConfig) {
        this.decompressConfig = decompressConfig;
    }

    public void setPluginJarUrl(String pluginJarUrl) {
        this.pluginJarUrl = pluginJarUrl;
    }

    public void getJSONParse(Object obj, Object preObj) {
        if (obj == null || obj instanceof NullNode) {
            return;
        }

        if (obj instanceof ObjectNode) {
            ObjectNode jsonObject = (ObjectNode) obj;
            List<String> names = JacksonHelperUtil.getNames(jsonObject);
            for (String fieldName : names) {
                currentNode.add(new NodeEntity(fieldName, 0));
                Object objFieldValue = jsonObject.get(fieldName);
                getJSONParse(objFieldValue, obj);
                ListUti.removeLast(currentNode);
            }
        } else if (obj instanceof ArrayNode) {
            ArrayNode objArray = (ArrayNode) obj;
            for (int i = 0; i < objArray.size(); i++) {
                currentNode.add(new NodeEntity(null, i));
                Object element = objArray.get(i);
                getJSONParse(element, obj);
                ListUti.removeLast(currentNode);
            }

        } else {

            String value = ((JsonNode) obj).asText();
            // TODO: 2022/9/20 improve the method to speed up
            List<String> nodePath = nameToLower
                    ? ListUti.convertToStringList(currentNode).stream().map(String::toLowerCase).collect(Collectors.toList())
                    : ListUti.convertToStringList(currentNode);
            MutablePair<JsonNode, Boolean> objectBooleanPair = null;
            if (decompressConfig != null && decompressConfig.containsKey(nodePath)) {
                DeCompressConfig deCompressConfig = decompressConfig.get(nodePath);
                objectBooleanPair = processCompress(value, this.pluginJarUrl, deCompressConfig, preObj);
            } else {
                objectBooleanPair = processStringParse(value, preObj);
            }

            if (objectBooleanPair.getKey() == null) {
                return;
            }
            if (Objects.equals(objectBooleanPair.getValue(), Boolean.TRUE)) {
                getJSONParse(objectBooleanPair.getKey(), preObj);
            }

            String currentName = getCurrentName(currentNode);
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
     * @param deCompressConfig
     * @param preObj
     * @return Pair<Object, Boolean>, the same define processStringParse()
     * k: show weather successfully process，if return null:fail,if return not null:success
     * v: show Object instanceof JSONObject or JSONArray which need to further performance getJSONParse
     */
    private MutablePair<JsonNode, Boolean> processCompress(String value, String pluginJarUrl,
                                                           DeCompressConfig deCompressConfig, Object preObj) {
        String unCompressStr;
        try {
            unCompressStr = DeCompressUtil.deCompressPlugin(pluginJarUrl, deCompressConfig, value);
        } catch (Throwable e) {
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
        return objTemp == null
                ? new MutablePair<>(null, Boolean.FALSE)
                : new MutablePair<>(objTemp, Boolean.TRUE);
    }

}
