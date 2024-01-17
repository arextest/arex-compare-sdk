package com.arextest.diff.model.enumeration;

import java.util.Collections;
import java.util.List;

public interface Constant {

  String DYNAMIC_PATH = "*";

  List<String> ROOT_PATH = Collections.singletonList("arex_root");

  String EXPRESSION_PATH_IDENTIFIER_START = "[";

  String EXPRESSION_PATH_IDENTIFIER_END = "]";

  int KEY_COMPUTE_WAIT_MINUTES_TIME = 2;

  int JSON_PATH_PARSE_MINUTES_TIME = 2;

}
