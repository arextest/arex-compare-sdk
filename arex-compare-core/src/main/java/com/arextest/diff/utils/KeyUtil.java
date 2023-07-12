package com.arextest.diff.utils;


import com.arextest.diff.model.key.IndexKey;
import com.arextest.diff.model.log.NodeEntity;

import java.util.List;

public class KeyUtil {

    public static final String CURRENT_PARSE_VAL = "current";

    public static final String BEFORE_PARSE_VAL = "before";

    public static IndexKey getIndexKey(IndexKey listIndexKeys, List<NodeEntity> currentNodeLeft) {

        if (listIndexKeys.getListChild().isEmpty() && listIndexKeys.getChild().isEmpty()) {
            return new IndexKey();
        }

        IndexKey indexKey = listIndexKeys;
        for (NodeEntity nodeEntity : currentNodeLeft) {

            if (nodeEntity.getNodeName() != null) {
                indexKey = indexKey.getChild().get(nodeEntity);
            } else {
                try {
                    indexKey = indexKey.getListChild().get(nodeEntity.getIndex());
                } catch (Exception e) {
                    return new IndexKey();
                }
            }
            if (indexKey == null) {
                return new IndexKey();
            }
        }
        return indexKey;
    }


}
