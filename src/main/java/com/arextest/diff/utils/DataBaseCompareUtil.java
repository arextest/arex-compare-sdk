package com.arextest.diff.utils;

import com.arextest.diff.handler.CompareHandler;
import com.arextest.diff.handler.FillResultSync;
import com.arextest.diff.handler.WhitelistHandler;
import com.arextest.diff.handler.keycompute.KeyCompute;
import com.arextest.diff.handler.log.LogProcess;
import com.arextest.diff.handler.log.filterrules.ArexPrefixFilter;
import com.arextest.diff.handler.log.filterrules.GuidFilter;
import com.arextest.diff.handler.log.filterrules.OnlyCompareSameColumnsFilter;
import com.arextest.diff.handler.log.filterrules.TimePrecisionFilter;
import com.arextest.diff.handler.parse.JSONParse;
import com.arextest.diff.handler.parse.JSONStructureParse;
import com.arextest.diff.handler.parse.ObjectParse;
import com.arextest.diff.handler.parse.sqlparse.SqlParse;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.LogProcessResponse;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.model.parse.MsgStructure;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/1/30.
 */
public class DataBaseCompareUtil {

    private static ObjectParse objectParse = new ObjectParse();

    private static SqlParse sqlParse = new SqlParse();

    private static JSONParse jsonParse = new JSONParse();

    private static FillResultSync fillResultSync = new FillResultSync();

    private static WhitelistHandler whitelistHandler = new WhitelistHandler();

    private static KeyCompute keyCompute = new KeyCompute();

    private static CompareHandler compareHandler = new CompareHandler();

    private static LogProcess logProcess = new LogProcess();

    private static JSONStructureParse jsonStructureParse = new JSONStructureParse();

    public static CompareResult jsonCompare(RulesConfig rulesConfig) {

        CompareResult result = new CompareResult();

        try {
            // Convert basMsg and testMsg to JSONObject
            MsgObjCombination msgObjCombination = null;
            try {
                msgObjCombination = objectParse.doHandler(rulesConfig);
            } catch (Exception e) {
                return ExceptionToCompareResult.addUnMatchedException(rulesConfig.getBaseMsg(), rulesConfig.getTestMsg());
            }

            // Parse string and compressed fields in JSONObject
            Map<String, List<String>> parsePaths = jsonParse.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj());
            // If enables sql parsing, fill in the field of "parsedsql"
            if (rulesConfig.isSqlBodyParse()) {
                sqlParse.doHandler(msgObjCombination, rulesConfig.isOnlyCompareCoincidentColumn());
            }

            // Parse JSON structure
            CompletableFuture<MutablePair<MsgStructure, MsgStructure>> msgStructureFuture =
                    jsonStructureParse.doHandler(msgObjCombination.getBaseObj(), msgObjCombination.getTestObj());

            // Backfill the parsed message to result
            List<Future<String>> list = fillResultSync.fillResult(msgObjCombination);

            // compute listKey
            KeyComputeResponse keyComputeResponse = keyCompute.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj());

            // process whiteList
            MsgObjCombination msgWhiteObj = whitelistHandler.doHandler(msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj(), rulesConfig.getInclusions());

            // compare jsonObject
            List<LogEntity> logs = compareHandler.doHandler(rulesConfig, keyComputeResponse, msgStructureFuture,
                    msgWhiteObj.getBaseObj(), msgWhiteObj.getTestObj());

            // process logEntity
            List<Predicate<LogEntity>> logFilterRules = new ArrayList<Predicate<LogEntity>>() {{
                add(new TimePrecisionFilter(rulesConfig.getIgnoredTimePrecision()));
                add(new ArexPrefixFilter());
                add(new GuidFilter());
            }};
            if (rulesConfig.isOnlyCompareCoincidentColumn()) {
                logFilterRules.add(new OnlyCompareSameColumnsFilter());
            }
            LogProcessResponse logProcessResponse = logProcess.process(
                    logs,
                    logFilterRules
            );

            result.setCode(logProcessResponse.getExistDiff());
            result.setMessage("compare successfully");
            result.setLogs(logProcessResponse.getLogs());
            result.setProcessedBaseMsg(list.get(0).get());
            result.setProcessedTestMsg(list.get(1).get());
            result.setInConsistentPaths(logProcessResponse.getInConsistentPaths());
            result.setParseNodePaths(parsePaths);

        } catch (Exception e) {
            return ExceptionToCompareResult.fromException(rulesConfig.getBaseMsg(), rulesConfig.getTestMsg(), ExceptionToCompareResult.exceptionToString(e));
        }

        return result;
    }

}
