package com.arextest.diff.sdk;

import com.arextest.diff.eigen.EigenHandler;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.eigen.EigenOptions;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.utils.EigenOptionsToRulesConvert;
import com.arextest.diff.utils.MDCCompareUtil;
import java.util.Base64;

public class EigenSDK {

  private static final EigenHandler eigenHandler = new EigenHandler();

  public EigenResult calculateEigen(String msg) {
    MDCCompareUtil.addServiceName(MDCCompareUtil.SERVICE_NAME_VALUE);
    String decodeMsg = tryBase64Decode(msg);
    RulesConfig rulesConfig = EigenOptionsToRulesConvert.convert(decodeMsg, null);
    EigenResult eigenResult = eigenHandler.doHandler(rulesConfig);
    MDCCompareUtil.removeServiceName();
    return eigenResult;
  }

  public EigenResult calculateEigen(String msg, EigenOptions eigenOptions) {
    MDCCompareUtil.addServiceName(MDCCompareUtil.SERVICE_NAME_VALUE);
    String decodeMsg = tryBase64Decode(msg);
    RulesConfig rulesConfig = EigenOptionsToRulesConvert.convert(decodeMsg, eigenOptions);
    EigenResult eigenResult = eigenHandler.doHandler(rulesConfig);
    MDCCompareUtil.removeServiceName();
    return eigenResult;
  }

  private static String tryBase64Decode(String encoded) {
    try {
      if (encoded == null) {
        return null;
      }
      if (isJson(encoded)) {
        return encoded;
      }
      String decoded = new String(Base64.getDecoder().decode(encoded));
      if (isJson(decoded)) {
        return decoded;
      }
      return encoded;
    } catch (Exception e) {
      return encoded;
    }
  }

  private static boolean isJson(String value) {
    if (value.startsWith("{") && value.endsWith("}")) {
      return true;
    } else {
      return value.startsWith("[") && value.endsWith("]");
    }
  }
}
