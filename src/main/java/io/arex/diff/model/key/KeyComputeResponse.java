package io.arex.diff.model.key;

import io.arex.diff.model.log.NodeEntity;

import java.util.HashMap;
import java.util.List;

public class KeyComputeResponse {

    private List<ReferenceEntity> allReferenceEntities;
    private List<ListSortEntity> listSortEntities;
    private HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft;
    private HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight;

    public List<ReferenceEntity> getAllReferenceEntities() {
        return allReferenceEntities;
    }

    public void setAllReferenceEntities(List<ReferenceEntity> allReferenceEntities) {
        this.allReferenceEntities = allReferenceEntities;
    }

    public List<ListSortEntity> getListSortEntities() {
        return listSortEntities;
    }

    public void setListSortEntities(List<ListSortEntity> listSortEntities) {
        this.listSortEntities = listSortEntities;
    }

    public HashMap<List<NodeEntity>, HashMap<Integer, String>> getListIndexKeysLeft() {
        return listIndexKeysLeft;
    }

    public void setListIndexKeysLeft(HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysLeft) {
        this.listIndexKeysLeft = listIndexKeysLeft;
    }

    public HashMap<List<NodeEntity>, HashMap<Integer, String>> getListIndexKeysRight() {
        return listIndexKeysRight;
    }

    public void setListIndexKeysRight(HashMap<List<NodeEntity>, HashMap<Integer, String>> listIndexKeysRight) {
        this.listIndexKeysRight = listIndexKeysRight;
    }
}
