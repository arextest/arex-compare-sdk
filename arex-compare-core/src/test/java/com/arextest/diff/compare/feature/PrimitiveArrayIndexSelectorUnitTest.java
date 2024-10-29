package com.arextest.diff.compare.feature;

import com.arextest.diff.model.compare.CompareContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PrimitiveArrayIndexSelectorUnitTest {

  static PrimitiveArrayIndexSelector primitiveArrayIndexSelector;

  @BeforeAll
  static void setUpClass() throws JsonProcessingException {
    CompareContext compareContext = new CompareContext();
    ObjectMapper objectMapper = new ObjectMapper();
    compareContext.currentBaseObj = objectMapper.readValue("[1,2,3]", ArrayNode.class);
    compareContext.currentTestObj = objectMapper.readValue("[3,1,2]", ArrayNode.class);
    primitiveArrayIndexSelector = new PrimitiveArrayIndexSelector(compareContext);
  }


  @Test
  public void testFindCorrespondLeftIndex() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayNode obj1 = objectMapper.readValue("[1,2,3]", ArrayNode.class);
    ArrayNode obj2 = objectMapper.readValue("[3,1,2]", ArrayNode.class);

    int correspondLeftIndex = primitiveArrayIndexSelector.findCorrespondLeftIndex(0,
        Collections.emptySet(), obj1, obj2);
    Assertions.assertEquals(2, correspondLeftIndex);
  }

  @Test
  public void testFindCorrespondRightIndex() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayNode obj1 = objectMapper.readValue("[1,2,3]", ArrayNode.class);
    ArrayNode obj2 = objectMapper.readValue("[3,1,2]", ArrayNode.class);

    int correspondRightIndex = primitiveArrayIndexSelector.findCorrespondRightIndex(0,
        Collections.emptySet(), obj1, obj2);
    Assertions.assertEquals(1, correspondRightIndex);
  }

  @Test
  public void testJudgeLeftIndexStandard() {
    String indexKey = primitiveArrayIndexSelector.judgeLeftIndexStandard(0);
    Assertions.assertEquals("Index:[0]", indexKey);
  }

  @Test
  public void testJudgeRightIndexStandard() {
    String indexKey = primitiveArrayIndexSelector.judgeRightIndexStandard(0);
    Assertions.assertEquals("Index:[0]", indexKey);
  }
}
