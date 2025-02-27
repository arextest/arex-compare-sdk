package com.arextest.diff.compare;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.register.LogRegister;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.enumeration.ParentNodeType;
import com.arextest.diff.model.exception.FindErrorException;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.utils.IgnoreUtil;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class GenericCompare {

  private static final Logger LOGGER = Logger.getLogger(GenericCompare.class.getName());

  public static void jsonCompare(Object obj1, Object obj2, CompareContext compareContext)
      throws Exception {

    compareContext.currentBaseObj = obj1;
    compareContext.currentTestObj = obj2;

    List<NodeEntity> currentNode =
        compareContext.currentNodeLeft.size() >= compareContext.currentNodeRight.size()
            ? compareContext.currentNodeLeft : compareContext.currentNodeRight;
    List<String> fuzzyPath = ListUti.convertToStringList(currentNode);

    // ignore primary key node
    if (compareContext.pkNodePaths != null) {
      for (List<String> pkNodePath : compareContext.pkNodePaths) {
        if (pkNodePath.equals(fuzzyPath)) {
          return;
        }
      }
    }

    // not compare by exclusions
    if (IgnoreUtil.ignoreProcessor(fuzzyPath, compareContext.currentNodeLeft,
        compareContext.currentNodeRight, compareContext.exclusions,
        compareContext.conditionExclusions, compareContext.ignoreNodeSet)) {
      return;
    }

    // custom compare
    try {
      if (ScriptCompare.isScriptComparisonRequired(fuzzyPath, compareContext)) {
        ScriptCompare.scriptCompare(obj1, obj2, fuzzyPath, compareContext);
        return;
      }
    } catch (FindErrorException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.warning("Script compare error: " + e.getMessage());
    }

    // field missing
    if (obj1 == null && obj2 == null) {
      return;
    } else if (obj1 == null) {
      LogRegister.register(obj1, obj2,
          Objects.equals(compareContext.parentNodeType, ParentNodeType.OBJECT)
              ? LogMarker.LEFT_OBJECT_MISSING
              : LogMarker.LEFT_ARRAY_MISSING, compareContext);
      return;
    } else if (obj2 == null) {
      LogRegister.register(obj1, obj2,
          Objects.equals(compareContext.parentNodeType, ParentNodeType.OBJECT)
              ? LogMarker.LEFT_OBJECT_MISSING
              : LogMarker.RIGHT_ARRAY_MISSING, compareContext);
      return;
    }

    // There is a null value in any of the left and right nodes
    if ((obj1 instanceof NullNode && !(obj2 instanceof NullNode)) ||
        (!(obj1 instanceof NullNode) && obj2 instanceof NullNode)) {
      LogRegister.register(obj1, obj2, LogMarker.NULL_CHECK, compareContext);
      return;
    }

    // obj1 and obj2 are different types
    if (!isNonLeafNodesTypeEqual(obj1, obj2)) {
      LogRegister.register(obj1, obj2, LogMarker.TYPE_DIFF, compareContext);
      return;
    }

    if (obj1 instanceof ObjectNode) {
      ObjectCompare.objectCompare(obj1, obj2, compareContext);
    } else if (obj1 instanceof ArrayNode) {
      ArrayCompare.arrayCompare(obj1, obj2, compareContext);
    } else {
      ValueCompare.valueCompare(obj1, obj2, compareContext);
    }
  }

  private static boolean isNonLeafNodesTypeEqual(Object obj1, Object obj2) {
    Class<?> obj1Class = obj1.getClass();
    Class<?> obj2Class = obj2.getClass();

    if ((obj1Class == ObjectNode.class && obj2Class != ObjectNode.class) ||
        (obj1Class != ObjectNode.class && obj2Class == ObjectNode.class)) {
      return false;
    }

    if ((obj1Class == ArrayNode.class && obj2Class != ArrayNode.class) ||
        (obj1Class != ArrayNode.class && obj2Class == ArrayNode.class)) {
      return false;
    }
    return true;
  }
}
