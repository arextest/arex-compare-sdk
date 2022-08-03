package com.arextest.diff.model.log;

import java.io.Serializable;

public class LogTag implements Serializable {

    private Boolean ig = false;
    private int errorType = 0;

    public LogTag() {
    }

    public Boolean getIg() {
        return ig;
    }

    public void setIg(Boolean ig) {
        this.ig = ig;
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }
}
