package com.arextest.diff.handler.decompress;

import com.arextest.diff.model.classloader.RemoteJarClassLoader;
import com.arextest.diff.service.DecompressService;
import com.arextest.diff.utils.RemoteJarLoaderUtils;
import com.arextest.diff.utils.StringUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rchen9 on 2023/4/26.
 */
public class DecompressServiceBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(DecompressServiceBuilder.class);

  private static Map<String, DecompressService> systemDecompressServiceMap = new HashMap<>();

  // use caffeine to expire the decompressService of application
  private static Cache<String, Map<String, DecompressService>> decompressServiceCache =
      Caffeine.newBuilder().maximumSize(10_000).removalListener(((key, value, cause) -> {
        if (cause.equals(RemovalCause.SIZE)) {
          LOGGER.warn("DecompressServiceCache is too large, key : {}, cause : {}", key, cause);
        }
      })).expireAfterWrite(2, TimeUnit.HOURS).build();

  public static DecompressService getDecompressService(String pluginUrl, String beanName) {
    if (StringUtil.isEmpty(pluginUrl)) {
      return systemDecompressServiceMap.get(beanName);
    }
    Map<String, DecompressService> decompressServiceMap = loadApplicationDecompressService(
        pluginUrl);
    DecompressService decompressService =
        decompressServiceMap == null ? null : decompressServiceMap.get(beanName);
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

  private static Map<String, DecompressService> loadApplicationDecompressService(
      String decompressJarUrl) {
    if (StringUtil.isEmpty(decompressJarUrl)) {
      return null;
    }
    Map<String, DecompressService> decompressServiceMap = decompressServiceCache.getIfPresent(
        decompressJarUrl);
    if (decompressServiceMap == null) {
      decompressServiceMap = buildDecompressServicesFromURL(decompressJarUrl);
      decompressServiceCache.put(decompressJarUrl, decompressServiceMap);
    }
    return decompressServiceMap;
  }

  private static Map<String, DecompressService> buildDecompressServicesFromURL(
      String decompressJarUrl) {

    Map<String, DecompressService> result = new HashMap<>();
    RemoteJarClassLoader serviceClassLoader;
    try {
      serviceClassLoader = RemoteJarLoaderUtils.loadJar(decompressJarUrl);
      List<DecompressService> decompressServices =
          RemoteJarLoaderUtils.loadService(DecompressService.class, serviceClassLoader);
      for (DecompressService decompressService : decompressServices) {
        if (decompressService.getAliasName() != null) {
          result.put(decompressService.getAliasName(), decompressService);
        } else {
          result.put(decompressService.getClass().getName(), decompressService);
        }
      }

    } catch (Throwable e) {
      LOGGER.warn("load decompress service error, jar url : {}", decompressJarUrl, e);
      return Collections.emptyMap();
    }
    try {
      serviceClassLoader.close();
    } catch (IOException e) {
      LOGGER.warn("close serviceClassLoader error, jar url : {}", decompressJarUrl, e);
    }

    LOGGER.info("load decompress service success, serviceSet:{}", result.keySet());
    return result;
  }

}
