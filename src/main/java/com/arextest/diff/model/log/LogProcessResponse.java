package com.arextest.diff.model.log;

import java.util.ArrayList;
import java.util.List;

public class LogProcessResponse {

    private int existDiff;

    private List<LogEntity> logs = new ArrayList<>();

    public LogProcessResponse() {
    }

    public LogProcessResponse(int existDiff, List<LogEntity> logs) {
        this.existDiff = existDiff;
        this.logs = logs;
    }

    public int getExistDiff() {
        return existDiff;
    }

    public void setExistDiff(int existDiff) {
        this.existDiff = existDiff;
    }

    public List<LogEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEntity> logs) {
        this.logs = logs;
    }
}
