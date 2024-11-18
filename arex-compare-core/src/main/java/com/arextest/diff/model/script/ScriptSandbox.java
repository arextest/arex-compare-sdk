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
    if (scriptContentInfo.getFunctionName() != null && !scriptContentInfo.getFunctionName()
        .isEmpty()) {
      compareScripts.put(scriptContentInfo.getFunctionName(), scriptContentInfo);
    }
  }

  public Boolean invoke(ScriptMethodContext context, Object obj1, Object obj2,
      ScriptMethod scriptMethod)
      throws ScriptException, NoSuchMethodException {

    String functionName = scriptMethod.getFunctionName();
    String functionArgs = scriptMethod.getFunctionArgs();
    if (!loadedScripts.contains(functionName)) {
      ScriptContentInfo contentInfo = compareScripts.get(functionName);
      if (contentInfo == null || contentInfo.getScriptContent() == null
          || contentInfo.getScriptContent().isEmpty()) {
        throw new IllegalArgumentException("Script content is empty for method: " + functionName);
      }
      sandbox.eval(contentInfo.getScriptContent());
      loadedScripts.add(functionName);
    }
    Invocable invocable = sandbox.getSandboxedInvocable();
    Object result = invocable.invokeFunction(functionName, context, obj1, obj2, functionArgs);
    if (result instanceof Boolean) {
      return (Boolean) result;
    }
    return false;
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
