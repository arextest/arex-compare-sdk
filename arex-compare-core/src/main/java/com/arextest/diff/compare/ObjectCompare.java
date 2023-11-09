package com.arextest.diff.compare;

import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;

public class ObjectCompare {

  public static void objectCompare(Object obj1, Object obj2, CompareContext compareContext)
      throws Exception {

    ObjectNode jsonObj1 = (ObjectNode) obj1;
    ObjectNode jsonObj2 = (ObjectNode) obj2;

    List<String> names1 = JacksonHelperUtil.getNames(jsonObj1);
    List<String> names2 = JacksonHelperUtil.getNames(jsonObj2);

    List<String> usedFieldNames = new ArrayList<>();
    for (String fieldName : names1) {
      compareContext.currentNodeLeft.add(new NodeEntity(fieldName, 0));

      Object obj1FieldValue, obj2FieldValue = null;
      obj1FieldValue = jsonObj1.get(fieldName);

      boolean rightExist = false;
      obj2FieldValue = jsonObj2.get(fieldName);
      if (obj2FieldValue != null) {
        rightExist = true;
        usedFieldNames.add(fieldName);
        compareContext.currentNodeRight.add(new NodeEntity(fieldName, 0));
      }

      GenericCompare.jsonCompare(obj1FieldValue, obj2FieldValue, compareContext);

      ListUti.removeLast(compareContext.currentNodeLeft);
      if (rightExist) {
        ListUti.removeLast(compareContext.currentNodeRight);
      }
    }
    for (String fieldName : names2) {
      if (usedFieldNames.contains(fieldName)) {
        continue;
      }
      compareContext.currentNodeRight.add(new NodeEntity(fieldName, 0));

      Object obj2FieldValue, obj1FieldValue = null;
      obj2FieldValue = jsonObj2.get(fieldName);

      boolean leftExist = false;
      obj1FieldValue = jsonObj1.get(fieldName);
      if (obj1FieldValue != null) {
        leftExist = true;
        compareContext.currentNodeLeft.add(new NodeEntity(fieldName, 0));
      }

      GenericCompare.jsonCompare(obj1FieldValue, obj2FieldValue, compareContext);

      if (leftExist) {
        ListUti.removeLast(compareContext.currentNodeLeft);
      }
      ListUti.removeLast(compareContext.currentNodeRight);
    }
  }
}
