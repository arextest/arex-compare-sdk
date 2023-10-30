package com.arextest.diff.model.eigen;

import com.arextest.diff.model.enumeration.CategoryType;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EigenOptions {

  /**
   * @see CategoryType
   */
  private String categoryType;

  /**
   * the collection of the node path which is ignore
   */
  private Set<List<String>> exclusions;

  public EigenOptions() {
  }

  public static EigenOptions options() {
    return new EigenOptions();
  }

  public EigenOptions putExclusions(List<String> path) {
    if (path == null || path.isEmpty()) {
      return this;
    }
    if (this.exclusions == null) {
      this.exclusions = new HashSet<>();
    }
    this.exclusions.add(path);
    return this;
  }

  public EigenOptions putExclusions(Collection<List<String>> paths) {
    if (paths == null || paths.isEmpty()) {
      return this;
    }
    if (this.exclusions == null) {
      this.exclusions = new HashSet<>();
    }
    this.exclusions.addAll(paths);
    return this;
  }

  public String getCategoryType() {
    return categoryType;
  }

  public Set<List<String>> getExclusions() {
    return exclusions;
  }
}
