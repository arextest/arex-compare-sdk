package com.arextest.diff.handler.keycompute;

import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class ListKeyProcess {

    private List<NodeEntity> currentNodePath = new ArrayList<>();

    private List<LogEntity> logs = new ArrayList<>();

    private List<ReferenceEntity> responseReferences;

    private List<ListSortEntity> allListKeys;

    private LinkedList<ListSortEntity> prioritylistSortEntities;

    // <referenceListPath,refNum,keyValue>
    private HashMap<String, HashMap<String, String>> referenceKeys = new HashMap<>();

    // <list path, <index,keyValue>>
    private HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeys = new HashMap<>();

    private HashMap<String, List<String>> listKeysMap;

    private List<String> currentParentPath;

    boolean useFirstElementKey = false;

    public ListKeyProcess(List<ReferenceEntity> responseReferences, List<ListSortEntity> allListKeys) {
        this.responseReferences = responseReferences;
        this.allListKeys = allListKeys;
        this.listKeysMap = getListKeysMap();
        this.prioritylistSortEntities = computeReferencedListPriority();
    }

    public List<LogEntity> getLogs() {
        return logs;
    }

    public HashMap<List<NodeEntity>, HashMap<Integer, String>> getListIndexKeys() {
        return listIndexKeys;
    }

    // probably dead loop
    private LinkedList<ListSortEntity> computeReferencedListPriority() {
        List<String> fkPaths = new ArrayList<>();
        Set<String> pkPaths = new LinkedHashSet<>();

        HashMap<String, List<String>> relations = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        for (ReferenceEntity re : responseReferences) {
            String fkPath = ListUti.convertToString2(re.getFkNodePath());
            String pkPath = ListUti.convertToString2(re.getPkNodeListPath());
            fkPaths.add(fkPath);
            pkPaths.add(pkPath);
            if (relations.containsKey(fkPath)) {
                relations.get(fkPath).add(pkPath);
            } else {
                List<String> list = new ArrayList<>();
                list.add(pkPath);
                relations.put(fkPath, list);
            }
        }

        while (pkPaths.size() > queue.size()) {
            for (String s : pkPaths) {
                if (queue.contains(s)) continue;
                List<String> list = findMatchPath(fkPaths, s);
                if (list.size() == 0) {
                    queue.add(s);
                } else {
                    boolean flag = true;
                    for (int i = 0; i < list.size(); i++) {
                        String refNode = list.get(i);
                        List<String> refPkNodes = relations.get(refNode);

                        for (String refPkNode : refPkNodes) {
                            if (!queue.contains(refPkNode)) {
                                flag = false;
                                break;
                            }
                        }
                        if (!flag) {
                            break;
                        }
                    }
                    if (flag) {
                        queue.add(s);
                    }
                }
            }
        }
        LinkedList<ListSortEntity> listSortEntityQueue = new LinkedList<>();
        String path;
        while ((path = queue.poll()) != null) {
            for (int i = 0; i < allListKeys.size(); i++) {
                ListSortEntity entity = allListKeys.get(i);
                if (ListUti.convertToString2(entity.getListNodepath()).equals(path)) {
                    listSortEntityQueue.add(entity);
                    break;
                }
            }
        }

        return listSortEntityQueue;
    }

    private List<String> findMatchPath(List<String> fkPaths, String s) {
        List<String> keyPaths = listKeysMap.get(s);
        //pkList keys match in fkNode path
        List<String> matchPath = new ArrayList<>();
        if (keyPaths == null) {
            return matchPath;
        }
        for (int i = 0; i < keyPaths.size(); i++) {
            for (int j = 0; j < fkPaths.size(); j++) {
                String str = fkPaths.get(j);
                if (str.equals(keyPaths.get(i))) matchPath.add(str);
            }
        }

        return matchPath;
    }

    private HashMap<String, List<String>> getListKeysMap() {
        HashMap<String, List<String>> map = new HashMap<>();
        for (ListSortEntity listSortEntity : allListKeys) {
            String listPath = ListUti.convertToString2(listSortEntity.getListNodepath());
            List<String> keyPaths = new ArrayList<>();
            for (int i = 0; i < listSortEntity.getKeys().size(); i++) {
                String listKeyPath = ListUti.convertToString2(mergePath(listSortEntity.getListNodepath(), listSortEntity.getKeys().get(i)));
                keyPaths.add(listKeyPath);
            }
            map.put(listPath, keyPaths);
        }

        return map;
    }

    // add node name
    private String getKeyValueByPath(List<String> relativePath, Object obj) throws JSONException {
        String result = null;
        if (obj instanceof JSONObject) {
            String path = relativePath.get(0);

            if (path.contains("[first]")) {
                useFirstElementKey = true;
                path = path.substring(0, path.indexOf("[first]"));
            } else if (path.contains("[combination]")) {
                useFirstElementKey = false;
                path = path.substring(0, path.indexOf("[combination]"));
            } else {
                useFirstElementKey = false;
            }

            currentParentPath.add(path);
            Object subObj = null;
            if (path.equals(Constant.DYNAMIC_PATH)) {
                String[] names = JSONObject.getNames((JSONObject) obj);
                if (names.length > 0) {
                    List<String> rList = new ArrayList<>(names.length);
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");
                    for (String name : names) {
                        subObj = ((JSONObject) obj).get(name);
                        if (subObj != null) {
                            ListUti.removeLast(currentParentPath);
                            currentParentPath.add(name);
                            result = getKeyValueByPath(relativePath.subList(1, relativePath.size()), subObj);
                            if (result != null) {
                                rList.add(result);
                            }
                        }
                    }
                    for (String r : rList) {
                        sb.append(r);
                    }
                    sb.append("}");
                    result = sb.toString();
                }
            } else {
                try {
                    subObj = ((JSONObject) obj).get(path);
                } catch (JSONException e) {
                }
                if (subObj != null) {
                    result = getKeyValueByPath(relativePath.subList(1, relativePath.size()), subObj);
                }
            }
            currentParentPath.remove(currentParentPath.size() - 1);

        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;

            if (jsonArray.length() > 0) {
                List<String> list = new ArrayList<>(jsonArray.length());
                StringBuilder sb = new StringBuilder();
                sb.append("[");
                if (useFirstElementKey) {
                    currentParentPath.add(null);
                    String elementKey = getKeyValueByPath(relativePath, jsonArray.get(0));
                    if (elementKey != null) {
                        list.add(elementKey);
                    }
                    currentParentPath.remove(currentParentPath.size() - 1);
                    useFirstElementKey = false;
                } else {//combination default
                    for (int i = 0; i < jsonArray.length(); i++) {
                        currentParentPath.add(null);
                        String elementKey = getKeyValueByPath(relativePath, jsonArray.get(i));
                        if (elementKey != null) {
                            list.add(elementKey);
                        }
                        currentParentPath.remove(currentParentPath.size() - 1);
                    }
                }

                if (list.size() > 1) {
                    Collections.sort(list);
                }
                for (String s : list) {
                    sb.append(s);
                }
                sb.append("]");
                result = sb.toString();
            }

        } else if (obj != null && !JSONObject.NULL.equals(obj) && !"".equals(obj.toString())) {
            // To solve the problem the relativePath exist value caused by the dynamic path, obj is the basic type (excluding %value%)
            if (relativePath.size() > 1 || (relativePath.size() == 1 && !relativePath.get(0).equals("%value%"))) {
                return null;
            }
            String value;
            List<String> referencePaths = new ArrayList<>();
            for (List<String> list : getReferencePath(currentParentPath)) {
                referencePaths.add(ListUti.convertToString2(list));
            }

            value = String.valueOf(obj);
            if (value.matches("\\d+\\.0+")) {
                value = value.substring(0, value.lastIndexOf('.'));
            }

            if (referencePaths.size() > 0 && !value.equals("0")) {
                int cnt = 0;
                for (String referencePath : referencePaths) {
                    String refKey = null;
                    if (referenceKeys.containsKey(referencePath)) {
                        refKey = referenceKeys.get(referencePath).get(value);
                    }
                    if (refKey != null) {
                        cnt++;
                        value = refKey;
                    }
                }

                if (cnt == 0) {
                    LogEntity log = new LogEntity("The referenced node could not be found or the referenced List does not have listKey, fkNodePath: "
                            + ListUti.convertToString2(currentParentPath) + ", fkNodeValue: " + value);
                    logs.add(log);
                }
                if (cnt > 1) {
                    LogEntity log = new LogEntity("More than one referenced node, fkNodePath: " + ListUti.convertToString2(currentParentPath) + ", fkNodeValue: " + value);
                    logs.add(log);
                }
            }

            String nodeName = currentParentPath.get(currentParentPath.size() - 1);
            StringBuilder sb = new StringBuilder();
            if (nodeName != null) {
                sb.append("(").append(nodeName).append(":").append(value).append(")");
            } else {
                sb.append("(").append(value).append(")");
            }
            result = sb.toString();
        }
        return result;
    }

    public void computeAllListKey(Object obj) throws JSONException {
        // priority list keys
        ListSortEntity listSortEntity;

        try {
            while ((listSortEntity = prioritylistSortEntities.poll()) != null) {
                List<String> listNodePath = listSortEntity.getListNodepath();
                Object object = getObject(obj, listNodePath);
                if (object == null || JSONObject.NULL.equals(object)) {
                    continue;
                }
                JSONArray listObj = ((JSONArray) object);

                HashMap<String, String> referenceKeyValue = new HashMap<>();
                referenceKeys.put(ListUti.convertToString2(mergePath(listNodePath, listSortEntity.getReferenceNodeRelativePath())), referenceKeyValue);

                for (int i = 0; i < listObj.length(); i++) {
                    StringBuilder fullKey = new StringBuilder();
                    Object listElement = listObj.get(i);
                    String refValue = getObject(listElement, listSortEntity.getReferenceNodeRelativePath()).toString();

                    if (listSortEntity.getKeys() == null || listSortEntity.getKeys().size() == 0) {
                        throw new RuntimeException("ref list node don't have listkey!");
                    }
                    for (List<String> path : listSortEntity.getKeys()) {
                        currentParentPath = ListUti.deepCopy(listSortEntity.getListNodepath());
                        currentParentPath.add(null);

                        String currentKey = getKeyValueByPath(path, listElement);
                        if (currentKey != null) {
                            fullKey.append(currentKey);
                        }
                    }
                    //if refValue repeat, log
                    referenceKeyValue.put(refValue, fullKey.toString());
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // other normal list keys,traverse the whole tree
        // currentNodePath.add(new NodeEntity("root", 0));

        computeNormalListKey(obj);

    }

    public void computeNormalListKey(Object obj) throws JSONException {

        if (obj instanceof JSONObject) {
            JSONObject jsonObj = (JSONObject) obj;
            String[] names = JSONObject.getNames(jsonObj);
            if (names != null) {
                for (String fieldName : names) {
                    currentNodePath.add(new NodeEntity(fieldName, 0));
                    Object objFieldValue = jsonObj.get(fieldName);
                    computeNormalListKey(objFieldValue);
                    currentNodePath.remove(currentNodePath.size() - 1);
                }
            }

        } else if (obj instanceof JSONArray) {
            JSONArray objArray = (JSONArray) obj;

            ListSortEntity listSortEntity = findListKeys(currentNodePath);
            if (listSortEntity != null) {

                HashMap<Integer, String> indexKeys = new HashMap<>();

                for (int i = 0; i < objArray.length(); i++) {

                    StringBuilder fullKey = new StringBuilder();
                    currentNodePath.add(new NodeEntity(null, i));

                    Object listElement = objArray.get(i);

                    for (List<String> path : listSortEntity.getKeys()) {
                        currentParentPath = ListUti.deepCopy(listSortEntity.getListNodepath());
                        currentParentPath.add(null);

                        String currentKey = getKeyValueByPath(path, listElement);
                        if (currentKey != null) {
                            fullKey.append(currentKey);
                        }
                    }

                    indexKeys.put(i, fullKey.toString());
                    computeNormalListKey(listElement);

                    currentNodePath.remove(currentNodePath.size() - 1);
                }

                if (indexKeys.size() > 0) {
                    listIndexKeys.put(new ArrayList<>(currentNodePath), indexKeys);
                }

            } else {
                for (int i = 0; i < objArray.length(); i++) {
                    currentNodePath.add(new NodeEntity(null, i));
                    Object listElement = objArray.get(i);
                    computeNormalListKey(listElement);
                    currentNodePath.remove(currentNodePath.size() - 1);
                }
            }

        }
    }

    private Object getObject(Object obj, List<String> listNodePath) throws JSONException {
        for (int i = 0; i < listNodePath.size(); i++) {
            if (JSONObject.NULL.equals(obj)) {
                return null;
            }
            try {
                obj = ((JSONObject) obj).get(listNodePath.get(i));
            } catch (Exception e) {
                return null;
            }
        }
        return obj;
    }

    private List<String> mergePath(List<String> path1, List<String> path2) {
        List<String> newPath = new ArrayList<>();
        newPath.addAll(path1);
        newPath.addAll(path2);
        return newPath;
    }

    private List<List<String>> getReferencePath(List<String> currentPath) {
        List<List<String>> refPkPaths = new ArrayList<>();

        List<String> paths = new ArrayList<>();
        for (int i = 0; i < currentPath.size(); i++) {
            if (currentPath.get(i) != null) {
                paths.add(currentPath.get(i));
            }
        }

        List<String> list;
        for (int i = 0; i < responseReferences.size(); i++) {
            List<String> fkNodePath = responseReferences.get(i).getFkNodePath();
            if ("%value%".equals(fkNodePath.get(fkNodePath.size() - 1))) {
                fkNodePath = fkNodePath.subList(0, fkNodePath.size() - 1);
            }
            // support dynamic path
            if (ListUti.stringListEqualsOnWildcard(paths, fkNodePath)) {
                list = responseReferences.get(i).getPkNodePath();
                refPkPaths.add(list);
            }
        }
        return refPkPaths;
    }

    private ListSortEntity findListKeys(List<NodeEntity> path) {
        List<String> pathWithoutIndex = new ArrayList<>();
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getNodeName() != null) pathWithoutIndex.add(path.get(i).getNodeName());
        }
        if (pathWithoutIndex.isEmpty()) {
            pathWithoutIndex.add("");
        }
        for (ListSortEntity listSortEntity : allListKeys) {
            // support dynamic path
            if (ListUti.stringListEqualsOnWildcard(listSortEntity.getListNodepath(), pathWithoutIndex)) {
                return listSortEntity;
            }
        }
        return null;
    }

}
