package com.arextest.diff.handler.log;

import com.arextest.diff.handler.log.filterrules.UnmatchedTypeFilter;
import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.LogTagAddResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.arextest.diff.utils.LogHandler.processInConsistentPaths;

public class LogTagAdd {

    private static List<Predicate<LogEntity>> globalFilterRules = new ArrayList<Predicate<LogEntity>>() {{
        add(new UnmatchedTypeFilter());
    }};

    public LogTagAddResponse addTagInLog(List<LogEntity> logEntities, List<Predicate<LogEntity>> filterRules) {

        Stream<LogEntity> stream = logEntities.stream();
        for (Predicate<LogEntity> filterRule : globalFilterRules) {
            stream = stream.filter(filterRule);
        }
        for (Predicate<LogEntity> filterRule : filterRules) {
            stream = stream.filter(filterRule);
        }

        Set<String> inConsistentPaths = new HashSet<>();
        List<LogEntity> filterLogs = new ArrayList<>();

        stream.forEach(item -> {
            processInConsistentPaths(item, inConsistentPaths);
            filterLogs.add(item);
        });

        return new LogTagAddResponse(inConsistentPaths,
                !filterLogs.isEmpty() ? DiffResultCode.COMPARED_WITH_DIFFERENCE : DiffResultCode.COMPARED_WITHOUT_DIFFERENCE,
                filterLogs);
    }

}
