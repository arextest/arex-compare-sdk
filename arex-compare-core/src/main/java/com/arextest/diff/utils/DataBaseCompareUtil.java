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
import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.exception.FindErrorException;
import com.arextest.diff.model.exception.SelectIgnoreException;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.model.parse.MsgStructure;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

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

    private static JSONStructureParse jsonStructureParse = new JSONStructureParse();

    public CompareResult jsonCompare(RulesConfig rulesConfig) {

        // CompareResult result = new CompareResult();
        String baseMsg = rulesConfig.getBaseMsg();
        String testMsg = rulesConfig.getTestMsg();


        // Convert basMsg and testMsg to JSONObject
        MsgObjCombination msgObjCombination = null;
        try {
            msgObjCombination = objectParse.doHandler(rulesConfig);
        } catch (Exception e) {
            return CompareResult.builder().addStringUnMatched(baseMsg, testMsg).build();
        }


        List<LogEntity> logs = null;
        List<Future<String>> processedMsgList = null;
        try {

            // Parse string and compressed fields in JSONObject
            Map<String, List<String>> parsePaths = jsonParse.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj());
            // If enables sql parsing, fill in the field of "parsedsql"
            sqlParse.doHandler(msgObjCombination, rulesConfig.isOnlyCompareCoincidentColumn(),
                    rulesConfig.isSelectIgnoreCompare());

            // Parse JSON structure
            CompletableFuture<MutablePair<MsgStructure, MsgStructure>> msgStructureFuture =
                    jsonStructureParse.doHandler(msgObjCombination.getBaseObj(), msgObjCombination.getTestObj());

            // Backfill the parsed message to result
            processedMsgList = fillResultSync.fillResult(msgObjCombination);

            // compute listKey
            KeyComputeResponse keyComputeResponse = keyCompute.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj());

            // process whiteList
            MsgObjCombination msgWhiteObj = whitelistHandler.doHandler(msgObjCombination.getBaseObj(),
                    msgObjCombination.getTestObj(), rulesConfig.getInclusions());


            // compare jsonObject
            LogProcess logProcess = new LogProcess();
            logProcess.setRulesConfig(rulesConfig);
            logProcess.appendFilterRules(Arrays.asList(
                    new TimePrecisionFilter(rulesConfig.getIgnoredTimePrecision()),
                    new ArexPrefixFilter(),
                    new GuidFilter()
            ));
            if (rulesConfig.isOnlyCompareCoincidentColumn()) {
                logProcess.appendFilterRules(new OnlyCompareSameColumnsFilter());
            }
            logs = compareHandler.doHandler(rulesConfig, keyComputeResponse, msgStructureFuture,
                    msgWhiteObj.getBaseObj(), msgWhiteObj.getTestObj(), logProcess);

            // get processed msg
            String processedBaseMsg = processedMsgList.get(0).get();
            String processedTestMsg = processedMsgList.get(1).get();
            return CompareResult.builder()
                    .code(ListUti.isEmpty(logs) ? DiffResultCode.COMPARED_WITHOUT_DIFFERENCE :
                            DiffResultCode.COMPARED_WITH_DIFFERENCE)
                    .message("compare successfully")
                    .msgInfo(baseMsg, testMsg)
                    .logs(logs)
                    .processedBaseMsg(rulesConfig.isQuickCompare() ? baseMsg : processedBaseMsg)
                    .processedTestMsg(rulesConfig.isQuickCompare() ? testMsg : processedTestMsg)
                    .parseNodePaths(parsePaths)
                    .build();


        } catch (SelectIgnoreException e) {
            return CompareResult.builder().noDiff(baseMsg, testMsg).build();
        } catch (FindErrorException e) {
            return CompareResult.builder().addFindErrorException(baseMsg, testMsg, processedMsgList, e).build();
        } catch (Exception e) {
            return CompareResult.builder().exception(baseMsg, testMsg, e).build();
        }
    }

}
