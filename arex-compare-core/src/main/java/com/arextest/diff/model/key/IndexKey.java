package com.arextest.diff.model.key;


import com.arextest.diff.model.log.NodeEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexKey {

  List<IndexKey> listChild = new ArrayList<>();
  private List<String> uniqueKeys;
  private Map<NodeEntity, IndexKey> child = new HashMap<>();


  public IndexKey() {
  }

  public List<String> getUniqueKeys() {
    return uniqueKeys;
  }

  public void setUniqueKeys(List<String> uniqueKeys) {
    this.uniqueKeys = uniqueKeys;
  }

  public Map<NodeEntity, IndexKey> getChild() {
    return child;
  }

  public void setChild(Map<NodeEntity, IndexKey> child) {
    this.child = child;
  }

  public List<IndexKey> getListChild() {
    return listChild;
  }

  public void setListChild(List<IndexKey> listChild) {
    this.listChild = listChild;
  }

}
