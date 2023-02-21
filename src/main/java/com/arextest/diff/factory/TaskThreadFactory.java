package com.arextest.diff.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskThreadFactory {

    public static ExecutorService jsonObjectThreadPool = new ThreadPoolExecutor(2, 7,
            1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(500), new NamedThreadFactory("JsonObject"),
            new CallerRunsPolicyWithReport("JsonObject"));

    public static ExecutorService structureHandlerThreadPool = new ThreadPoolExecutor(2, 7,
            1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(500), new NamedThreadFactory("structureHandler"),
            new CallerRunsPolicyWithReport("structureHandler"));

}
