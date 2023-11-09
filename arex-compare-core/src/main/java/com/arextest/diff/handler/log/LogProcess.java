package com.arextest.diff.handler.log;

import com.arextest.diff.factory.PluginServiceFactory;
import com.arextest.diff.handler.log.filterrules.UnmatchedTypeFilter;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.plugin.LogEntityFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class LogProcess {

  private static List<Predicate<LogEntity>> globalFilterRules = new ArrayList<Predicate<LogEntity>>() {{
    add(new UnmatchedTypeFilter());
  }};


  private RulesConfig rulesConfig;
  private List<Predicate<LogEntity>> filterRules;

  public void setRulesConfig(RulesConfig rulesConfig) {
    this.rulesConfig = rulesConfig;
  }

  public void appendFilterRules(Predicate<LogEntity> rule) {
    if (this.filterRules == null) {
      this.filterRules = new ArrayList<>();
    }
    this.filterRules.add(rule);
  }

  public void appendFilterRules(List<Predicate<LogEntity>> rules) {
    if (this.filterRules == null) {
      this.filterRules = new ArrayList<>();
    }
    this.filterRules.addAll(rules);
  }

  public boolean process(List<LogEntity> logEntities) {

    Stream<LogEntity> stream = logEntities.stream();
    for (Predicate<LogEntity> filterRule : globalFilterRules) {
      stream = stream.filter(filterRule);
    }
    for (Predicate<LogEntity> filterRule : this.filterRules) {
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
    return stream.count() == 0;
  }

}
