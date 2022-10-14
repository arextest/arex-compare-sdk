package com.arextest.diff.compare;

import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ObjectCompare {
    public static void objectCompare(Object obj1, Object obj2, CompareContext compareContext) throws JSONException {

        JSONObject jsonObj1 = (JSONObject) obj1;
        JSONObject jsonObj2 = (JSONObject) obj2;

        String[] names1 = JSONObject.getNames(jsonObj1);
        String[] names2 = JSONObject.getNames(jsonObj2);
        if (names1 == null) {
            names1 = new String[0];
        }
        if (names2 == null) {
            names2 = new String[0];
        }

        List<String> usedFieldNames = new ArrayList<>();
        for (String fieldName : names1) {
            compareContext.currentNodeLeft.add(new NodeEntity(fieldName, 0));

            Object obj1FieldValue, obj2FieldValue = null;
            obj1FieldValue = jsonObj1.get(fieldName);

            boolean rightExist = false;
            try {
                obj2FieldValue = jsonObj2.get(fieldName);
                rightExist = true;
                usedFieldNames.add(fieldName);
                compareContext.currentNodeRight.add(new NodeEntity(fieldName, 0));
            } catch (JSONException e) {
            }

            GenericCompare.jsonCompare(obj1FieldValue, obj2FieldValue, compareContext);

            ListUti.removeLast(compareContext.currentNodeLeft);
            if (rightExist) {
                ListUti.removeLast(compareContext.currentNodeRight);
            }
        }
        for (String fieldName : names2) {
            if (usedFieldNames.contains(fieldName)) {
                continue;
            }
            compareContext.currentNodeRight.add(new NodeEntity(fieldName, 0));

            Object obj2FieldValue, obj1FieldValue = null;
            obj2FieldValue = jsonObj2.get(fieldName);

            boolean leftExist = false;
            try {
                obj1FieldValue = jsonObj1.get(fieldName);
                leftExist = true;
                compareContext.currentNodeLeft.add(new NodeEntity(fieldName, 0));
            } catch (JSONException e) {
            }

            GenericCompare.jsonCompare(obj1FieldValue, obj2FieldValue, compareContext);

            if (leftExist) {
                ListUti.removeLast(compareContext.currentNodeLeft);
            }
            ListUti.removeLast(compareContext.currentNodeRight);
        }
    }
}
