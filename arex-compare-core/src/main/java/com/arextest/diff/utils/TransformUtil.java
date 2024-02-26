package com.arextest.diff.utils;

import com.arextest.diff.handler.decompress.DecompressServiceBuilder;
import com.arextest.diff.model.TransformConfig.TransformMethod;
import com.arextest.diff.service.DecompressService;
import java.util.List;

public class TransformUtil {

  public static String transformPlugin(String pluginJarUrl,
      List<TransformMethod> transformMethodList,
      String fieldValue) throws Throwable {
    String result = fieldValue;
    for (TransformMethod transFormMethod : transformMethodList) {
      result = transform(result, pluginJarUrl, transFormMethod.getMethodName(),
          transFormMethod.getMethodArgs());
      if (StringUtil.isEmpty(result)) {
        break;
      }
    }
    return result;
  }


  private static String transform(String fieldValue, String pluginJarUrl, String methodName,
      String methodArgs) {
    DecompressService decompressService = DecompressServiceBuilder.getDecompressService(
        pluginJarUrl, methodName);
    if (decompressService == null) {
      throw new RuntimeException("decompressService not exist");
    }
    return decompressService.decompress(fieldValue, methodArgs);
  }

}
