package com.arextest.diff.model.log;

import java.io.Serializable;

public class LogTag implements Serializable {

  /**
   * {@link com.arextest.diff.model.enumeration.ErrorType}
   */
  private int errorType = 0;

  public LogTag() {
  }

  public int getErrorType() {
    return errorType;
  }

  public void setErrorType(int errorType) {
    this.errorType = errorType;
  }
}
