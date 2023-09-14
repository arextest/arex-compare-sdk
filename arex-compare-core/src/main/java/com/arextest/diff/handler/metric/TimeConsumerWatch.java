package com.arextest.diff.handler.metric;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arextest.diff.model.SystemConfig;

public class TimeConsumerWatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeConsumerWatch.class);

    private Long startTime;

    private Long endTime;

    private Map<String, TaskInfo> taskMap = new LinkedHashMap<>();

    public void start(String taskName) {
        taskMap.computeIfAbsent(taskName, k -> new TaskInfo(taskName, System.currentTimeMillis(), null))
            .setStartTime(System.currentTimeMillis());

    }

    public void end(String taskName) {
        TaskInfo taskInfo = taskMap.get(taskName);
        if (taskInfo != null) {
            taskInfo.setData(System.currentTimeMillis() - taskInfo.getStartTime());
        }
    }

    public void record() {
        if (SystemConfig.isMetricsEnable()) {
            LOGGER.info("TimeConsumerWatch: {}", taskMap);
        }
    }

    private static class TaskInfo {
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
