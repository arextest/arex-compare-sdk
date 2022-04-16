package io.arex.diff.utils;

import io.arex.diff.model.enumeration.Constant;
import io.arex.diff.model.log.NodeEntity;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListUti {

    public static boolean stringListEqual(List<String> listA, List<String> listB) {
        if (listA == listB) {
            return true;
        }
        if (listA == null || listB == null) {
            return false;
        }

        if (listA.size() == listB.size()) {
            for (int i = 0; i < listA.size(); i++) {
                if (!listA.get(i).equals(listB.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;

    }

    public static boolean stringListEqualsOnWildcard(List<String> listA, List<String> listB) {
        if (listA == listB) {
            return true;
        }
        if (listA == null || listB == null) {
            return false;
        }

        if (listA.size() == listB.size()) {
            for (int i = 0; i < listA.size(); i++) {
                if (!listA.get(i).equals(listB.get(i)) && !listA.get(i).equals(Constant.DYNAMIC_PATH) && !listB.get(i).equals(Constant.DYNAMIC_PATH)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static List<String> convertToStringList(List<NodeEntity> list) {
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        List<String> nodes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getNodeName() != null) {
                nodes.add(list.get(i).getNodeName());
            }
        }
        return nodes;
    }

    public static String convertToString2(List<?> list) {
        if (list == null || list.size() == 0) return null;
        StringBuilder sb = new StringBuilder();
        if (list.get(0) instanceof NodeEntity) {
            for (int i = 0; i < list.size(); i++) {
                String nodeName = ((NodeEntity) list.get(i)).getNodeName();
                if (nodeName != null) {
                    sb.append(nodeName).append((i == list.size() - 1 ? "" : "\\"));
                }
            }
            if (sb.charAt(sb.length() - 1) == '\\') {
                sb.deleteCharAt(sb.length() - 1);
            }
            return sb.toString();
        }
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                sb.append(list.get(i)).append("\\");
            } else {
                sb.append(list.get(i));
            }
        }
        return sb.toString();
    }

    public static String convertPathToStringForShow(List<NodeEntity> nodes) {
        if (nodes == null) {
            return null;
        }
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            String suffix = (i == nodes.size() - 1) ? "" : ".";
            NodeEntity no = nodes.get(i);
            if (!StringUtil.isEmpty(no.getNodeName())) {
                path.append(no.getNodeName() + suffix);
            } else {
                if (path.length()>0){
                    path.deleteCharAt(path.length() - 1);
                }
                path.append("[").append(no.getIndex()).append("]").append(suffix);
            }
        }
        return path.toString();
    }

    public static List<String> deepCopy(List<String> orig) {
        List<String> dest = new ArrayList<>();
        for (int i = 0; i < orig.size(); i++) {
            dest.add(orig.get(i));
        }
        return dest;
    }

    public static void removeLast(List<?> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        list.remove(list.size() - 1);
    }

}
