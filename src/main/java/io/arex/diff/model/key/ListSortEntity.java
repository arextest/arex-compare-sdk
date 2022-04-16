package io.arex.diff.model.key;

import java.io.Serializable;
import java.util.List;

public class ListSortEntity implements Serializable {
    private List<String> listNodepath;

    private List<List<String>> keys;

    private List<String> referenceNodeRelativePath;


    public List<String> getListNodepath() {
        return listNodepath;
    }

    public void setListNodepath(List<String> listNodepath) {
        this.listNodepath = listNodepath;
    }

    public List<List<String>> getKeys() {
        return keys;
    }

    public void setKeys(List<List<String>> keys) {
        this.keys = keys;
    }

    public List<String> getReferenceNodeRelativePath() {
        return referenceNodeRelativePath;
    }

    public void setReferenceNodeRelativePath(List<String> referenceNodeRelativePath) {
        this.referenceNodeRelativePath = referenceNodeRelativePath;
    }


    public enum ListKeyType {
        First,
        Combination,
        OrderlyCombination;

    }

}
