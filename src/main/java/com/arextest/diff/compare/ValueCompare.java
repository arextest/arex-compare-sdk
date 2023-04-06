package com.arextest.diff.compare;

import com.arextest.diff.compare.feature.ReferenceFeature;
import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.register.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.key.ReferenceEntity;

import java.util.List;

public class ValueCompare {

    public static void valueCompare(Object obj1, Object obj2, CompareContext compareContext) {

        List<ReferenceEntity> references = CompareHelper.findReferenceNode(compareContext.currentNodeLeft, compareContext.responseReferences);
        if (!references.isEmpty()) {
            ReferenceFeature.referenceHandler(references, obj1, obj2, compareContext);
        } else if (!obj1.equals(obj2)) {
            LogRegister.register(obj1, obj2, LogMarker.VALUE_DIFF, compareContext);
        }
    }

}
