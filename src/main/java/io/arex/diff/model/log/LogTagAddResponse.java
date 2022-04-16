package io.arex.diff.model.log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LogTagAddResponse {

    private Set<String> inConsistentPaths = new HashSet<>();

    private int existDiff;

    private List<LogEntity> logs = new ArrayList<>();

    public LogTagAddResponse() {
    }

    public LogTagAddResponse(Set<String> inConsistentPaths, int existDiff, List<LogEntity> logs) {
        this.inConsistentPaths = inConsistentPaths;
        this.existDiff = existDiff;
        this.logs = logs;
    }

    public Set<String> getInConsistentPaths() {
        return inConsistentPaths;
    }

    public void setInConsistentPaths(Set<String> inConsistentPaths) {
        this.inConsistentPaths = inConsistentPaths;
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
