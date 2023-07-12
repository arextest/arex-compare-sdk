package com.arextest.diff.compare.feature;

import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.log.NodeEntity;

import java.util.List;
import java.util.Map;

public class IndexSelectorFactory {

    public static IndexSelector getIndexSelector(List<NodeEntity> currentNodeLeft, List<NodeEntity> currentNodeRight, CompareContext compareContext) {
        Map<Integer, String> indexKeysLeft = compareContext.listIndexKeysLeft.get(currentNodeLeft);
        Map<Integer, String> indexKeysRight = compareContext.listIndexKeysRight.get(currentNodeRight);

        if (indexKeysLeft == null && indexKeysRight == null) {
            return new GeneralIndexSelector();
        } else {
            return new ListKeyIndexSelector(indexKeysLeft, indexKeysRight, compareContext);
        }
    }
}
