package com.arextest.diff.handler.log;

import com.arextest.diff.factory.PluginServiceFactory;
import com.arextest.diff.handler.log.filterrules.UnmatchedTypeFilter;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.enumeration.DiffResultCode;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.LogProcessResponse;
import com.arextest.diff.plugin.LogEntityFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogProcess {

    private static List<Predicate<LogEntity>> globalFilterRules = new ArrayList<Predicate<LogEntity>>() {{
        add(new UnmatchedTypeFilter());
    }};

    public LogProcessResponse process(List<LogEntity> logEntities, List<Predicate<LogEntity>> filterRules, RulesConfig rulesConfig) {

        Stream<LogEntity> stream = logEntities.stream();
        for (Predicate<LogEntity> filterRule : globalFilterRules) {
            stream = stream.filter(filterRule);
        }
        for (Predicate<LogEntity> filterRule : filterRules) {
            stream = stream.filter(filterRule);
        }

        for (LogEntityFilter logEntityFilter : PluginServiceFactory.getLogEntityFilterList()) {
            stream = stream.filter(item -> {
                try {
                    return !logEntityFilter.isIgnore(item, rulesConfig);
                } catch (Throwable e) {
                }
                return true;
            });
        }

        List<LogEntity> filterLogs = stream.collect(Collectors.toList());
        return new LogProcessResponse(
                filterLogs.isEmpty() ? DiffResultCode.COMPARED_WITHOUT_DIFFERENCE : DiffResultCode.COMPARED_WITH_DIFFERENCE,
                filterLogs);
    }

}
