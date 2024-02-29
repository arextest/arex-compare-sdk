package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.metric.MetricIndicator;
import java.util.List;
import java.util.Map;

public class CompareResult {

  /**
   * Compare status codes{@link DiffResultCode}
   */
  private int code;

  // status information
  private String message;

  // flag indicating whether the comparison message is lost
  private MsgInfo msgInfo;

  // inconsistent message node records
  private List<LogEntity> logs;

  // processed basic messages, such as: decompression/json expansion/sql parsing
  private String processedBaseMsg;
  // processed test messages, such as: decompression/json expansion/sql parsing
  private String processedTestMsg;

  // K:parsed node path, V:original value
  private Map<String, List<String>> parseNodePaths;
  // metric indicator
  private MetricIndicator metricIndicator;

  public CompareResult() {
  }

  public CompareResult(int code, String message, MsgInfo msgInfo, List<LogEntity> logs,
      String processedBaseMsg, String processedTestMsg, Map<String, List<String>> parseNodePaths) {
    this.code = code;
    this.message = message;
    this.msgInfo = msgInfo;
    this.logs = logs;
    this.processedBaseMsg = processedBaseMsg;
    this.processedTestMsg = processedTestMsg;
    this.parseNodePaths = parseNodePaths;
  }

  public static CompareBuilder builder() {
    return new CompareBuilder();
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public List<LogEntity> getLogs() {
    return logs;
  }

  public void setLogs(List<LogEntity> logs) {
    this.logs = logs;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public MsgInfo getMsgInfo() {
    return msgInfo;
  }

  public void setMsgInfo(MsgInfo msgInfo) {
    this.msgInfo = msgInfo;
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

  public MetricIndicator getMetricIndicator() {
    return metricIndicator;
  }

  public void setMetricIndicator(MetricIndicator metricIndicator) {
    this.metricIndicator = metricIndicator;
  }
}
