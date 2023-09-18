package com.arextest.diff.model.metric;

import java.util.Map;

public class MetricIndicator {

    private Map<String, TaskInfo> timeMetric;

    public Map<String, TaskInfo> getTimeMetric() {
        return timeMetric;
    }

    public void setTimeMetric(Map<String, TaskInfo> timeMetric) {
        this.timeMetric = timeMetric;
    }

    public static class TaskInfo {
        private String taskName;

        private transient Long startTime;

        private Object data;

        public TaskInfo() {

        }

        public TaskInfo(String taskName, Long startTime, Object data) {
            this.taskName = taskName;
            this.startTime = startTime;
            this.data = data;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public Long getStartTime() {
            return startTime;
        }

        public void setStartTime(Long startTime) {
            this.startTime = startTime;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "TaskInfo{" + "taskName='" + taskName + '\'' + ", startTime=" + startTime + ", data=" + data + '}';
        }
    }
}
