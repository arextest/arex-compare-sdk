package com.arextest.diff.handler.decompress;

import com.arextest.diff.model.classloader.RemoteJarClassLoader;
import com.arextest.diff.service.DecompressService;
import com.arextest.diff.utils.RemoteJarLoaderUtils;
import com.arextest.diff.utils.StringUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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
public class TransformServiceBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransformServiceBuilder.class);
  private static PluginDecompressServiceSummary systemDecompressServiceMap =
      new PluginDecompressServiceSummary(null, Collections.emptyMap());

  /**
   * use caffeine to expire the decompressService of application key: pluginUrl value:
   * Map<beanName,DecompressService>
   */
  private static Cache<String, PluginDecompressServiceSummary> decompressServiceCache =
      Caffeine.newBuilder().maximumSize(100).removalListener(((key, value, cause) -> {
        LOGGER.info("DecompressServiceCache expire, key : {}, cause : {}", key, cause);
        try {
          if (value instanceof PluginDecompressServiceSummary) {
            RemoteJarClassLoader serviceClassLoader = ((PluginDecompressServiceSummary) value)
                .getServiceClassLoader();
            if (serviceClassLoader != null) {
              serviceClassLoader.close();
            }
          }
        } catch (Exception e) {
          LOGGER.warn("close serviceClassLoader error, jar url : {}", key, e);
        }
      })).expireAfterWrite(2, TimeUnit.HOURS).build();

  public static DecompressService getDecompressService(String pluginUrl, String beanName) {
    if (StringUtil.isEmpty(pluginUrl)) {
      return systemDecompressServiceMap.getDecompressServiceMap().get(beanName);
    }
    Map<String, DecompressService> decompressServiceMap =
        loadApplicationDecompressService(pluginUrl).getDecompressServiceMap();
    DecompressService decompressService =
        decompressServiceMap == null ? null : decompressServiceMap.get(beanName);
    if (decompressService == null) {
      decompressService = systemDecompressServiceMap.getDecompressServiceMap().get(beanName);
    }
    return decompressService;
  }

  public static void loadSystemDecompressService(String decompressJarUrl) {
    if (StringUtil.isEmpty(decompressJarUrl)) {
      return;
    }
    systemDecompressServiceMap = buildDecompressServicesFromURL(decompressJarUrl);
  }

  private static PluginDecompressServiceSummary loadApplicationDecompressService(
      String decompressJarUrl) {
    if (StringUtil.isEmpty(decompressJarUrl)) {
      return new PluginDecompressServiceSummary(null, null);
    }
    PluginDecompressServiceSummary decompressServiceMap = decompressServiceCache.getIfPresent(
        decompressJarUrl);
    if (decompressServiceMap == null) {
      decompressServiceMap = buildDecompressServicesFromURL(decompressJarUrl);
      decompressServiceCache.put(decompressJarUrl, decompressServiceMap);
    }
    return decompressServiceMap;
  }

  private static PluginDecompressServiceSummary buildDecompressServicesFromURL(
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
      return new PluginDecompressServiceSummary(null, Collections.emptyMap());
    }
    LOGGER.info("load decompress service success, serviceSet:{}", result.keySet());
    return new PluginDecompressServiceSummary(serviceClassLoader, result);
  }

  private static class PluginDecompressServiceSummary {

    private RemoteJarClassLoader serviceClassLoader;
    private Map<String, DecompressService> decompressServiceMap;

    public PluginDecompressServiceSummary(RemoteJarClassLoader serviceClassLoader,
        Map<String, DecompressService> decompressServiceMap) {
      this.serviceClassLoader = serviceClassLoader;
      this.decompressServiceMap = decompressServiceMap;
    }

    public RemoteJarClassLoader getServiceClassLoader() {
      return serviceClassLoader;
    }

    public void setServiceClassLoader(
        RemoteJarClassLoader serviceClassLoader) {
      this.serviceClassLoader = serviceClassLoader;
    }

    public Map<String, DecompressService> getDecompressServiceMap() {
      return decompressServiceMap;
    }

    public void setDecompressServiceMap(
        Map<String, DecompressService> decompressServiceMap) {
      this.decompressServiceMap = decompressServiceMap;
    }
  }

}
