package com.arextest.diff.model;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by rchen9 on 2022/12/22.
 */
public class SystemConfig {

    private static final Set<String> ignoreNodeSet = new HashSet<>();

    public static Set<String> getIgnoreNodeSet() {
        return ignoreNodeSet;
    }

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
        } catch (Exception e) {
        }
    }


}