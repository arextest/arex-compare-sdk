package com.arextest.diff.handler.metric;

import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.SystemConfig;
import com.arextest.diff.model.metric.MetricIndicator;
import java.util.LinkedHashMap;
import java.util.Map;

public class TimeConsumerWatch {

  private Map<String, MetricIndicator.TaskInfo> taskMap = new LinkedHashMap<>();

  public void start(String taskName) {
    taskMap.computeIfAbsent(taskName,
            k -> new MetricIndicator.TaskInfo(taskName, System.currentTimeMillis(), null))
        .setStartTime(System.currentTimeMillis());

  }

  public void end(String taskName) {
    MetricIndicator.TaskInfo taskInfo = taskMap.get(taskName);
    if (taskInfo != null) {
      taskInfo.setData(System.currentTimeMillis() - taskInfo.getStartTime());
    }
  }

  public void record(CompareResult compareResult) {

    if (compareResult == null) {
      return;
    }

    if (!SystemConfig.isMetricsEnable()) {
      return;
    }

    MetricIndicator metricIndicator = compareResult.getMetricIndicator();
    if (metricIndicator == null) {
      metricIndicator = new MetricIndicator();
      metricIndicator.setTimeMetric(taskMap);
      compareResult.setMetricIndicator(metricIndicator);
    } else {
      metricIndicator.setTimeMetric(taskMap);
    }

  }
}
