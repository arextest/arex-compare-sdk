package com.arextest.diff.compare;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.ListUti;
import com.google.common.base.Strings;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static com.arextest.diff.compare.CompareHelper.getUnmatchedPair;

/**
 * Created by rchen9 on 2022/7/25.
 */
public class GenericCompare {

    public static void jsonCompare(Object obj1, Object obj2, CompareContext compareContext) throws JSONException {

        HashSet<List<String>> pkNodePaths = compareContext.getPkNodePaths();
        List<NodeEntity> currentNodeLeft = compareContext.getCurrentNodeLeft();
        boolean notDistinguishNullAndEmpty = compareContext.isNotDistinguishNullAndEmpty();
        List<LogEntity> logs = compareContext.getLogs();

        // ignore primary key node
        if (pkNodePaths != null) {
            for (List<String> pkNodePath : pkNodePaths) {
                if (pkNodePath.equals(ListUti.convertToStringList(currentNodeLeft))) {
                    return;
                }
            }
        }

        if (Objects.equals(obj1, obj2)) {
            return;
        }

        // There is a null value in any of the left and right nodes
        if (obj1 == null || obj2 == null || JSONObject.NULL.equals(obj1) || JSONObject.NULL.equals(obj2)) {
            if (notDistinguishNullAndEmpty) {
                if (bothEmptyString(obj1, obj2)) return;
                if (obj1 == null && JSONObject.NULL.equals(obj2)) return;
                if (obj2 == null && JSONObject.NULL.equals(obj1)) return;
                if (obj1 instanceof JSONArray && ((JSONArray) obj1).length() == 0) return;
                if (obj2 instanceof JSONArray && ((JSONArray) obj2).length() == 0) return;
            }

            boolean leftNull = (obj1 == null || JSONObject.NULL.equals(obj1));
            LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(leftNull ? UnmatchedType.LEFT_MISSING : UnmatchedType.RIGHT_MISSING, compareContext)
                    .setListKeys(leftNull ? compareContext.getCurrentListKeysRight() : compareContext.getCurrentListKeysLeft()));
            logs.add(log);
            return;
        }

        // obj1 and obj2 are different types
        if (differentType(obj1, obj2)) {
            LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(UnmatchedType.DIFFERENT_TYPE, compareContext)
                    .setListKeys(compareContext.getCurrentListKeysLeft()));
            logs.add(log);

            // Eliminate type inconsistencies
            obj1 = obj1.toString();
            obj2 = obj2.toString();
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

    private static boolean differentType(Object obj1, Object obj2) {
        if (obj1.getClass().equals(obj2.getClass())) {
            return false;
        }
        if (JSONObject.NULL.equals(obj1) && obj2 instanceof String) {
            return false;
        }
        if (JSONObject.NULL.equals(obj2) && obj1 instanceof String) {
            return false;
        }
        if (obj1 instanceof Number && obj2 instanceof Number) {
            return false;
        }
        if (bothNumberOrString(obj1, obj2)) {
            return false;
        }
        return true;
    }

    private static boolean bothNumberOrString(Object obj1, Object obj2) {
        return (obj1 instanceof Number && obj2 instanceof String) || (obj2 instanceof Number && obj1 instanceof String);
    }


}
