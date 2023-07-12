package com.arextest.diff.model.log;

import java.io.Serializable;

public class LogTag implements Serializable {

    private int errorType = 0;

    public LogTag() {
    }

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }
}
