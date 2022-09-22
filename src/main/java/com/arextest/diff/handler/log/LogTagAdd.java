package com.arextest.diff.handler.log;

import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.LogTagAddResponse;
import com.arextest.diff.model.log.UnmatchedPairEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.arextest.diff.utils.LogHandler.processInConsistentPaths;

public class LogTagAdd {

    private static Set<Integer> filterCondition = new HashSet<Integer>() {{
        add(UnmatchedType.LEFT_MISSING);
        add(UnmatchedType.RIGHT_MISSING);
        add(UnmatchedType.UNMATCHED);
        // add(UnmatchedType.DIFFERENT_TYPE);
        // add(UnmatchedType.DIFFERENT_COUNT);
    }};

    public LogTagAddResponse addTagInLog(List<LogEntity> logEntities) {

        Set<String> inConsistentPaths = new HashSet<>();
        int existDiff = DiffResultCode.COMPARED_WITHOUT_DIFFERENCE;
        List<LogEntity> filterLogs = new ArrayList<>();

        for (LogEntity log : logEntities) {
            int unmatchedType = log.getPathPair().getUnmatchedType();

            if (filterCondition.contains(unmatchedType)) {
                processInConsistentPaths(log, inConsistentPaths);

                if (unmatchedType != UnmatchedType.DIFFERENT_COUNT) {
                    existDiff = DiffResultCode.COMPARED_WITH_DIFFERENCE;
                }
                filterLogs.add(log);
            }
        }
        return new LogTagAddResponse(inConsistentPaths, existDiff, filterLogs);
    }
}
