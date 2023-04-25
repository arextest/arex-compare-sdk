package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.log.LogEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompareResult {
    /**
     * Compare status codes{@link DiffResultCode}
     */
    private int code;
    // status information
    private String message;
    // inconsistent record
    private List<LogEntity> logs;
    private String processedBaseMsg;
    private String processedTestMsg;
    // parsed node path and original value
    private Map<String, List<String>> parseNodePaths;

    public CompareResult() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<LogEntity> getLogs() {
        return logs;
    }

    public void setLogs(List<LogEntity> logs) {
        this.logs = logs;
    }

    public String getProcessedBaseMsg() {
        return processedBaseMsg;
    }

    public void setProcessedBaseMsg(String processedBaseMsg) {
        this.processedBaseMsg = processedBaseMsg;
    }

    public String getProcessedTestMsg() {
        return processedTestMsg;
    }

    public void setProcessedTestMsg(String processedTestMsg) {
        this.processedTestMsg = processedTestMsg;
    }

    public Map<String, List<String>> getParseNodePaths() {
        return parseNodePaths;
    }

    public void setParseNodePaths(Map<String, List<String>> parseNodePaths) {
        this.parseNodePaths = parseNodePaths;
    }
}
