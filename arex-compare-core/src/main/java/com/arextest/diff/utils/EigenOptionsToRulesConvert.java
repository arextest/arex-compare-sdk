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
    rulesConfig.setExclusions(
        ExpressionNodeParser.doConvertNameNode(eigenOptions.getExclusions()));
    rulesConfig.setExpressionExclusions(
        ExpressionNodeParser.doParse(eigenOptions.getExclusions()));
    rulesConfig.setIgnoreNodeSet(eigenOptions.getIgnoreNodes());
  }

  private static void configToLower(RulesConfig rulesConfig) {
    rulesConfig.setExclusions(
        FieldToLowerUtil.expressionNodeListToLower(rulesConfig.getExclusions()));
    rulesConfig.setExpressionExclusions(
        FieldToLowerUtil.expressionNodeListToLower(rulesConfig.getExpressionExclusions()));
    rulesConfig.setIgnoreNodeSet(FieldToLowerUtil.setToLower(rulesConfig.getIgnoreNodeSet()));
  }
}
