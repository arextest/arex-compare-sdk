package com.arextest.diff.factory;

import java.util.concurrent.ThreadPoolExecutor;

public class CallerRunsPolicyWithReport extends ThreadPoolExecutor.CallerRunsPolicy {


  private String threadPoolName;

  public CallerRunsPolicyWithReport() {
    super();
  }

  public CallerRunsPolicyWithReport(String threadPoolName) {
    this.threadPoolName = threadPoolName;
  }


  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
    String msg = String.format("The thread pool [%s] is full! Task: %d (completed: %d), Queue: %d",
        threadPoolName, e.getTaskCount(), e.getCompletedTaskCount(), e.getQueue().size());
    super.rejectedExecution(r, e);
  }
}
