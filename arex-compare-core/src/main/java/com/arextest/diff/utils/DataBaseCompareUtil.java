package com.arextest.diff.utils;

import com.arextest.diff.handler.CompareHandler;
import com.arextest.diff.handler.FillResultSync;
import com.arextest.diff.handler.WhitelistHandler;
import com.arextest.diff.handler.keycompute.KeyCompute;
import com.arextest.diff.handler.log.LogProcess;
import com.arextest.diff.handler.log.filterrules.ArexPrefixFilter;
import com.arextest.diff.handler.log.filterrules.IPFilter;
import com.arextest.diff.handler.log.filterrules.OnlyCompareSameColumnsFilter;
import com.arextest.diff.handler.log.filterrules.TimePrecisionFilter;
import com.arextest.diff.handler.log.filterrules.UuidFilter;
import com.arextest.diff.handler.metric.TimeConsumerWatch;
import com.arextest.diff.handler.metric.TimeMetricLabel;
import com.arextest.diff.handler.parse.JSONParse;
import com.arextest.diff.handler.parse.JSONStructureParse;
import com.arextest.diff.handler.parse.ObjectParse;
import com.arextest.diff.handler.parse.sqlparse.SqlParse;
import com.arextest.diff.handler.verify.VerifyObjectParse;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.exception.FindErrorException;
import com.arextest.diff.model.exception.SelectIgnoreException;
import com.arextest.diff.model.key.KeyComputeResponse;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.parse.MsgObjCombination;
import com.arextest.diff.model.parse.MsgStructure;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.apache.commons.lang3.tuple.MutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rchen9 on 2023/1/30.
 */
public class DataBaseCompareUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseCompareUtil.class);

  private static ObjectParse objectParse = new ObjectParse();

  private static VerifyObjectParse verifyObjectParse = new VerifyObjectParse();

  private static SqlParse sqlParse = new SqlParse();

  private static JSONParse jsonParse = new JSONParse();

  private static FillResultSync fillResultSync = new FillResultSync();

  private static WhitelistHandler whitelistHandler = new WhitelistHandler();

  private static KeyCompute keyCompute = new KeyCompute();

  private static CompareHandler compareHandler = new CompareHandler();

  private static JSONStructureParse jsonStructureParse = new JSONStructureParse();

  public CompareResult jsonCompare(RulesConfig rulesConfig) {

    CompareResult result = null;
    String baseMsg = rulesConfig.getBaseMsg();
    String testMsg = rulesConfig.getTestMsg();

    TimeConsumerWatch timeConsumerWatch = new TimeConsumerWatch();
    timeConsumerWatch.start(TimeMetricLabel.TOTAL);

    // Convert basMsg and testMsg to JSONObject
    MsgObjCombination msgObjCombination = null;
    try {
      timeConsumerWatch.start(TimeMetricLabel.OBJECT_PARSE);
      msgObjCombination = objectParse.doHandler(rulesConfig);
      timeConsumerWatch.end(TimeMetricLabel.OBJECT_PARSE);

      // verify parse results
      boolean verifyResult = verifyObjectParse.verify(msgObjCombination);
      if (!verifyResult) {
        result = CompareResult.builder()
            .addEqualsCompare(msgObjCombination.getBaseObj(), msgObjCombination.getTestObj(),
                rulesConfig)
            .build();
        timeConsumerWatch.end(TimeMetricLabel.TOTAL);
        timeConsumerWatch.record(result);
        return result;
      }
    } catch (Exception e) {
      result = CompareResult.builder().addStringUnMatched(baseMsg, testMsg).build();
      timeConsumerWatch.end(TimeMetricLabel.TOTAL);
      timeConsumerWatch.record(result);
      return result;
    }

    List<LogEntity> logs = null;
    List<Future<String>> processedMsgList = null;
    try {

      // Parse string and compressed fields in JSONObject
      timeConsumerWatch.start(TimeMetricLabel.JSON_PARSE);
      Map<String, List<String>> parsePaths =
          jsonParse.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
              msgObjCombination.getTestObj());
      // If enables sql parsing, fill in the field of "parsedsql"
      sqlParse.doHandler(msgObjCombination, rulesConfig.isOnlyCompareCoincidentColumn(),
          rulesConfig.isSelectIgnoreCompare(), rulesConfig.isNameToLower());
      timeConsumerWatch.end(TimeMetricLabel.JSON_PARSE);

      // Parse JSON structure
      CompletableFuture<MutablePair<MsgStructure, MsgStructure>> msgStructureFuture =
          jsonStructureParse.doHandler(msgObjCombination.getBaseObj(),
              msgObjCombination.getTestObj());

      // Backfill the parsed message to result
      processedMsgList = fillResultSync.fillResult(msgObjCombination);

      // compute listKey
      timeConsumerWatch.start(TimeMetricLabel.KEY_COMPUTE);
      KeyComputeResponse keyComputeResponse =
          keyCompute.doHandler(rulesConfig, msgObjCombination.getBaseObj(),
              msgObjCombination.getTestObj());
      timeConsumerWatch.end(TimeMetricLabel.KEY_COMPUTE);

      // process whiteList
      timeConsumerWatch.start(TimeMetricLabel.WHITE_LIST);
      MsgObjCombination msgWhiteObj = whitelistHandler.doHandler(msgObjCombination.getBaseObj(),
          msgObjCombination.getTestObj(), rulesConfig.getInclusions());
      timeConsumerWatch.end(TimeMetricLabel.WHITE_LIST);

      // compare jsonObject
      timeConsumerWatch.start(TimeMetricLabel.COMPARE_HANDLER);
      LogProcess logProcess = new LogProcess();
      logProcess.setRulesConfig(rulesConfig);
      logProcess.appendFilterRules(
          Arrays.asList(new TimePrecisionFilter(rulesConfig.getIgnoredTimePrecision()),
              new ArexPrefixFilter()));
      if (rulesConfig.isUuidIgnore()) {
        logProcess.appendFilterRules(new UuidFilter());
      }
      if (rulesConfig.isOnlyCompareCoincidentColumn()) {
        logProcess.appendFilterRules(new OnlyCompareSameColumnsFilter());
      }
      logProcess.appendFilterRules(Collections.singletonList(new IPFilter()));
      logs = compareHandler.doHandler(rulesConfig, keyComputeResponse, msgStructureFuture,
          msgWhiteObj.getBaseObj(), msgWhiteObj.getTestObj(), logProcess);
      timeConsumerWatch.end(TimeMetricLabel.COMPARE_HANDLER);

      // get processed msg
      String processedBaseMsg = processedMsgList.get(0).get();
      String processedTestMsg = processedMsgList.get(1).get();
      result = CompareResult.builder()
          .code(ListUti.isEmpty(logs) ? DiffResultCode.COMPARED_WITHOUT_DIFFERENCE
              : DiffResultCode.COMPARED_WITH_DIFFERENCE)
          .message("compare successfully").msgInfo(baseMsg, testMsg).logs(logs)
          .processedBaseMsg(rulesConfig.isQuickCompare() ? baseMsg : processedBaseMsg)
          .processedTestMsg(rulesConfig.isQuickCompare() ? testMsg : processedTestMsg)
          .parseNodePaths(parsePaths)
          .build();

    } catch (SelectIgnoreException e) {
      result = CompareResult.builder().noDiff(baseMsg, testMsg).build();
    } catch (FindErrorException e) {
      result = CompareResult.builder()
          .addFindErrorException(baseMsg, testMsg, processedMsgList, e, rulesConfig)
          .build();
    } catch (Exception e) {
      LOGGER.error("compare error, exception:", e);
      result = CompareResult.builder().exception(baseMsg, testMsg, e).build();
    } finally {
      timeConsumerWatch.end(TimeMetricLabel.TOTAL);
      timeConsumerWatch.record(result);
    }
    return result;
  }

}
