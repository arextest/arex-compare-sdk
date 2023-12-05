package com.arextest.diff.handler.verify;

import com.arextest.diff.model.parse.MsgObjCombination;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class VerifyObjectParse {

  public boolean verify(MsgObjCombination msgObjCombination) {
    Object baseObj = msgObjCombination.getBaseObj();
    Object testObj = msgObjCombination.getTestObj();

    if (baseObj instanceof ObjectNode && testObj instanceof ObjectNode) {
      return true;
    }

    if (baseObj instanceof ArrayNode && testObj instanceof ArrayNode) {
      return true;
    }
    return false;
  }

}
