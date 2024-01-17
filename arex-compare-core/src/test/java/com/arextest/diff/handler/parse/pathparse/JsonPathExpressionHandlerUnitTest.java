package com.arextest.diff.handler.parse.pathparse;


import com.arextest.diff.handler.pathparse.JsonPathExpressionHandler;
import com.arextest.diff.model.pathparse.ExpressionNodeEntity;
import com.arextest.diff.model.pathparse.ExpressionNodeType;
import com.arextest.diff.model.pathparse.expression.EqualsExpression;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JsonPathExpressionHandlerUnitTest {

  @InjectMocks
  JsonPathExpressionHandler jsonPathExpressionHandler;

  private ObjectNode objectNode;

  {
    String json = "{\n"
        + "    \"response\": {\n"
        + "        \"students\": [\n"
        + "            {\n"
        + "                \"info\": {\n"
        + "                    \"name\": \"xiaomi\"\n"
        + "                },\n"
        + "                \"age\": 18\n"
        + "            },\n"
        + "            {\n"
        + "                \"info\": {\n"
        + "                    \"name\": \"apple\"\n"
        + "                },\n"
        + "                \"age\": 19\n"
        + "            },\n"
        + "            {\n"
        + "                \"info\": {\n"
        + "                    \"name\": \"apple\"\n"
        + "                }\n"
        + "            }\n"
        + "        ],\n"
        + "        \"region\": \"beijing\"\n"
        + "    }\n"
        + "}";
    try {
      objectNode = JacksonHelperUtil.objectMapper.readValue(json, ObjectNode.class);
    } catch (JsonProcessingException e) {
    }
  }

//  private Object object = JacksonHelperUtil.objectMapper.reader

  @Test
  @DisplayName("test json path expression handler: students[i]")
  public void testArrayIndex()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    LinkedList<ExpressionNodeEntity> expressions = new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(1, ExpressionNodeType.INDEX_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE)
    ));
    int startIndex = 0;
    int endIndex = expressions.size();
    boolean isArr = false;

    // Get the private method doSinglePathExpressionParse
    Method method = JsonPathExpressionHandler.class.getDeclaredMethod(
        "doSinglePathExpressionParse",
        List.class, int.class, int.class, Object.class, boolean.class);
    method.setAccessible(true);

    // Invoke the private method
    LinkedList<LinkedList<ExpressionNodeEntity>> result =
        (LinkedList<LinkedList<ExpressionNodeEntity>>) method.invoke(
            jsonPathExpressionHandler, expressions, startIndex, endIndex, objectNode, isArr);

    LinkedList<LinkedList<ExpressionNodeEntity>> expected = new LinkedList<>();
    expected.add(new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(1, ExpressionNodeType.INDEX_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE)
    )));
    Assertions.assertEquals(expected, result);
  }

  @Test
  @DisplayName("test json path expression handler: students/[info/name == 'apple']")
  public void testArrayNameEquals()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    LinkedList<ExpressionNodeEntity> expressions = new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(new EqualsExpression(Arrays.asList("info", "name"), "apple"),
            ExpressionNodeType.EXPRESSION_NODE)
    ));

    int startIndex = 0;
    int endIndex = expressions.size();
    boolean isArr = false;

    // Get the private method doSinglePathExpressionParse
    Method method = JsonPathExpressionHandler.class.getDeclaredMethod(
        "doSinglePathExpressionParse",
        List.class, int.class, int.class, Object.class, boolean.class);
    method.setAccessible(true);

    // Invoke the private method
    LinkedList<LinkedList<ExpressionNodeEntity>> result =
        (LinkedList<LinkedList<ExpressionNodeEntity>>) method.invoke(
            jsonPathExpressionHandler, expressions, startIndex, endIndex, objectNode, isArr);

    LinkedList<LinkedList<ExpressionNodeEntity>> expected = new LinkedList<>();
    expected.add(new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(1, ExpressionNodeType.INDEX_NODE)
    )));
    expected.add(new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(2, ExpressionNodeType.INDEX_NODE)
    )));
    Assertions.assertEquals(expected, result);
  }

  @Test
  @DisplayName("test json path expression handler: students/[info/name == 'apple']/info/name")
  public void testArrayNameEqualsAndSpecialObjectIgnore()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    LinkedList<ExpressionNodeEntity> expressions = new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(new EqualsExpression(Arrays.asList("info", "name"), "apple"),
            ExpressionNodeType.EXPRESSION_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("name", ExpressionNodeType.NAME_NODE)
    ));

    int startIndex = 0;
    int endIndex = expressions.size();
    boolean isArr = false;

    // Get the private method doSinglePathExpressionParse
    Method method = JsonPathExpressionHandler.class.getDeclaredMethod(
        "doSinglePathExpressionParse",
        List.class, int.class, int.class, Object.class, boolean.class);
    method.setAccessible(true);

    // Invoke the private method
    LinkedList<LinkedList<ExpressionNodeEntity>> result =
        (LinkedList<LinkedList<ExpressionNodeEntity>>) method.invoke(
            jsonPathExpressionHandler, expressions, startIndex, endIndex, objectNode, isArr);

    LinkedList<LinkedList<ExpressionNodeEntity>> expected = new LinkedList<>();
    expected.add(new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(1, ExpressionNodeType.INDEX_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("name", ExpressionNodeType.NAME_NODE)
    )));
    expected.add(new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(2, ExpressionNodeType.INDEX_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("name", ExpressionNodeType.NAME_NODE)
    )));
    Assertions.assertEquals(expected, result);
  }

  @Test
  @DisplayName("test json path expression handler: students/info/[name== 'xiaomi']")
  public void testArrayNameEqualsAndIgnore()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

    LinkedList<ExpressionNodeEntity> expressions = new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(new EqualsExpression(Arrays.asList("name"), "xiaomi"),
            ExpressionNodeType.EXPRESSION_NODE)
    ));

    int startIndex = 0;
    int endIndex = expressions.size();
    boolean isArr = false;

    // Get the private method doSinglePathExpressionParse
    Method method = JsonPathExpressionHandler.class.getDeclaredMethod(
        "doSinglePathExpressionParse",
        List.class, int.class, int.class, Object.class, boolean.class);
    method.setAccessible(true);

    // Invoke the private method
    LinkedList<LinkedList<ExpressionNodeEntity>> result =
        (LinkedList<LinkedList<ExpressionNodeEntity>>) method.invoke(
            jsonPathExpressionHandler, expressions, startIndex, endIndex, objectNode, isArr);

    LinkedList<LinkedList<ExpressionNodeEntity>> expected = new LinkedList<>();
    expected.add(new LinkedList<>(Arrays.asList(
        new ExpressionNodeEntity("response", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("students", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity(0, ExpressionNodeType.INDEX_NODE),
        new ExpressionNodeEntity("info", ExpressionNodeType.NAME_NODE),
        new ExpressionNodeEntity("name", ExpressionNodeType.NAME_NODE)
    )));
    Assertions.assertEquals(expected, result);
  }


}
