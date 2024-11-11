package com.arextest.diff.compare;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.register.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.enumeration.ParentNodeType;
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
    int result = scriptSandbox.invoke(context, baseValue, testValue, scriptMethod);
    if (result == ScriptCompareType.Matched) {
      return;
    }
    LogRegister.register(obj1, obj2,
        ScriptCompareType.convert(result, compareContext.parentNodeType), compareContext);
  }

  public interface ScriptCompareType {

    int Matched = 1;
    int Unmatched = 2;
    // the node of basic msg is missing
    int LEFT_MISSING = 3;
    // the node of test msg is missing
    int RIGHT_MISSING = 4;

    static LogMarker convert(int type, int parentType) {
      switch (type) {
        case Unmatched:
          return LogMarker.VALUE_DIFF;
        case LEFT_MISSING:
          return parentType == ParentNodeType.OBJECT ? LogMarker.LEFT_OBJECT_MISSING
              : LogMarker.LEFT_ARRAY_MISSING;
        case RIGHT_MISSING:
          return parentType == ParentNodeType.OBJECT ? LogMarker.RIGHT_OBJECT_MISSING
              : LogMarker.RIGHT_ARRAY_MISSING;
        default:
          return LogMarker.UNKNOWN;
      }

    }

  }


}
