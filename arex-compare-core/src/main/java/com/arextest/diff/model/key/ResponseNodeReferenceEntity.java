package com.arextest.diff.model.key;

public class ResponseNodeReferenceEntity {

    private String pkNode;
    private String fkNode;
    private String pkNodePath;

    public ResponseNodeReferenceEntity() {
    }

    public ResponseNodeReferenceEntity(String pkNode, String fkNode) {
        this.pkNode = pkNode;
        this.fkNode = fkNode;
    }

    public String getPkNode() {
        return pkNode;
    }

    public void setPkNode(String pkNode) {
        this.pkNode = pkNode;
    }


    public String getFkNode() {
        return fkNode;
    }

    public void setFkNode(String fkNode) {
        this.fkNode = fkNode;
    }


    public String getPkNodePath() {
        return pkNodePath;
    }

    public void setPkNodePath(String pkNodePath) {
        this.pkNodePath = pkNodePath;
    }

    @Override
    public String toString() {
        return "{" +
                "pkNode='" + pkNode + '\'' +
                ", fkNode='" + fkNode + '\'' +
                ", pkNodePath='" + pkNodePath + '\'' +
                '}';
    }
}
