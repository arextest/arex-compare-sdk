package com.arextest.diff.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StringUtil {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().equals("");
    }

    public static boolean jsonEmpty(Object o) {
        if (o == null || o instanceof NullNode) {
            return true;
        }
        if (o instanceof ArrayNode) {
            if (((ArrayNode) o).size() == 0) {
                return true;
            }
        }
        String s = String.valueOf(o);
        return s.equals("null") || s.equals("");
    }

    public static boolean jsonEmptyJudge(Object o) {
        if (o == null || o instanceof NullNode) {
            return true;
        }
        if (o instanceof ArrayNode) {
            if (((ArrayNode) o).size() == 0) {
                return true;
            }
        } else if (o instanceof ObjectNode) {
            return false;
        } else {
            String s = String.valueOf(o);
            return s.equals("");
        }
        return false;
    }
}
