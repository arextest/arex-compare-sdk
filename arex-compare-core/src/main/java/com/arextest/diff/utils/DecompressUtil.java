package com.arextest.diff.utils;

import com.arextest.diff.handler.decompress.DecompressServiceBuilder;
import com.arextest.diff.model.DecompressConfig;
import com.arextest.diff.service.DecompressService;

public class DecompressUtil {

    public static String decompressPlugin(String pluginJarUrl, DecompressConfig decompressConfig, String fieldValue) throws Throwable {
        String name = decompressConfig.getName();
        String args = decompressConfig.getArgs();
        DecompressService decompressService = DecompressServiceBuilder.getDecompressService(pluginJarUrl, name);
        if (decompressService == null) {
            throw new Exception("decompressService not exist");
        }
        return decompressService.decompress(fieldValue, args);
    }

}
