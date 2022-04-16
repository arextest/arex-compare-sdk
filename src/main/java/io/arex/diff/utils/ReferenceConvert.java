package io.arex.diff.utils;

import io.arex.diff.model.key.ListSortEntity;
import io.arex.diff.model.key.ReferenceEntity;
import io.arex.diff.model.key.ResponseListKeyEntity;
import io.arex.diff.model.key.ResponseNodeReferenceEntity;

import java.util.*;

public class ReferenceConvert {

    public static List<ReferenceEntity> getAllReferenceEntities(List<ResponseNodeReferenceEntity> responseNodeReferenceEntities) {

        if (responseNodeReferenceEntities == null) {
            return new ArrayList<>();
        }

        Set<ReferenceEntity> referenceEntitiesSet = new HashSet<>();
        for (ResponseNodeReferenceEntity responseNodeReferenceEntity : responseNodeReferenceEntities) {


            String fkNodePath = responseNodeReferenceEntity.getFkNode();
            String pkNodePath = responseNodeReferenceEntity.getPkNode();

            ReferenceEntity entity = new ReferenceEntity();
            entity.setFkNodePath(Arrays.asList(fkNodePath.split("\\\\")));
            List<String> pkNode = Arrays.asList(pkNodePath.split("\\\\"));
            entity.setPkNodePath(pkNode);
            // this may be cause problem
            entity.setPkNodeListPath(pkNode.subList(0, pkNode.size() - 1));
            referenceEntitiesSet.add(entity);
        }

        List<ReferenceEntity> referenceEntities = new ArrayList<>();
        for (ReferenceEntity referenceEntity : referenceEntitiesSet) {
            referenceEntities.add(referenceEntity);
        }

        return referenceEntities;
    }

    public static List<ListSortEntity> getKeyConfig(List<ResponseListKeyEntity> keyEntities, List<ReferenceEntity> references) {
        if (keyEntities == null) {
            return new ArrayList<>();
        }

        Map<String, ReferenceEntity> pkNodePath2ListPathMap = new HashMap<>();
        if (references != null) {
            for (ReferenceEntity referenceEntity : references) {
                pkNodePath2ListPathMap.put(ListUti.convertToString2(referenceEntity.getPkNodeListPath()), referenceEntity);
            }
        }

        List<ListSortEntity> keys = new ArrayList<>();
        for (ResponseListKeyEntity keyEntity : keyEntities) {
            if (StringUtil.isEmpty(keyEntity.getKeyNodes())) {
                continue;
            }

            ListSortEntity listSortEntity = new ListSortEntity();
            List<List<String>> keyNodePaths = new ArrayList<>();
            for (String keyNodePath : keyEntity.getKeyNodes().split(",")) {
                keyNodePaths.add(Arrays.asList(keyNodePath.split("\\\\")));
            }

            listSortEntity.setListNodepath(Arrays.asList(keyEntity.getListNodePath().split("\\\\")));
            listSortEntity.setKeys(keyNodePaths);

            ReferenceEntity entity = pkNodePath2ListPathMap.get(ListUti.convertToString2(listSortEntity.getListNodepath()));
            if (entity != null) {
                List<String> pkNodeRelativePath = entity.getPkNodePath().subList(entity.getPkNodeListPath().size(), entity.getPkNodePath().size());
                listSortEntity.setReferenceNodeRelativePath(pkNodeRelativePath);
                // listKeyEntity.setReferenceEntity(entity);
            }
            keys.add(listSortEntity);
        }
        return keys;
    }
}
