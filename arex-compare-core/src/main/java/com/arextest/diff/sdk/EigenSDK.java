package com.arextest.diff.sdk;

import com.arextest.diff.eigen.EigenHandler;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.eigen.EigenOptions;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.utils.EigenOptionsToRulesConvert;
import com.arextest.diff.utils.MDCCompareUtil;

public class EigenSDK {

  private static final EigenHandler eigenHandler = new EigenHandler();

  public EigenResult calculateEigen(String msg) {
    MDCCompareUtil.addServiceName(MDCCompareUtil.SERVICE_NAME_VALUE);
    RulesConfig rulesConfig = EigenOptionsToRulesConvert.convert(msg, null);
    EigenResult eigenResult = eigenHandler.doHandler(rulesConfig);
    MDCCompareUtil.removeServiceName();
    return eigenResult;
  }

  public EigenResult calculateEigen(String msg, EigenOptions eigenOptions) {
    MDCCompareUtil.addServiceName(MDCCompareUtil.SERVICE_NAME_VALUE);
    RulesConfig rulesConfig = EigenOptionsToRulesConvert.convert(msg, eigenOptions);
    EigenResult eigenResult = eigenHandler.doHandler(rulesConfig);
    MDCCompareUtil.removeServiceName();
    return eigenResult;
  }
}
