package com.arextest.diff.model.enumeration;

public class UnmatchedType {

  // unknown error
  public static final int NA = 0;
  // the node of basic msg is missing
  public static final int LEFT_MISSING = 1;
  // the node of test msg is missing
  public static final int RIGHT_MISSING = 2;
  // the value of the two nodes is different
  public static final int UNMATCHED = 3;
  // The array node of the basic msg has a problem of missing elements.
  public static final int DIFFERENT_COUNT = 4;
  // the key of list is not unique
  public static final int NOT_UNIQUE = 5;
  // not support the comparision of the node
  public static final int NOT_SUPPORT = 6;
  // not find the reference node
  public static final int REFERENCE_NOT_FOUND = 7;
  // the actual value is null
  public static final int EXPECT_NOT_NULL = 8;
  // the actual value is not expected
  public static final int NOT_EXPECT_VALUE = 9;
  // the number of list nodes is not expected
  public static final int NOT_EXPECT_LIST_COUNT = 10;
  // the type of the two nodes is different
  public static final int DIFFERENT_TYPE = 11;
  // other error
  public static final int OTHERS = 12;

  private UnmatchedType() {
  }
}