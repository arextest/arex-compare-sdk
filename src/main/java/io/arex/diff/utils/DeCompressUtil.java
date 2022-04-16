package io.arex.diff.utils;

import io.arex.diff.service.DecompressService;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

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
