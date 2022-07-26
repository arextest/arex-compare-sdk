package com.arextest.diff.compare;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import com.arextest.diff.utils.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.arextest.diff.compare.CompareHelper.getUnmatchedPair;

/**
 * Created by rchen9 on 2022/7/25.
 */
public class ObjectCompare {
    public static void objectCompare(Object obj1, Object obj2, CompareContext compareContext) throws JSONException {

        List<NodeEntity> currentNodeLeft = compareContext.getCurrentNodeLeft();
        List<NodeEntity> currentNodeRight = compareContext.getCurrentNodeRight();
        List<String> currentListKeysLeft = compareContext.getCurrentListKeysLeft();
        List<String> currentListKeysRight = compareContext.getCurrentListKeysRight();
        boolean notDistinguishNullAndEmpty = compareContext.isNotDistinguishNullAndEmpty();
        List<LogEntity> logs = compareContext.getLogs();

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
            currentNodeLeft.add(new NodeEntity(fieldName, 0));

            Object obj1FieldValue, obj2FieldValue = null;
            obj1FieldValue = jsonObj1.get(fieldName);

            boolean rightExist = false;
            try {
                obj2FieldValue = jsonObj2.get(fieldName);
                rightExist = true;
                usedFieldNames.add(fieldName);
                currentNodeRight.add(new NodeEntity(fieldName, 0));
            } catch (JSONException e) {
                if (!notDistinguishNullAndEmpty || !StringUtil.jsonEmptyJudge(obj1FieldValue)) {
                    LogEntity log = new LogEntity(obj1FieldValue, obj2FieldValue,
                            getUnmatchedPair(UnmatchedType.RIGHT_MISSING, compareContext).setListKeys(currentListKeysLeft));
                    logs.add(log);
                }
            }

            if (obj1FieldValue != null && obj2FieldValue != null) {
                GenericCompare.jsonCompare(obj1FieldValue, obj2FieldValue, compareContext);
            }

            ListUti.removeLast(currentNodeLeft);
            if (rightExist) {
                ListUti.removeLast(currentNodeRight);
            }
        }
        for (String fieldName : names2) {
            if (usedFieldNames.contains(fieldName)) {
                continue;
            }
            currentNodeRight.add(new NodeEntity(fieldName, 0));

            Object obj2FieldValue, obj1FieldValue = null;
            obj2FieldValue = jsonObj2.get(fieldName);

            boolean leftExist = false;
            try {
                obj1FieldValue = jsonObj1.get(fieldName);
                leftExist = true;
                currentNodeLeft.add(new NodeEntity(fieldName, 0));
            } catch (JSONException e) {
                if (!notDistinguishNullAndEmpty || !StringUtil.jsonEmptyJudge(obj2FieldValue)) {
                    LogEntity log = new LogEntity(obj1FieldValue, obj2FieldValue, getUnmatchedPair(UnmatchedType.LEFT_MISSING, compareContext)
                            .setListKeys(currentListKeysRight));
                    logs.add(log);
                }
            }
            if (obj1FieldValue != null && obj2FieldValue != null) {
                GenericCompare.jsonCompare(obj1FieldValue, obj2FieldValue, compareContext);
            }
            if (leftExist) {
                ListUti.removeLast(currentNodeLeft);
            }
            ListUti.removeLast(currentNodeRight);
        }
    }
}
