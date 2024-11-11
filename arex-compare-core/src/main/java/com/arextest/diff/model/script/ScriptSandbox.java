package com.arextest.diff.model.script;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.script.ScriptCompareConfig.ScriptMethod;
import delight.nashornsandbox.NashornSandbox;
import delight.nashornsandbox.NashornSandboxes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.script.Invocable;
import javax.script.ScriptException;

public class ScriptSandbox {

  private NashornSandbox sandbox = createSandbox();

  public ScriptSandbox() {
  }

  private Map<String, ScriptContentInfo> compareScripts = new HashMap<>();

  private Set<String> loadedScripts = new HashSet<>();

  public NashornSandbox getSandbox() {
    return sandbox;
  }

  public void putCompareScript(ScriptContentInfo scriptContentInfo) {
    if (scriptContentInfo.getAliasName() != null && !scriptContentInfo.getAliasName().isEmpty()) {
      compareScripts.put(scriptContentInfo.getAliasName(), scriptContentInfo);
    }
    if (scriptContentInfo.getFunctionName() != null && !scriptContentInfo.getFunctionName()
        .isEmpty()) {
      compareScripts.put(scriptContentInfo.getFunctionName(), scriptContentInfo);
    }
  }

  public int invoke(ScriptMethodContext context, Object obj1, Object obj2,
      ScriptMethod scriptMethod)
      throws ScriptException, NoSuchMethodException {

    String methodName = scriptMethod.getMethodName();
    String methodArgs = scriptMethod.getMethodArgs();
    if (!loadedScripts.contains(methodName)) {
      ScriptContentInfo contentInfo = compareScripts.get(methodName);
      if (contentInfo == null || contentInfo.getScriptContent() == null
          || contentInfo.getScriptContent().isEmpty()) {
        throw new IllegalArgumentException("Script content is empty for method: " + methodName);
      }
      sandbox.eval(contentInfo.getScriptContent());
      loadedScripts.add(methodName);
    }
    String functionName = compareScripts.get(methodName).getFunctionName();
    Invocable invocable = sandbox.getSandboxedInvocable();
    return (Integer) invocable.invokeFunction(functionName, context, obj1, obj2, methodArgs);
  }

  private NashornSandbox createSandbox() {
    NashornSandbox nashornSandbox = NashornSandboxes.create();
    nashornSandbox.setMaxCPUTime(2 * 60 * 1000);
    nashornSandbox.setMaxMemory(512 * 1024 * 1024 * 8L);
    nashornSandbox.setMaxPreparedStatements(50);
    nashornSandbox.setExecutor(TaskThreadFactory.jsEvalThreadPool);
    return nashornSandbox;
  }

}
