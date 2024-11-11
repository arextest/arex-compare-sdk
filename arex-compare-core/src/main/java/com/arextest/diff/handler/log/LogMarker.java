package com.arextest.diff.handler.log;

public enum LogMarker {


  UNKNOWN(0),

  // This mark indicates that there is unmatched value at the comparison of value
  VALUE_DIFF(1),

  // This marker indicates that there is right missing at the comparison of object
  RIGHT_OBJECT_MISSING(2),

  // This marker indicates that there is left missing at the comparison of object
  LEFT_OBJECT_MISSING(3),

  // This mark indicates that there is left missing at the comparision of array
  LEFT_ARRAY_MISSING(4),

  // This marker indicates that there is right missing at the comparison of array
  RIGHT_ARRAY_MISSING(5),

  // This marker indicates that there is a null value at the recursive entry
  NULL_CHECK(6),

  // This marker indicates that there is different type
  TYPE_DIFF(7),

  // This marker indicates that there is different count at the comparison of array
  DIFF_ARRAY_COUNT(8),

  // This marker indicates that there is not unique key at the left search of listkey
  REPEAT_LEFT_KEY(9),

  // This marker indicates that there is not unique key at the right search of listkey
  REPEAT_RIGHT_KEY(10),

  // This mark indicates that there is right missing at the comparison of listKey mode
  RIGHT_ARRAY_MISSING_KEY(11),

  // This mark indicates that there is left missing at the comparison of listKey mode
  LEFT_ARRAY_MISSING_KEY(12),

  // This mark indicates that there is left reference not fund at the comparison of value
  LEFT_REF_NOT_FOUND(13),

  // This mark indicates that there is right reference not found at the comparison of value
  RIGHT_REF_NOT_FOUND(14);

  private Integer code;

  LogMarker(int code) {
    this.code = code;
  }

  public Integer getCode() {
    return code;
  }

  public static LogMarker from(int code) {
    for (LogMarker type : LogMarker.values()) {
      if (type.getCode() == code) {
        return type;
      }
    }
    return UNKNOWN;
  }


}
