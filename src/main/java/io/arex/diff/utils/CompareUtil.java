package io.arex.diff.utils;

import io.arex.diff.handler.CompareHandler;
import io.arex.diff.handler.FillResultSync;
import io.arex.diff.handler.LogTagAdd;
import io.arex.diff.handler.WhitelistHandler;
import io.arex.diff.handler.keycompute.KeyCompute;
import io.arex.diff.handler.parse.JSONParse;
import io.arex.diff.handler.parse.ObjectParse;
import io.arex.diff.model.CompareResult;
import io.arex.diff.model.RulesConfig;
import io.arex.diff.model.enumeration.DiffResultCode;
import io.arex.diff.model.enumeration.UnmatchedType;
import io.arex.diff.model.key.KeyComputeResponse;
import io.arex.diff.model.log.LogEntity;
import io.arex.diff.model.log.LogTagAddRequest;
import io.arex.diff.model.log.LogTagAddResponse;
import io.arex.diff.model.log.UnmatchedPairEntity;
import io.arex.diff.model.parse.MsgObjCombination;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class CompareUtil {

    private static ObjectParse objectParse = new ObjectParse();

    private static JSONParse jsonParse = new JSONParse();

    private static FillResultSync fillResultSync = new FillResultSync();

    private static WhitelistHandler whitelistHandler = new WhitelistHandler();

    private static KeyCompute keyCompute = new KeyCompute();

    private static CompareHandler compareHandler = new CompareHandler();

    private static LogTagAdd logTagAdd = new LogTagAdd();

    public static CompareResult jsonCompare(RulesConfig rulesConfig) {

        CompareResult result = new CompareResult();

        try {
            // Convert basMsg and testMsg to JSONObject
            MsgObjCombination msgObjCombination = null;
            try {
                msgObjCombination = objectParse.doHandler(rulesConfig);
            } catch (Exception e) {
                return addUnMatchedException(rulesConfig.getBaseMsg(), rulesConfig.getTestMsg());
            }

            // Parse string and compressed fields in JSONObject
            Map<String, List<String>> parsePaths = jsonParse.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj());

            // Backfill the parsed message to result
            List<Future<String>> list = fillResultSync.fillResult(msgObjCombination);

            // compute listKey
            KeyComputeResponse keyComputeResponse = keyCompute.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj());

            // process whiteList
            MsgObjCombination msgWhiteObj = whitelistHandler.doHandler(msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj(), rulesConfig.getInclusions());

            // compare jsonObject
            List<LogEntity> logs = compareHandler.doHandler(rulesConfig, keyComputeResponse, msgWhiteObj.getBaseObj(),
                    msgWhiteObj.getTestObj());

            LogTagAddRequest logTagAddRequest = new LogTagAddRequest(logs, rulesConfig.getExclusions());
            LogTagAddResponse logTagAddResponse = logTagAdd.addTagInLog(logTagAddRequest);

            result.setCode(logTagAddResponse.getExistDiff());
            result.setMessage("compare successfully");
            result.setLogs(logTagAddResponse.getLogs());
            result.setProcessedBaseMsg(list.get(0).get());
            result.setProcessedTestMsg(list.get(1).get());
            result.setInConsistentPaths(logTagAddResponse.getInConsistentPaths());
            result.setParseNodePaths(parsePaths);

        } catch (Exception e) {
            return fromException(rulesConfig.getBaseMsg(), rulesConfig.getTestMsg(), exceptionToString(e));
        }

        return result;
    }

    public static CompareResult fromException(String baseMsg, String testMsg, String remark) {
        CompareResult result = new CompareResult();
        result.setCode(DiffResultCode.COMPARED_INTERNAL_EXCEPTION);
        result.setMessage(remark);
        result.setProcessedBaseMsg(baseMsg);
        result.setProcessedTestMsg(testMsg);
        List<LogEntity> logs = new ArrayList<>();
        LogEntity logEntity = new LogEntity();
        logEntity.setLogInfo(remark);
        UnmatchedPairEntity pairEntity = new UnmatchedPairEntity();
        pairEntity.setUnmatchedType(UnmatchedType.NA);
        logEntity.setPathPair(pairEntity);
        logs.add(logEntity);
        result.setLogs(logs);
        return result;
    }

    private static String exceptionToString(Throwable e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String errorMsg = sw.toString();
            return errorMsg;
        } catch (Throwable e1) {
        }
        return e.getMessage();
    }

    private static CompareResult addUnMatchedException(String baseMsg, String testMsg) {
        CompareResult result = new CompareResult();
        result.setCode(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        result.setMessage("compare successfully");
        result.setProcessedBaseMsg(baseMsg);
        result.setProcessedTestMsg(testMsg);
        List<LogEntity> logs = new ArrayList<>();
        LogEntity logEntity = new LogEntity();
        logEntity.setBaseValue(baseMsg);
        logEntity.setTestValue(testMsg);
        UnmatchedPairEntity pairEntity = new UnmatchedPairEntity();
        pairEntity.setUnmatchedType(UnmatchedType.UNMATCHED);
        logEntity.setPathPair(pairEntity);
        logs.add(logEntity);
        result.setLogs(logs);
        return result;
    }
}
