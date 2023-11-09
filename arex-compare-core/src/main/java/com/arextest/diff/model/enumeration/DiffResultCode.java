package com.arextest.diff.model.enumeration;

public class DiffResultCode {

  // Compare successfully, no difference
  public static final int COMPARED_WITHOUT_DIFFERENCE = 0;
  // Compare successfully, have difference
  public static final int COMPARED_WITH_DIFFERENCE = 1;
  // Comparison exception
  public static final int COMPARED_INTERNAL_EXCEPTION = 2;
  private DiffResultCode() {

  }

}
