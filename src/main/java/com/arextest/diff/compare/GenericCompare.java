package com.arextest.diff.compare;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.LogRegister;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.IgnoreUtil;
import com.arextest.diff.utils.ListUti;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class GenericCompare {

    public static void jsonCompare(Object obj1, Object obj2, CompareContext compareContext) throws JSONException {

        List<NodeEntity> currentNode = compareContext.currentNodeLeft.size() >= compareContext.currentNodeRight.size()
                ? compareContext.getCurrentNodeLeft() : compareContext.getCurrentNodeRight();
        List<String> fuzzyPath = ListUti.convertToStringList(currentNode);

        // ignore primary key node
        if (compareContext.pkNodePaths != null) {
            for (List<String> pkNodePath : compareContext.pkNodePaths) {
                if (pkNodePath.equals(fuzzyPath)) {
                    return;
                }
            }
        }

        // not compare by exclusions
        if (IgnoreUtil.ignoreProcessor(fuzzyPath, compareContext.exclusions)) {
            return;
        }


        if ((obj1 == null && obj2 != null) ||
                (obj1 != null && obj2 == null)) {
            LogRegister.register(obj1, obj2,
                    obj1 == null ? LogMarker.LEFT_OBJECT_MISSING : LogMarker.RIGHT_OBJECT_MISSING, compareContext);
            return;
        }

        // There is a null value in any of the left and right nodes
        if ((JSONObject.NULL.equals(obj1) && !JSONObject.NULL.equals(obj2)) ||
                (!JSONObject.NULL.equals(obj1) && JSONObject.NULL.equals(obj2))) {
            if (compareContext.notDistinguishNullAndEmpty) {
                if (bothEmptyString(obj1, obj2)) return;
                if (obj1 instanceof JSONArray && ((JSONArray) obj1).length() == 0) return;
                if (obj2 instanceof JSONArray && ((JSONArray) obj2).length() == 0) return;
            }

            LogRegister.register(obj1, obj2, LogMarker.NULL_CHECK, compareContext);
            return;
        }

        // obj1 and obj2 are different types
        if (!obj1.getClass().equals(obj2.getClass())) {
            LogRegister.register(obj1, obj2, LogMarker.TYPE_DIFF, compareContext);
            return;
        }

        if (obj1 instanceof JSONObject) {
            ObjectCompare.objectCompare(obj1, obj2, compareContext);
        } else if (obj1 instanceof JSONArray) {
            ArrayCompare.arrayCompare(obj1, obj2, compareContext);
        } else {
            ValueCompare.valueCompare(obj1, obj2, compareContext);
        }
    }

    private static boolean bothEmptyString(Object obj1, Object obj2) {
        if (JSONObject.NULL.equals(obj1) && Objects.equals("", obj2)) {
            return true;
        }
        if (JSONObject.NULL.equals(obj2) && Objects.equals("", obj1)) {
            return true;
        }
        return false;
    }

}
