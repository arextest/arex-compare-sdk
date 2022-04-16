package io.arex.diff.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskThreadFactory {

    public static ExecutorService jsonObjectThreadPool = new ThreadPoolExecutor(7, 7,
            1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(500), new NamedThreadFactory("JsonObject"),
            new CallerRunsPolicyWithReport("JsonObject"));

    public static ExecutorService keyHandlerThreadPool = new ThreadPoolExecutor(7, 7,
            1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(500), new NamedThreadFactory("KeyHandler"),
            new CallerRunsPolicyWithReport("KeyHandler"));

}
