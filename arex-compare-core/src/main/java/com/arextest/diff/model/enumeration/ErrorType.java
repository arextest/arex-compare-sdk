package com.arextest.diff.model.enumeration;

public interface ErrorType {

  // unknown error
  int NA = 0;
  // the type of the two values is different
  int TYPE_UNMATCHED = 1;
  // the value of the two nodes is different
  int VALUE_UNMATCHED = 2;
  // There is a null value in any node
  int NULL_EXIST = 3;
  // The array node of the basic msg has a problem of missing elements.
  int LIST_LEFT_MISSING = 4;
  // The array node of the test msg has a problem of missing elements.
  int LIST_RIGHT_MISSING = 5;
  // Message structure comparison, the left node is missing
  int SCHEMA_LEFT_MISSING = 6;
  // Message structure comparison, the right node is missing
  int SCHEMA_RIGHT_MISSING = 7;
  //  The object node of the basic msg is missing
  int OTHER_LEFT_MISSING = 8;
  //  The object node of the test msg is missing
  int OTHER_RIGHT_MISSING = 9;
}
