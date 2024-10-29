package com.arextest.diff.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;

public class NameConvertUtil {

  public static void nameConvert(Object object) {
    if (object == null || object instanceof NullNode) {
      return;
    }

    if (object instanceof ObjectNode) {
      ObjectNode jsonObj1 = (ObjectNode) object;
      List<String> names = JacksonHelperUtil.getNames(jsonObj1);

      for (String fieldName : names) {
        JsonNode obj1FieldValue = jsonObj1.get(fieldName);
        String lowerCase = fieldName.toLowerCase();
        jsonObj1.set(lowerCase, obj1FieldValue);
        if (fieldName != lowerCase) {
          jsonObj1.remove(fieldName);
        }
        nameConvert(obj1FieldValue);
      }
    } else if (object instanceof ArrayNode) {
      ArrayNode obj1Array = (ArrayNode) object;
      int len = obj1Array.size();
      for (int i = 0; i < len; i++) {
        Object element = obj1Array.get(i);
        nameConvert(element);
      }
    }
  }
}
