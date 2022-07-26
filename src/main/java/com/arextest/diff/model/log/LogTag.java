package com.arextest.diff.model.log;

import java.io.Serializable;

public class LogTag implements Serializable {

    private Boolean ig = false;

    public LogTag() {
    }

    public Boolean getIg() {
        return ig;
    }

    public void setIg(Boolean ig) {
        this.ig = ig;
    }

}
