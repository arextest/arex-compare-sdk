package com.arextest.diff.model.enumeration;

/**
 * Created by rchen9 on 2023/6/15.
 */
public class MsgMissingCode {

  // both compared messages exist
  public static final int NO_MISSING = 0;
  // Both comparison messages are missing
  public static final int ALL_MISSING = 1;
  // The left comparison message is missing
  public static final int LEFT_MISSING = 2;
  // The right comparison message is missing
  public static final int RIGHT_MISSING = 3;

}
