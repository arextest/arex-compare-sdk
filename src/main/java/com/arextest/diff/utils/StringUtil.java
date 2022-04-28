package com.arextest.diff.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class StringUtil {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static boolean jsonEmpty(Object o) {
        if (o == null || JSONObject.NULL.equals(o)) {
            return true;
        }
        if (o instanceof JSONArray) {
            if (((JSONArray) o).length() == 0) {
                return true;
            }
        }
        String s = String.valueOf(o);
        return s.equals("null") || s.equals("");
    }

    public static boolean jsonEmptyJudge(Object o) {
        if (o == null || JSONObject.NULL.equals(o)) {
            return true;
        }
        if (o instanceof JSONArray) {
            if (((JSONArray) o).length() == 0) {
                return true;
            }
        } else if (o instanceof JSONObject) {
            return false;
        } else {
            String s = String.valueOf(o);
            return s.equals("");
        }
        return false;
    }
}
