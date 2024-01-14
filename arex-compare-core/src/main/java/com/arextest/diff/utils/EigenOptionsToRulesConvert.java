package com.arextest.diff.utils;

import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.eigen.EigenOptions;

public class EigenOptionsToRulesConvert {

  public static RulesConfig convert(String msg, EigenOptions eigenOptions) {
    RulesConfig rulesConfig = new RulesConfig();
    rulesConfig.setBaseMsg(msg);
    rulesConfig.setNameToLower(true);
    copyOptionsToRules(eigenOptions, rulesConfig);
    configToLower(rulesConfig);
    return rulesConfig;
  }

  private static void copyOptionsToRules(EigenOptions eigenOptions, RulesConfig rulesConfig) {
    if (eigenOptions == null) {
      return;
    }
    rulesConfig.setCategoryType(eigenOptions.getCategoryType());
//    rulesConfig.setExclusions(eigenOptions.getExclusions() == null ? null
//        : new ArrayList<>(eigenOptions.getExclusions()));
    rulesConfig.setExclusions(null);
    rulesConfig.setIgnoreNodeSet(eigenOptions.getIgnoreNodes());
  }

  private static void configToLower(RulesConfig rulesConfig) {
//    rulesConfig.setExclusions(FieldToLowerUtil.listListToLower(rulesConfig.getExclusions()));
    rulesConfig.setExclusions(null);
    rulesConfig.setIgnoreNodeSet(FieldToLowerUtil.setToLower(rulesConfig.getIgnoreNodeSet()));
  }


}
