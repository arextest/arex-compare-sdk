package com.arextest.diff.plugin;

import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.model.log.LogEntity;

/**
 * Created by rchen9 on 2023/4/24.
 */
public interface LogEntityFilter {

  boolean isIgnore(LogEntity logEntity, RulesConfig rulesConfig);
}
