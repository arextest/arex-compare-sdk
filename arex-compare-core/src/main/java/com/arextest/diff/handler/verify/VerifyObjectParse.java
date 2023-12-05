package com.arextest.diff.handler.verify;

import com.arextest.diff.model.parse.MsgObjCombination;
import java.util.Objects;

public class VerifyObjectParse {

  public boolean verify(MsgObjCombination msgObjCombination) {
    Object baseObj = msgObjCombination.getBaseObj();
    Object testObj = msgObjCombination.getTestObj();

    if (baseObj == null || testObj == null) {
      return false;
    }

    if (baseObj instanceof String || testObj instanceof String) {
      return false;
    }

    if (!Objects.equals(baseObj.getClass(), testObj.getClass())) {
      return false;
    }
    return true;
  }

}
