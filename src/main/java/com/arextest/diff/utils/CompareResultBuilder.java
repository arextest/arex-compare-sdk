package com.arextest.diff.utils;

import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.UnmatchedPairEntity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rchen9 on 2023/1/30.
 */
public class CompareResultBuilder {

    public static CompareResult noError(String baseMsg, String testMsg) {
        CompareResult result = new CompareResult();
        result.setCode(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE);
        result.setMessage("compare successfully");
        result.setProcessedBaseMsg(baseMsg);
        result.setProcessedTestMsg(testMsg);
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

    public static String exceptionToString(Throwable e) {
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

    public static CompareResult addUnMatchedException(String baseMsg, String testMsg) {
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
