package io.arex.diff.handler;

import io.arex.diff.model.enumeration.Constant;
import io.arex.diff.model.enumeration.DiffResultCode;
import io.arex.diff.model.enumeration.UnmatchedType;
import io.arex.diff.model.log.*;
import io.arex.diff.utils.ListUti;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.arex.diff.utils.LogHandler.processInConsistentPaths;

public class LogTagAdd {

    private static Set<Integer> filterCondition = new HashSet<Integer>() {{
        add(UnmatchedType.LEFT_MISSING);
        add(UnmatchedType.RIGHT_MISSING);
        add(UnmatchedType.UNMATCHED);
        // add(UnmatchedType.DIFFERENT_COUNT);
    }};

    private void ignoreTagProcessor(List<NodeEntity> unmatchedPath, List<List<String>> ignoreNodePaths, LogTag logTag) {
        List<String> unmatchedPathList = ListUti.convertToStringList(unmatchedPath);
        if (ignoreNodePaths != null && !ignoreNodePaths.isEmpty()) {
            for (List<String> ignoreNodePath : ignoreNodePaths) {
                if (ignoreMatch(unmatchedPathList, ignoreNodePath)) {
                    logTag.setIg(true);
                    return;
                }
            }
        }

    }

    private boolean ignoreMatch(List<String> pathInList, List<String> ignoreNodePath) {

        int size = ignoreNodePath.size();

        if (ignoreNodePath.size() > pathInList.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!ignoreNodePath.get(i).equalsIgnoreCase(pathInList.get(i)) && !ignoreNodePath.get(i).equals(Constant.DYNAMIC_PATH)) {
                return false;
            }
        }
        return true;
    }

    public LogTagAddResponse addTagInLog(LogTagAddRequest request) {

        Set<String> inConsistentPaths = new HashSet<>();
        int existDiff = DiffResultCode.COMPARED_WITHOUT_DIFFERENCE;
        List<LogEntity> filterLogs = new ArrayList<>();

        for (LogEntity log : request.getLogs()) {
            int unmatchedType = log.getPathPair().getUnmatchedType();

            if (filterCondition.contains(unmatchedType)) {
                UnmatchedPairEntity pathPair = log.getPathPair();
                LogTag logTag = log.getLogTag();
                List<NodeEntity> unmatchedPath = new ArrayList<>();
                if (pathPair.getLeftUnmatchedPath().size() >= pathPair.getRightUnmatchedPath().size()) {
                    unmatchedPath = pathPair.getLeftUnmatchedPath();
                } else {
                    unmatchedPath = pathPair.getRightUnmatchedPath();
                }
                logTag.setLv(unmatchedPath.size());
                ignoreTagProcessor(unmatchedPath, request.getIgnoreNodePaths(), logTag);
                processInConsistentPaths(log, inConsistentPaths);

                if (unmatchedType != UnmatchedType.DIFFERENT_COUNT && Boolean.FALSE.equals(log.getLogTag().getIg())) {
                    existDiff = DiffResultCode.COMPARED_WITH_DIFFERENCE;
                }
                filterLogs.add(log);
            }
        }
        return new LogTagAddResponse(inConsistentPaths, existDiff, filterLogs);
    }
}
