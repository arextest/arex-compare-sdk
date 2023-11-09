package com.arextest.diff.handler.log;

public enum LogMarker {
  ZERO,

  // This marker indicates that there is a null value at the recursive entry
  NULL_CHECK,

  // This marker indicates that there is different type
  TYPE_DIFF,

  // This marker indicates that there is right missing at the comparison of object
  RIGHT_OBJECT_MISSING,

  // This marker indicates that there is left missing at the comparison of object
  LEFT_OBJECT_MISSING,

  // This marker indicates that there is different count at the comparison of array
  DIFF_ARRAY_COUNT,

  // This marker indicates that there is right missing at the comparison of array
  RIGHT_ARRAY_MISSING,

  // This marker indicates that there is not unique key at the left search of listkey
  REPEAT_LEFT_KEY,

  // This mark indicates that there is right missing at the comparison of listKey mode
  RIGHT_ARRAY_MISSING_KEY,

  // This mark indicates that there is left missing at the comparision of array
  LEFT_ARRAY_MISSING,

  // This marker indicates that there is not unique key at the right search of listkey
  REPEAT_RIGHT_KEY,

  // This mark indicates that there is left missing at the comparison of listKey mode
  LEFT_ARRAY_MISSING_KEY,

  // This mark indicates that there is left reference not fund at the comparison of value
  LEFT_REF_NOT_FOUND,

  // This mark indicates that there is right reference not found at the comparison of value
  RIGHT_REF_NOT_FOUND,

  // This mark indicates that there is unmatched value at the comparison of value
  VALUE_DIFF,
}
