package com.arextest.diff.compare;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.register.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.exception.FindErrorException;
import com.arextest.diff.model.script.ScriptCompareConfig.ScriptMethod;
import com.arextest.diff.model.script.ScriptMethodContext;
import com.arextest.diff.model.script.ScriptSandbox;
import com.arextest.diff.utils.JacksonHelperUtil;
import java.util.List;
import javax.script.ScriptException;

public class ScriptCompare {

  public static boolean isScriptComparisonRequired(List<String> fuzzyPath,
      CompareContext compareContext) {
    return compareContext.scriptCompareConfigMap != null
        && compareContext.scriptCompareConfigMap.containsKey(fuzzyPath);
  }

  // custom compare
  public static void scriptCompare(Object obj1, Object obj2, List<String> fuzzyPath,
      CompareContext compareContext)
      throws ScriptException, NoSuchMethodException, FindErrorException {

    ScriptMethodContext context = new ScriptMethodContext();
    context.setBasePath(compareContext.currentNodeLeft);
    context.setTestPath(compareContext.currentNodeRight);

    Object baseValue = JacksonHelperUtil.objectMapper.convertValue(obj1, Object.class);
    Object testValue = JacksonHelperUtil.objectMapper.convertValue(obj2, Object.class);
    ScriptSandbox scriptSandbox = compareContext.scriptSandbox;
    ScriptMethod scriptMethod = compareContext.scriptCompareConfigMap.get(fuzzyPath);
    Boolean result = scriptSandbox.invoke(context, baseValue, testValue, scriptMethod);
    if (result) {
      return;
    }
    LogRegister.register(obj1, obj2, LogMarker.VALUE_DIFF, compareContext);
  }

}
