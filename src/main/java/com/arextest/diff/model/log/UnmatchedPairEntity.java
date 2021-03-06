package com.arextest.diff.model.log;

import com.arextest.diff.utils.ListUti;
import com.arextest.diff.model.enumeration.UnmatchedType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by yuxiangshi on 2017/7/12.
 */
public class UnmatchedPairEntity implements Serializable {
    /**
     * {@link UnmatchedType}
     */
    private int unmatchedType;
    private List<NodeEntity> leftUnmatchedPath;
    private List<NodeEntity> rightUnmatchedPath;
    private List<String> listKeys;
    private Trace trace;

    public UnmatchedPairEntity() {
        leftUnmatchedPath = new ArrayList<>();
        rightUnmatchedPath = new ArrayList<>();
    }

    public UnmatchedPairEntity(int unmatchedType, List<NodeEntity> leftUnmatchedPath, List<NodeEntity> rightUnmatchedPath, Trace trace) {
        this.unmatchedType = unmatchedType;
        this.leftUnmatchedPath = deepCopy(leftUnmatchedPath);
        this.rightUnmatchedPath = deepCopy(rightUnmatchedPath);
        this.trace = trace;
    }

    public int getUnmatchedType() {
        return unmatchedType;
    }

    public void setUnmatchedType(int unmatchedType) {
        this.unmatchedType = unmatchedType;
    }

    public List<NodeEntity> getLeftUnmatchedPath() {
        return leftUnmatchedPath;
    }

    public void setLeftUnmatchedPath(List<NodeEntity> leftUnmatchedPath) {
        this.leftUnmatchedPath = leftUnmatchedPath;
    }

    public List<NodeEntity> getRightUnmatchedPath() {
        return rightUnmatchedPath;
    }

    public void setRightUnmatchedPath(List<NodeEntity> rightUnmatchedPath) {
        this.rightUnmatchedPath = rightUnmatchedPath;
    }

    public UnmatchedPairEntity setListKeys(List<String> listKeys) {
        this.listKeys = new ArrayList<>(listKeys);
        return this;
    }

    public List<String> getListKeys() {
        return listKeys;
    }

    public Trace getTrace() {
        return trace;
    }

    public void setTrace(Trace trace) {
        this.trace = trace;
    }

    private List<NodeEntity> deepCopy(List<NodeEntity> origList) {
        if (origList instanceof ArrayList) {
            return (List<NodeEntity>) ((ArrayList) origList).clone();

        } else {
            List<NodeEntity> nodeEntities = new ArrayList<>();
            if (origList != null) {
                for (NodeEntity node : origList) {
                    nodeEntities.add(new NodeEntity(node.getNodeName(), node.getIndex()));
                }
            }
            return nodeEntities;
        }

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnmatchedPairEntity that = (UnmatchedPairEntity) o;
        return unmatchedType == that.unmatchedType &&
                Objects.equals(ListUti.convertToStringList(leftUnmatchedPath), ListUti.convertToStringList(that.leftUnmatchedPath)) &&
                Objects.equals(ListUti.convertToStringList(rightUnmatchedPath), ListUti.convertToStringList(that.rightUnmatchedPath));
    }

    @Override
    public int hashCode() {
        return Objects.hash(unmatchedType, ListUti.convertToStringList(leftUnmatchedPath), ListUti.convertToStringList(rightUnmatchedPath));
    }
}
