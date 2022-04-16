package io.arex.diff.model.log;

import java.util.List;

public class LogTagAddRequest {
    private List<LogEntity> logs;
    private List<List<String>> ignoreNodePaths;

    public LogTagAddRequest() {
    }

    public LogTagAddRequest(List<LogEntity> logs, List<List<String>> ignoreNodePaths) {
        this.logs = logs;
        this.ignoreNodePaths = ignoreNodePaths;
    }

    public List<LogEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEntity> logs) {
        this.logs = logs;
    }

    public List<List<String>> getIgnoreNodePaths() {
        return ignoreNodePaths;
    }

    public void setIgnoreNodePaths(List<List<String>> ignoreNodePaths) {
        this.ignoreNodePaths = ignoreNodePaths;
    }
}
