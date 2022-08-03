package com.arextest.diff.handler.parse;

import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.service.DecompressService;
import com.arextest.diff.utils.DeCompressUtil;
import com.arextest.diff.utils.ListUti;
import com.arextest.diff.utils.StringUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class StringAndCompressParse {

    private List<NodeEntity> currentNode = new ArrayList<>();

    private Map<List<NodeEntity>, String> original = new HashMap<>();

    private boolean nameToLower;

    private Map<String, DecompressService> decompressServices;

    private Map<String, String> decompressConfig;

    public Map<List<NodeEntity>, String> getOriginal() {
        return original;
    }

    public void setNameToLower(boolean nameToLower) {
        this.nameToLower = nameToLower;
    }

    public void setDecompressServices(Map<String, DecompressService> decompressServices) {
        this.decompressServices = decompressServices;
    }

    public void setDecompressConfig(Map<String, String> decompressConfig) {
        this.decompressConfig = decompressConfig;
    }

    public void getJSONParse(Object obj, Object preObj) throws JSONException {
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
                currentNode.add(new NodeEntity(fieldName, 0));
                Object objFieldValue = jsonObject.get(fieldName);
                getJSONParse(objFieldValue, obj);
                ListUti.removeLast(currentNode);
            }
        } else if (obj instanceof JSONArray) {
            JSONArray objArray = (JSONArray) obj;
            for (int i = 0; i < objArray.length(); i++) {
                currentNode.add(new NodeEntity(null, i));
                Object element = objArray.get(i);
                getJSONParse(element, obj);
                ListUti.removeLast(currentNode);
            }

        } else {

            String value = obj.toString();
            String nodePath = nameToLower ? ListUti.convertToString2(currentNode).toLowerCase() : ListUti.convertToString2(currentNode);

            MutablePair<Object, Boolean> objectBooleanPair = null;
            if (decompressConfig != null && decompressConfig.containsKey(nodePath)) {
                String beanPath = decompressConfig.get(nodePath);
                objectBooleanPair = processCompress(value, beanPath, preObj);
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
            if (preObj instanceof JSONObject) {
                ((JSONObject) preObj).put(currentName, objectBooleanPair.getKey());
                original.put(new ArrayList<>(currentNode), value);
            } else if (preObj instanceof JSONArray) {
                ((JSONArray) preObj).put(Integer.valueOf(currentName), objectBooleanPair.getKey());
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
     * @param beanPath
     * @param preObj
     * @return Pair<Object, Boolean>, the same define processStringParse()
     * k: show weather successfully process，if return null:fail,if return not null:success
     * v: show Object instanceof JSONObject or JSONArray which need to further performance getJSONParse
     */
    private MutablePair<Object, Boolean> processCompress(String value, String beanPath, Object preObj) {
        String unCompressStr;
        try {
            unCompressStr = (String) DeCompressUtil.deCompressPlugin(decompressServices,
                    beanPath, value);
        } catch (Throwable e) {
            return new MutablePair<>(null, Boolean.FALSE);
        }

        MutablePair<Object, Boolean> objectBooleanPair = processStringParse(unCompressStr, preObj);
        if (objectBooleanPair.getKey() == null) {
            if (StringUtil.isEmpty(unCompressStr)) {
                return new MutablePair<>(null, Boolean.FALSE);
            } else {
                return new MutablePair<>(unCompressStr, Boolean.FALSE);
            }
        } else {
            return objectBooleanPair;
        }
    }


    private MutablePair<Object, Boolean> processStringParse(String value, Object preObj) {

        Object objTemp = null;

        if (StringUtil.isEmpty(value)) {
            return new MutablePair<>(null, Boolean.FALSE);
        }

        if (value.startsWith("{") && value.endsWith("}")) {
            try {
                objTemp = new JSONObject(value);
            } catch (JSONException e) {
            }

        } else if (value.startsWith("[") && value.endsWith("]")) {
            try {
                objTemp = new JSONArray(value);
            } catch (JSONException e) {
            }
        }
        return objTemp == null ? new MutablePair<>(null, Boolean.FALSE) : new MutablePair<>(objTemp, Boolean.TRUE);
    }

}
