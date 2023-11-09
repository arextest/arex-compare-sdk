package com.arextest.diff.model.enumeration;

public interface ErrorType {

  int NA = 0;
  int TYPE_UNMATCHED = 1;
  int VALUE_UNMATCHED = 2;
  int NULL_EXIST = 3;
  int LIST_LEFT_MISSING = 4;
  int LIST_RIGHT_MISSING = 5;
  int SCHEMA_LEFT_MISSING = 6;
  int SCHEMA_RIGHT_MISSING = 7;
  int OTHER_LEFT_MISSING = 8;
  int OTHER_RIGHT_MISSING = 9;
}
