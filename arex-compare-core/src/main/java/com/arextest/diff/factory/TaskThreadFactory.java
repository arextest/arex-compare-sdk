package com.arextest.diff.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskThreadFactory {

  private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();
  private static final int MAX_POOL_SIZE = CORE_SIZE + 1;
  private static final int QUENE_SIZE = 500;
  public static ExecutorService jsonObjectThreadPool = new ThreadPoolExecutor(CORE_SIZE,
      MAX_POOL_SIZE,
      1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(QUENE_SIZE),
      new NamedThreadFactory("JsonObject"),
      new CallerRunsPolicyWithReport("JsonObject"));
  public static ExecutorService structureHandlerThreadPool = new ThreadPoolExecutor(CORE_SIZE,
      MAX_POOL_SIZE,
      1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(QUENE_SIZE),
      new NamedThreadFactory("structureHandler"),
      new CallerRunsPolicyWithReport("structureHandler"));

}
