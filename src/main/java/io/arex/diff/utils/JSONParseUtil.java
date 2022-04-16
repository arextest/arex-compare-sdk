package io.arex.diff.utils;

import io.arex.diff.model.log.NodeEntity;

import java.util.*;

public class JSONParseUtil {

    public static Map<String, List<String>> getTotalParses(Map<List<NodeEntity>, String> leftParseNodePaths, Map<List<NodeEntity>, String> rightParseNodePaths) {

        Set<List<NodeEntity>> leftSet = new HashSet<>(leftParseNodePaths.keySet());
        Set<List<NodeEntity>> rightSet = new HashSet<>(rightParseNodePaths.keySet());

        Map<String, List<String>> parseNodePaths = new HashMap<>();

        for (List<NodeEntity> leftPath : leftSet) {
            parseNodePaths.put(ListUti.convertPathToStringForShow(leftPath),
                    new ArrayList<String>() {{
                        add(leftParseNodePaths.get(leftPath));
                        add(rightParseNodePaths.getOrDefault(leftPath, ""));
                    }});
            rightSet.remove(leftPath);
        }
        for (List<NodeEntity> rightPath : rightSet) {
            parseNodePaths.put(ListUti.convertPathToStringForShow(rightPath),
                    new ArrayList<String>() {{
                        add("");
                        add(rightParseNodePaths.get(rightPath));
                    }});
        }
        return parseNodePaths;
    }


}
