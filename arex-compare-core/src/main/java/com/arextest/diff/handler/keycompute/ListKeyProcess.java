package com.arextest.diff.handler.keycompute;

import com.arextest.diff.model.enumeration.Constant;
import com.arextest.diff.model.exception.ListKeyCycleException;
import com.arextest.diff.model.key.ListSortEntity;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ListKeyProcess {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListKeyProcess.class);
    private List<NodeEntity> currentNodePath = new ArrayList<>();

    private List<LogEntity> logs = new ArrayList<>();

    private List<ReferenceEntity> responseReferences;

    private List<ListSortEntity> allListKeys;

    // <referenceListPath,refNum,keyValue>
    private HashMap<String, HashMap<String, String>> referenceKeys = new HashMap<>();

    // <list path, <index,keyValue>>
    private HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeys = new HashMap<>();

    private List<String> currentParentPath;

    boolean useFirstElementKey = false;

    public ListKeyProcess(List<ReferenceEntity> responseReferences, List<ListSortEntity> allListKeys) {
        this.responseReferences = responseReferences;
        this.allListKeys = allListKeys;
    }

    public List<LogEntity> getLogs() {
        return logs;
    }

    public HashMap<List<NodeEntity>, HashMap<Integer, String>> getListIndexKeys() {
        return listIndexKeys;
    }

    // probably dead loop
    private LinkedList<ListSortEntity> computeReferencedListPriority(List<ReferenceEntity> responseReferences,
                                                                     List<ListSortEntity> allListKeys) throws ListKeyCycleException {
        if (responseReferences == null || responseReferences.isEmpty()) {
            return new LinkedList<>();
        }

        Map<String, List<String>> listKeysMap = this.getListKeysMap(allListKeys);

        // the collection of fkPaths
        Set<String> fkPaths = new HashSet<>();
        // the collection of pkNodeListPaths
        Set<String> pkListPaths = new HashSet<>();

        // fkPath -> the collection of pkNodeListPaths
        Map<String, Set<String>> relations = new HashMap<>();
        for (ReferenceEntity re : responseReferences) {
            String fkPath = ListUti.convertToString2(re.getFkNodePath());
            String pkListPath = ListUti.convertToString2(re.getPkNodeListPath());
            fkPaths.add(fkPath);
            pkListPaths.add(pkListPath);
            relations.computeIfAbsent(fkPath, k -> new HashSet<>()).add(pkListPath);
        }

        // all traversed node collections
        Set<String> traversedSet = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();

        this.doPriorityReferences(queue, pkListPaths, new HashSet<>(), traversedSet, relations, fkPaths, listKeysMap);

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

    /**
     * find all fkPaths in pkNodeListPath
     *
     * @param fkPaths        the collection of fkPaths
     * @param pkNodeListPath pkNodeListPath
     * @return
     */
    private Set<String> findFkPathInListKey(Set<String> fkPaths, String pkNodeListPath, Map<String, List<String>> listKeysMap) {
        List<String> keyPaths = listKeysMap.get(pkNodeListPath);
        if (keyPaths == null || keyPaths.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> matchPath = new HashSet<>(keyPaths.size());
        for (String keyPath : keyPaths) {
            if (fkPaths.contains(keyPath)) {
                matchPath.add(keyPath);
            }
        }
        return matchPath;
    }

    private Map<String, List<String>> getListKeysMap(List<ListSortEntity> allListKeys) {
        if (allListKeys == null || allListKeys.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> listKeysMap = new HashMap<>();
        for (ListSortEntity listSortEntity : allListKeys) {
            String listPath = ListUti.convertToString2(listSortEntity.getListNodepath());
            List<String> keyPaths = new ArrayList<>();
            for (int i = 0; i < listSortEntity.getKeys().size(); i++) {
                String listKeyPath = ListUti.convertToString2(
                        mergePath(listSortEntity.getListNodepath(), listSortEntity.getKeys().get(i)));
                keyPaths.add(listKeyPath);
            }
            listKeysMap.put(listPath, keyPaths);
        }
        return listKeysMap;
    }

    private void doPriorityReferences(LinkedList<String> queue, Set<String> refLinkNodes, Set<String> singleLinkAllNodeSet,
                                      Set<String> traversedSet, Map<String, Set<String>> relations, Set<String> fkPaths,
                                      Map<String, List<String>> listKeysMap) throws ListKeyCycleException {
        if (refLinkNodes == null || refLinkNodes.isEmpty()) {
            return;
        }

        for (String refLinkNode : refLinkNodes) {
            if (singleLinkAllNodeSet.contains(refLinkNode)) {
                throw new ListKeyCycleException(String.format("an infinite loop occurs, path: %s", singleLinkAllNodeSet));
            }

            if (traversedSet.contains(refLinkNode)) {
                continue;
            }

            queue.addLast(refLinkNode);
            singleLinkAllNodeSet.add(refLinkNode);
            traversedSet.add(refLinkNode);

            Set<String> refFkNodePaths = findFkPathInListKey(fkPaths, refLinkNode, listKeysMap);
            if (refFkNodePaths.isEmpty()) {
                continue;
            }

            for (String refFkNode : refFkNodePaths) {
                Set<String> refPkListPaths = relations.get(refFkNode);
                this.doPriorityReferences(queue, refPkListPaths, new HashSet<>(singleLinkAllNodeSet), traversedSet,
                        relations, fkPaths, listKeysMap);
            }
        }
    }

    // add node name
    private String getKeyValueByPath(List<String> relativePath, Object obj) {
        String result = null;
        if (relativePath == null || relativePath.isEmpty() || (relativePath.size() == 1 && relativePath.get(0).equals("%value%"))) {
            if (obj != null && !(obj instanceof NullNode) && !"".equals(((JsonNode) obj).asText())) {

                String value;
                List<String> referencePaths = new ArrayList<>();
                for (List<String> list : getReferencePath(currentParentPath)) {
                    referencePaths.add(ListUti.convertToString2(list));
                }

                value = ((JsonNode) obj).asText();
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

        if (obj instanceof ObjectNode) {
            String path = relativePath.get(0);
            ObjectNode jsonObj = (ObjectNode) obj;

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
                List<String> names = JacksonHelperUtil.getNames(jsonObj);
                int size = names.size();
                if (size > 0) {
                    List<String> rList = new ArrayList<>(size);
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");
                    for (String name : names) {
                        subObj = jsonObj.get(name);
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
                subObj = jsonObj.get(path);
                if (subObj != null) {
                    result = getKeyValueByPath(relativePath.subList(1, relativePath.size()), subObj);
                }
            }
            currentParentPath.remove(currentParentPath.size() - 1);

        } else if (obj instanceof ArrayNode) {
            ArrayNode jsonArray = (ArrayNode) obj;
            int len = jsonArray.size();

            if (len > 0) {
                List<String> list = new ArrayList<>(len);
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
                    for (int i = 0; i < len; i++) {
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
        }
        return result;
    }

    public void computeAllListKey(Object obj) throws ListKeyCycleException {

        LinkedList<ListSortEntity> priorityListSortEntities = this.computeReferencedListPriority(
                responseReferences, allListKeys);

        // priority list keys
        ListSortEntity listSortEntity;

        try {
            while ((listSortEntity = priorityListSortEntities.poll()) != null) {
                List<String> listNodePath = listSortEntity.getListNodepath();
                Object object = getObject(obj, listNodePath);
                if (object == null || obj instanceof NullNode) {
                    continue;
                }
                ArrayNode listObj = (ArrayNode) object;

                HashMap<String, String> referenceKeyValue = new HashMap<>();
                referenceKeys.put(ListUti.convertToString2(mergePath(listNodePath, listSortEntity.getReferenceNodeRelativePath())), referenceKeyValue);

                for (int i = 0; i < listObj.size(); i++) {
                    StringBuilder fullKey = new StringBuilder();
                    Object listElement = listObj.get(i);
                    String refValue = getObject(listElement, listSortEntity.getReferenceNodeRelativePath()).toString();

                    if (listSortEntity.getKeys() == null || listSortEntity.getKeys().isEmpty()) {
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
            LOGGER.error("computePriorityListSort error", throwable);
        }

        // other normal list keys,traverse the whole tree
        // currentNodePath.add(new NodeEntity("root", 0));

        computeNormalListKey(obj);

    }

    public void computeNormalListKey(Object obj) {

        if (obj instanceof ObjectNode) {
            ObjectNode jsonObj = (ObjectNode) obj;
            List<String> names = JacksonHelperUtil.getNames(jsonObj);
            for (String fieldName : names) {
                currentNodePath.add(new NodeEntity(fieldName, 0));
                Object objFieldValue = jsonObj.get(fieldName);
                computeNormalListKey(objFieldValue);
                currentNodePath.remove(currentNodePath.size() - 1);
            }

        } else if (obj instanceof ArrayNode) {
            ArrayNode objArray = (ArrayNode) obj;

            ListSortEntity listSortEntity = findListKeys(currentNodePath);
            if (listSortEntity != null) {

                HashMap<Integer, String> indexKeys = new HashMap<>();
                for (int i = 0; i < objArray.size(); i++) {

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
                for (int i = 0; i < objArray.size(); i++) {
                    currentNodePath.add(new NodeEntity(null, i));
                    Object listElement = objArray.get(i);
                    computeNormalListKey(listElement);
                    currentNodePath.remove(currentNodePath.size() - 1);
                }
            }

        }
    }

    private Object getObject(Object obj, List<String> listNodePath) {
        for (int i = 0; i < listNodePath.size(); i++) {
            if (obj == null || obj instanceof NullNode) {
                return null;
            }
            obj = ((ObjectNode) obj).get(listNodePath.get(i));
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
