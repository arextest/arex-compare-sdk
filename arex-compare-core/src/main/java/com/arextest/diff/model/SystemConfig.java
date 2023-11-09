package com.arextest.diff.model;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by rchen9 on 2022/12/22.
 */
public class SystemConfig {

  private static final Set<String> ignoreNodeSet = new HashSet<>();
  private static boolean metricsEnable = true;

  static {
    try {
      InputStream in = SystemConfig.class.getResourceAsStream("/sdkConfig.properties");
      Properties properties = new Properties();
      properties.load(in);
      String ignoreNodes = properties.getProperty("ignore.nodes");
      if (StringUtils.isNotBlank(ignoreNodes)) {
        String[] split = ignoreNodes.split(",");
        ignoreNodeSet.addAll(Arrays.asList(split));
      }

      String metricsEnableStr = properties.getProperty("metrics.enable");
      if (StringUtils.isNotBlank(metricsEnableStr) && "false".equals(metricsEnableStr)) {
        metricsEnable = false;
      }
    } catch (Exception e) {
    }
  }

  public static Set<String> getIgnoreNodeSet() {
    return ignoreNodeSet;
  }

  public static boolean isMetricsEnable() {
    return metricsEnable;
  }


}
