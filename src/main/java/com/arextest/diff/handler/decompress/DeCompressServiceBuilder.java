package com.arextest.diff.handler.decompress;

import com.arextest.diff.service.DecompressService;
import com.arextest.diff.utils.StringUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Created by rchen9 on 2023/4/26.
 */
public class DeCompressServiceBuilder {

    private static Logger logger = Logger.getLogger(DeCompressServiceBuilder.class.getName());

    private static Map<String, DecompressService> systemDecompressServiceMap = new HashMap<>();

    // use caffeine to expire the decompressService of application
    private static Cache<String, Map<String, DecompressService>> decompressServiceCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .removalListener(((key, value, cause) -> {
                if (cause.equals(RemovalCause.SIZE)) {
                    logger.warning(String.format("DecompressServiceCache is too large, key : %s, cause : %s",
                            key, cause));
                }
            }))
            .expireAfterWrite(2, TimeUnit.HOURS)
            .build();

    public static DecompressService getDecompressService(String pluginUrl, String beanName) {
        if (StringUtil.isEmpty(pluginUrl)) {
            return systemDecompressServiceMap.get(beanName);
        }
        Map<String, DecompressService> decompressServiceMap = loadApplicationDecompressService(pluginUrl);
        DecompressService decompressService = decompressServiceMap == null ? null : decompressServiceMap.get(beanName);
        if (decompressService == null) {
            decompressService = systemDecompressServiceMap.get(beanName);
        }
        return decompressService;
    }

    public static void loadSystemDecompressService(String decompressJarUrl) {
        if (StringUtil.isEmpty(decompressJarUrl)) {
            return;
        }
        Map<String, DecompressService> tempMap = buildDecompressServicesFromURL(decompressJarUrl);
        systemDecompressServiceMap.putAll(tempMap);
    }

    private static Map<String, DecompressService> loadApplicationDecompressService(String decompressJarUrl) {
        if (StringUtil.isEmpty(decompressJarUrl)) {
            return null;
        }
        Map<String, DecompressService> decompressServiceMap = decompressServiceCache.getIfPresent(decompressJarUrl);
        if (decompressServiceMap == null) {
            decompressServiceMap = buildDecompressServicesFromURL(decompressJarUrl);
            decompressServiceCache.put(decompressJarUrl, decompressServiceMap);
        }
        return decompressServiceMap;
    }

    private static Map<String, DecompressService> buildDecompressServicesFromURL(String decompressJarUrl) {

        Map<String, DecompressService> result = new HashMap<>();
        URL resource = null;
        URLClassLoader serviceClassLoader = null;
        try {
            if (decompressJarUrl.startsWith("http")) {
                resource = new URL(decompressJarUrl);
            } else {
                resource = DeCompressServiceBuilder.class.getClassLoader().getResource(decompressJarUrl);
            }
            if (resource == null) {
                resource = new File(decompressJarUrl).toURI().toURL();
            }

            serviceClassLoader = new URLClassLoader(new URL[]{resource});

            ServiceLoader<DecompressService> load = ServiceLoader.load(DecompressService.class, serviceClassLoader);
            for (DecompressService decompressService : load) {
                if (decompressService.getAliasName() != null) {
                    result.put(decompressService.getAliasName(), decompressService);
                } else {
                    result.put(decompressService.getClass().getName(), decompressService);
                }
            }

        } catch (Throwable e) {
            return Collections.emptyMap();
        }

        try {
            serviceClassLoader.close();
        } catch (IOException e) {
        }
        return result;
    }

}
