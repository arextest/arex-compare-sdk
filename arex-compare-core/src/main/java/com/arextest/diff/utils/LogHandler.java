package com.arextest.diff.utils;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import java.util.List;
import java.util.Set;

public class LogHandler {

  // used to determine whether the path of the log with inconsistent number is a foreign key node
  public static boolean isFkListPath(List<String> path, List<List<String>> fkListSet) {
    for (List<String> fkListPath : fkListSet) {
      if (ListUti.stringListEqualsOnWildcard(fkListPath, path)) {
        return true;
      }
    }
    return false;
  }

  private static String convertPathToInConsistentPaths(List<NodeEntity> nodes) {
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
        if (path.length() > 0) {
          path.deleteCharAt(path.length() - 1);
        }
        path.append("[*]").append(suffix);
      }
    }
    return path.toString();
  }

  public static void processInConsistentPaths(LogEntity log, Set<String> inConsistentPaths) {
    if (log.getPathPair().getUnmatchedType() != UnmatchedType.DIFFERENT_COUNT) {
      List<NodeEntity> leftUnmatchedPath = log.getPathPair().getLeftUnmatchedPath();
      List<NodeEntity> rightUnmatchedPath = log.getPathPair().getRightUnmatchedPath();
      if (leftUnmatchedPath == null || rightUnmatchedPath == null) {
        return;
      }
      String inConsistentPath = convertPathToInConsistentPaths(
          leftUnmatchedPath.size() > rightUnmatchedPath.size() ? leftUnmatchedPath
              : rightUnmatchedPath);
      inConsistentPaths.add(inConsistentPath);
    }
  }
}
