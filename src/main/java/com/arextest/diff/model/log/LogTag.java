package com.arextest.diff.model.log;

import java.io.Serializable;

public class LogTag implements Serializable {

    // level
    private int lv;
    // beforeIgnore
    private Boolean ig = false;

    public LogTag() {
    }

    public Boolean getIg() {
        return ig;
    }

    public void setIg(Boolean ig) {
        this.ig = ig;
    }

    public int getLv() {
        return lv;
    }

    public void setLv(int lv) {
        this.lv = lv;
    }
}
