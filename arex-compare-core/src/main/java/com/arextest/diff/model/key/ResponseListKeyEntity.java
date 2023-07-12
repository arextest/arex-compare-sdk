package com.arextest.diff.model.key;

public class ResponseListKeyEntity {
    private String listNodePath;
    private String keyNodes;

    public ResponseListKeyEntity() {
    }

    public ResponseListKeyEntity(String listNodePath, String keyNodes) {
        this.listNodePath = listNodePath;
        this.keyNodes = keyNodes;
    }


    public String getListNodePath() {
        return listNodePath;
    }

    public void setListNodePath(String listNodePath) {
        this.listNodePath = listNodePath;
    }

    public String getKeyNodes() {
        return keyNodes;
    }

    public void setKeyNodes(String keyNodes) {
        this.keyNodes = keyNodes;
    }

    @Override
    public String toString() {
        return "{" +
                "listNodePath='" + listNodePath + '\'' +
                ", keyNodes='" + keyNodes + '\'' +
                '}';
    }
}