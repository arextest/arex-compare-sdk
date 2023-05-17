package com.arextest.diff.utils;

import com.arextest.diff.handler.decompress.DeCompressServiceBuilder;
import com.arextest.diff.model.DeCompressConfig;
import com.arextest.diff.service.DecompressService;

public class DeCompressUtil {

    public static String deCompressPlugin(String pluginJarUrl, DeCompressConfig deCompressConfig, String fieldValue) throws Throwable {
        String name = deCompressConfig.getName();
        String args = deCompressConfig.getArgs();
        DecompressService decompressService = DeCompressServiceBuilder.getDecompressService(pluginJarUrl, name);
        if (decompressService == null) {
            throw new Exception("decompressService not exist");
        }
        return decompressService.decompress(fieldValue, args);
    }

}
