package com.arextest.diff.factory;

import com.arextest.diff.plugin.LogEntityFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by rchen9 on 2023/4/24.
 */
public class PluginServiceFactory {

  private static List<LogEntityFilter> logEntityFilterList = new ArrayList<>();

  static {
    try {
      ServiceLoader<LogEntityFilter> serviceLoader = ServiceLoader.load(LogEntityFilter.class);
      for (LogEntityFilter service : serviceLoader) {
        logEntityFilterList.add(service);
      }
    } catch (Throwable ignored) {
    }
  }

  public static List<LogEntityFilter> getLogEntityFilterList() {
    return logEntityFilterList;
  }
}
