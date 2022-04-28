package com.arextest.diff.utils;

import com.arextest.diff.service.DecompressService;

import java.util.Map;

public class DeCompressUtil {

    public static Object deCompressPlugin(Map<String, DecompressService> decompressServices, String beanPath, String arg) throws Throwable {
        if (decompressServices == null || decompressServices.isEmpty()) {
            throw new Exception("decompressService not exist");
        }
        if (!decompressServices.containsKey(beanPath)) {
            throw new Exception("decompressService not exist");
        }
        return decompressServices.get(beanPath).decompress(arg);
    }

}
