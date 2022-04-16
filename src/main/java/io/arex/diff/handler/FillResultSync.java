package io.arex.diff.handler;

import io.arex.diff.factory.TaskThreadFactory;
import io.arex.diff.model.parse.MsgObjCombination;

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
                return response.getBaseObj() == null ? null : response.getBaseObj().toString();
            }
        });

        Future<String> submit2 = TaskThreadFactory.jsonObjectThreadPool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return response.getTestObj() == null ? null : response.getTestObj().toString();
            }
        });
        list.add(submit1);
        list.add(submit2);
        return list;
    }
}
