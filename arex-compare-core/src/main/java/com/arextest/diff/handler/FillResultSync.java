package com.arextest.diff.handler;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.JacksonHelperUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FillResultSync {

  public List<Future<String>> fillResult(MsgObjCombination response) {
    List<Future<String>> list = new ArrayList<>();
    Future<String> submit1 = TaskThreadFactory.jsonObjectThreadPool.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return response.getBaseObj() == null
            ? null
            : JacksonHelperUtil.objectMapper.writeValueAsString(response.getBaseObj());
      }
    });

    Future<String> submit2 = TaskThreadFactory.jsonObjectThreadPool.submit(new Callable<String>() {
      @Override
      public String call() throws Exception {
        return response.getTestObj() == null
            ? null
            : JacksonHelperUtil.objectMapper.writeValueAsString(response.getTestObj());
      }
    });
    list.add(submit1);
    list.add(submit2);
    return list;
  }
}
