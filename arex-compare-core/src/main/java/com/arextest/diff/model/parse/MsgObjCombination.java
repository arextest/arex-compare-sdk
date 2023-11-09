package com.arextest.diff.model.parse;

public class MsgObjCombination {

  private Object baseObj;

  private Object testObj;

  public MsgObjCombination() {
  }

  public MsgObjCombination(Object baseObj, Object testObj) {
    this.baseObj = baseObj;
    this.testObj = testObj;
  }

  public Object getBaseObj() {
    return baseObj;
  }

  public void setBaseObj(Object baseObj) {
    this.baseObj = baseObj;
  }

  public Object getTestObj() {
    return testObj;
  }

  public void setTestObj(Object testObj) {
    this.testObj = testObj;
  }
}
