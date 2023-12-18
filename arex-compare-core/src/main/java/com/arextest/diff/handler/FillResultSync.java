package com.arextest.diff.handler;

import com.arextest.diff.factory.TaskThreadFactory;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class FillResultSync {

  public List<Future<String>> fillResult(MsgObjCombination response) {
    List<Future<String>> list = new ArrayList<>();
    Future<String> submit1 = TaskThreadFactory.jsonObjectThreadPool.submit(
        () -> getJsonString(response.getBaseObj()));

    Future<String> submit2 = TaskThreadFactory.jsonObjectThreadPool.submit(
        () -> getJsonString(response.getTestObj()));

    list.add(submit1);
    list.add(submit2);
    return list;
  }

  private String getJsonString(Object obj) throws JsonProcessingException {
    return obj == null
        ? null
        : JacksonHelperUtil.objectMapper.writeValueAsString(obj);
  }


}
