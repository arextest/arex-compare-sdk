package com.arextest.diff.model;

import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.enumeration.MsgMissingCode;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.UnmatchedPairEntity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class CompareBuilder {
    /**
     * Compare status codes{@link DiffResultCode}
     */
    private int code;

    // status information
    private String message;

    private MsgInfo msgInfo;

    // inconsistent record
    private List<LogEntity> logs;

    private String processedBaseMsg;

    private String processedTestMsg;

    // parsed node path and original value
    private Map<String, List<String>> parseNodePaths;

    public CompareBuilder code(int code) {
        this.code = code;
        return this;
    }

    public CompareBuilder msgInfo(String baseMsg, String testMsg) {
        if (this.msgInfo == null) {
            this.msgInfo = new MsgInfo();
        }

        if (baseMsg == null && testMsg == null) {
            msgInfo.setMsgMiss(MsgMissingCode.ALL_MISSING);
        } else if (baseMsg == null) {
            msgInfo.setMsgMiss(MsgMissingCode.LEFT_MISSING);
        } else if (testMsg == null) {
            msgInfo.setMsgMiss(MsgMissingCode.RIGHT_MISSING);
        } else {
            msgInfo.setMsgMiss(MsgMissingCode.NO_MISSING);
        }
        return this;
    }

    public CompareBuilder message(String message) {
        this.message = message;
        return this;
    }

    public CompareBuilder logs(List<LogEntity> logs) {
        this.logs = logs;
        return this;
    }

    public CompareBuilder processedBaseMsg(String processedBaseMsg) {
        this.processedBaseMsg = processedBaseMsg;
        return this;
    }

    public CompareBuilder processedTestMsg(String processedTestMsg) {
        this.processedTestMsg = processedTestMsg;
        return this;
    }

    public CompareBuilder parseNodePaths(Map<String, List<String>> parseNodePaths) {
        this.parseNodePaths = parseNodePaths;
        return this;
    }

    public CompareBuilder noDiff(String baseMsg, String testMsg) {
        this.code(DiffResultCode.COMPARED_WITHOUT_DIFFERENCE);
        this.message("compare successfully");
        this.msgInfo(baseMsg, testMsg);
        this.processedBaseMsg(baseMsg);
        this.processedTestMsg(testMsg);
        return this;
    }

    public CompareBuilder addStringUnMatched(String baseMsg, String testMsg) {
        this.code(DiffResultCode.COMPARED_WITH_DIFFERENCE);
        this.message("compare successfully");
        this.msgInfo(baseMsg, testMsg);
        this.processedBaseMsg(baseMsg);
        this.processedTestMsg(testMsg);

        LogEntity logEntity = new LogEntity();
        logEntity.setBaseValue(baseMsg);
        logEntity.setTestValue(testMsg);
        UnmatchedPairEntity pairEntity = new UnmatchedPairEntity();
        pairEntity.setUnmatchedType(UnmatchedType.UNMATCHED);
        logEntity.setPathPair(pairEntity);
        this.logs(Collections.singletonList(logEntity));
        return this;
    }

    public CompareBuilder exception(String baseMsg, String testMsg, Exception e) {
        String remark = exceptionToString(e);
        this.code(DiffResultCode.COMPARED_INTERNAL_EXCEPTION);
        this.message(remark);
        this.msgInfo(baseMsg, testMsg);
        this.processedBaseMsg(baseMsg);
        this.processedTestMsg(testMsg);

        LogEntity logEntity = new LogEntity();
        logEntity.setLogInfo(remark);
        UnmatchedPairEntity pairEntity = new UnmatchedPairEntity();
        pairEntity.setUnmatchedType(UnmatchedType.NA);
        logEntity.setPathPair(pairEntity);
        this.logs(Collections.singletonList(logEntity));
        return this;
    }

    public CompareBuilder exception(String baseMsg, String testMsg, String remark) {
        this.code(DiffResultCode.COMPARED_INTERNAL_EXCEPTION);
        this.message(remark);
        this.msgInfo(baseMsg, testMsg);
        this.processedBaseMsg(baseMsg);
        this.processedTestMsg(testMsg);

        LogEntity logEntity = new LogEntity();
        logEntity.setLogInfo(remark);
        UnmatchedPairEntity pairEntity = new UnmatchedPairEntity();
        pairEntity.setUnmatchedType(UnmatchedType.NA);
        logEntity.setPathPair(pairEntity);
        this.logs(Collections.singletonList(logEntity));
        return this;
    }

    public CompareBuilder addFindErrorException(String baseMsg, String testMsg,
                                                List<Future<String>> processedMsgList, Exception e) {
        String processedBaseMsg;
        String processedTestMsg;
        try {
            processedBaseMsg = processedMsgList.get(0).get();
            processedTestMsg = processedMsgList.get(1).get();
        } catch (Exception exception) {
            return CompareResult.builder().exception(baseMsg, testMsg, e);
        }

        return CompareResult.builder()
                .code(DiffResultCode.COMPARED_WITH_DIFFERENCE)
                .message("compare successfully")
                .msgInfo(baseMsg, testMsg)
                .processedBaseMsg(processedBaseMsg)
                .processedTestMsg(processedTestMsg);
    }

    private String exceptionToString(Throwable e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        } catch (Throwable e1) {
        }
        return e.getMessage();
    }

    public CompareResult build() {
        return new CompareResult(code, message, msgInfo, logs, processedBaseMsg, processedTestMsg, parseNodePaths);
    }

}