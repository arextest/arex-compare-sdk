package com.arextest.diff.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rchen9 on 2023/2/16.
 */
public class JacksonHelperUtil {

  public static ObjectMapper objectMapper = new ObjectMapper();

  public static List<String> getNames(ObjectNode objectNode) {
    List<String> result = new ArrayList<>();
    Iterator<String> stringIterator = objectNode.fieldNames();
    while (stringIterator.hasNext()) {
      result.add(stringIterator.next());
    }
    return result;
  }

  public static ObjectNode getObjectNode() {
    return objectMapper.createObjectNode();
  }

  public static ArrayNode getArrayNode() {
    return objectMapper.createArrayNode();
  }

  public static boolean isObjectNode(Object object) {
    return object instanceof ObjectNode;
  }

  public static boolean isArrayNode(Object object) {
    return object instanceof ArrayNode;
  }


}
