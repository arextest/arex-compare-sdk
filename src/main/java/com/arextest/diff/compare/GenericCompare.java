package com.arextest.diff.compare;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.LogRegister;
import com.arextest.diff.utils.ListUti;
import com.google.common.base.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GenericCompare {

    public static void jsonCompare(Object obj1, Object obj2, CompareContext compareContext) throws JSONException {

        // ignore primary key node
        if (compareContext.pkNodePaths != null) {
            for (List<String> pkNodePath : compareContext.pkNodePaths) {
                if (pkNodePath.equals(ListUti.convertToStringList(compareContext.currentNodeLeft))) {
                    return;
                }
            }
        }

        // There is a null value in any of the left and right nodes
        if (obj1 == null || obj2 == null || JSONObject.NULL.equals(obj1) || JSONObject.NULL.equals(obj2)) {
            if (compareContext.notDistinguishNullAndEmpty) {
                if (bothEmptyString(obj1, obj2)) return;
                if (obj1 == null && JSONObject.NULL.equals(obj2)) return;
                if (obj2 == null && JSONObject.NULL.equals(obj1)) return;
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
        if ((obj1 == null || JSONObject.NULL.equals(obj1)) && (obj2 == null || Strings.isNullOrEmpty(obj2.toString()))) {
            return true;
        }
        if ((obj2 == null || JSONObject.NULL.equals(obj2)) && (obj1 == null || Strings.isNullOrEmpty(obj1.toString()))) {
            return true;
        }
        return false;
    }

}
