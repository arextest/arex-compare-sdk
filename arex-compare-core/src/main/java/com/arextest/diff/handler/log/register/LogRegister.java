package com.arextest.diff.handler.log.register;

import com.arextest.diff.handler.log.LogMarker;
import com.arextest.diff.handler.log.LogProcess;
import com.arextest.diff.model.compare.CompareContext;
import com.arextest.diff.model.enumeration.ErrorType;
import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.exception.FindErrorException;
import com.arextest.diff.model.key.ReferenceEntity;
import com.arextest.diff.model.log.LogEntity;
import com.arextest.diff.model.log.NodeEntity;
import com.arextest.diff.model.parse.MsgStructure;
import com.arextest.diff.utils.ListUti;
import com.fasterxml.jackson.databind.node.NullNode;

import java.util.Collections;
import java.util.List;

import static com.arextest.diff.compare.CompareHelper.findReferenceNode;
import static com.arextest.diff.compare.CompareHelper.getPkNodePath;
import static com.arextest.diff.compare.CompareHelper.getUnmatchedPair;

public class LogRegister {

    public static void register(Object obj1, Object obj2, LogMarker logMarker, CompareContext compareContext) throws FindErrorException {
        if (LogRegisterCondition.rejectRegister(obj1, obj2, logMarker, compareContext)) {
            return;
        }
        LogEntity log = null;
        switch (logMarker) {
            case NULL_CHECK:
                log = nullCheck(obj1, obj2, logMarker, compareContext);
                break;
            case TYPE_DIFF:
                log = produceLog(obj1, obj2, UnmatchedType.UNMATCHED, ErrorType.TYPE_UNMATCHED,
                        compareContext.getCurrentListKeysLeft(), compareContext);
                break;
            case RIGHT_OBJECT_MISSING:
                log = rightObjMissing(obj1, obj2, logMarker, compareContext);
                break;
            case LEFT_OBJECT_MISSING:
                log = leftObjMissing(obj1, obj2, logMarker, compareContext);
                break;
            case DIFF_ARRAY_COUNT:
                log = produceLog(obj1, obj2, UnmatchedType.DIFFERENT_COUNT, ErrorType.NA,
                        compareContext.getCurrentListKeysLeft(), compareContext);
                break;
            case RIGHT_ARRAY_MISSING:
                log = rightArrayMissing(obj1, obj2, logMarker, compareContext);
                break;
            case REPEAT_LEFT_KEY:
                log = repeatKey(obj1, obj2, true, logMarker, compareContext);
                break;
            case RIGHT_ARRAY_MISSING_KEY:
                log = rightArrayMissing(obj1, obj2, logMarker, compareContext);
                break;
            case LEFT_ARRAY_MISSING:
                log = leftArrayMissing(obj1, obj2, logMarker, compareContext);
                break;
            case REPEAT_RIGHT_KEY:
                log = repeatKey(obj1, obj2, false, logMarker, compareContext);
                break;
            case LEFT_ARRAY_MISSING_KEY:
                log = leftArrayMissing(obj1, obj2, logMarker, compareContext);
                break;
            case LEFT_REF_NOT_FOUND:
                log = refNotFound(obj1, obj2, compareContext.getCurrentListKeysLeft(), logMarker, compareContext);
                break;
            case RIGHT_REF_NOT_FOUND:
                log = refNotFound(obj1, obj2, compareContext.getCurrentListKeysRight(), logMarker, compareContext);
                break;
            case VALUE_DIFF:
                log = produceLog(obj1, obj2, UnmatchedType.UNMATCHED, ErrorType.VALUE_UNMATCHED, compareContext.getCurrentListKeysLeft(), compareContext);
                break;
            default:
                break;
        }

        // filter logEntity
        LogProcess logProcess = compareContext.getLogProcess();
        if (logProcess.process(Collections.singletonList(log))) {
            return;
        }
        if (compareContext.isQuickCompare()) {
            throw new FindErrorException("find value diff");
        }
        saveLog(log, compareContext);
    }

    private static LogEntity nullCheck(Object obj1, Object obj2, LogMarker logMarker, CompareContext compareContext) {
        boolean leftNull = (obj1 == null || obj1 instanceof NullNode);
        List<String> currentListKeys = leftNull
                ? compareContext.getCurrentListKeysRight()
                : compareContext.getCurrentListKeysLeft();
        return produceLog(obj1, obj2, UnmatchedType.UNMATCHED, ErrorType.NULL_EXIST, currentListKeys, compareContext);
    }


    private static LogEntity rightObjMissing(Object obj1, Object obj2, LogMarker logMarker, CompareContext compareContext) {
        int errorType = isStructMissing(UnmatchedType.RIGHT_MISSING, compareContext)
                ? ErrorType.SCHEMA_RIGHT_MISSING
                : ErrorType.OTHER_RIGHT_MISSING;
        return produceLog(obj1, obj2, UnmatchedType.RIGHT_MISSING, errorType, compareContext.getCurrentListKeysLeft(), compareContext);
    }

    private static LogEntity leftObjMissing(Object obj1, Object obj2, LogMarker logMarker, CompareContext compareContext) {
        int errorType = isStructMissing(UnmatchedType.LEFT_MISSING, compareContext)
                ? ErrorType.SCHEMA_LEFT_MISSING
                : ErrorType.OTHER_LEFT_MISSING;
        return produceLog(obj1, obj2, UnmatchedType.LEFT_MISSING, errorType, compareContext.getCurrentListKeysRight(), compareContext);
    }

    private static LogEntity rightArrayMissing(Object obj1, Object obj2, LogMarker logMarker, CompareContext compareContext) {
        LogEntity log = produceLog(obj1, obj2, UnmatchedType.RIGHT_MISSING, ErrorType.LIST_RIGHT_MISSING, compareContext.getCurrentListKeysLeft(), compareContext);
        List<ReferenceEntity> references = findReferenceNode(compareContext.getCurrentNodeLeft(), compareContext.getResponseReferences());
        if (!references.isEmpty()) {
            List<NodeEntity> pkNodePath = getPkNodePath(references, true, obj1, compareContext);
            if (pkNodePath != null) {
                log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
            }
        }
        return log;
    }

    private static LogEntity repeatKey(Object obj1, Object obj2, boolean left, LogMarker logMarker, CompareContext compareContext) {
        Object usedObj = logMarker == LogMarker.REPEAT_LEFT_KEY ? obj1 : obj2;
        List<String> currentListKeys = logMarker == LogMarker.REPEAT_LEFT_KEY
                ? compareContext.getCurrentListKeysLeft()
                : compareContext.getCurrentListKeysRight();
        LogEntity log = produceLog(obj1, obj2, UnmatchedType.NOT_UNIQUE, ErrorType.NA, currentListKeys, compareContext);
        log.setWarn(1);
        addReferencePath(log, left, usedObj, compareContext);
        return log;
    }

    private static LogEntity leftArrayMissing(Object obj1, Object obj2, LogMarker logMarker, CompareContext compareContext) {
        LogEntity log = produceLog(obj1, obj2, UnmatchedType.LEFT_MISSING, ErrorType.LIST_LEFT_MISSING, compareContext.getCurrentListKeysRight(), compareContext);
        List<ReferenceEntity> references = findReferenceNode(compareContext.getCurrentNodeRight(), compareContext.getResponseReferences());
        if (!references.isEmpty()) {
            List<NodeEntity> pkNodePath = getPkNodePath(references, false, obj2, compareContext);
            if (pkNodePath != null) {
                log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
            }
        }
        return log;
    }

    public static LogEntity refNotFound(Object obj1, Object obj2, List<String> currentListKeys, LogMarker logMarker, CompareContext compareContext) {
        LogEntity log = produceLog(obj1, obj2, UnmatchedType.REFERENCE_NOT_FOUND, ErrorType.NA, currentListKeys, compareContext);
        log.setWarn(1);
        return log;
    }

    private static LogEntity produceLog(Object obj1, Object obj2, int unmatchedType, int errorType, List<String> currentListKeys, CompareContext compareContext) {
        LogEntity log = new LogEntity(obj1, obj2, getUnmatchedPair(unmatchedType, compareContext).buildListKeys(currentListKeys));
        log.getLogTag().setErrorType(errorType);
        return log;
    }

    private static void saveLog(LogEntity logEntity, CompareContext compareContext) {
        if (logEntity != null) {
            compareContext.getLogs().add(logEntity);
        }
    }

    private static boolean isStructMissing(int unmatchedType, CompareContext compareContext) {
        List<NodeEntity> path = unmatchedType == UnmatchedType.LEFT_MISSING ? compareContext.getCurrentNodeRight() : compareContext.getCurrentNodeLeft();
        MsgStructure msgStructure = unmatchedType == UnmatchedType.LEFT_MISSING ? compareContext.getBaseMsgStructure() : compareContext.getTestMsgStructure();
        if (msgStructure != null) {
            for (NodeEntity nodeEntity : path) {
                if (nodeEntity.getNodeName() != null) {
                    if (msgStructure.getNode().containsKey(nodeEntity.getNodeName())) {
                        msgStructure = msgStructure.getNode().get(nodeEntity.getNodeName());
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void addReferencePath(LogEntity log, boolean left, Object obj, CompareContext compareContext) {
        List<ReferenceEntity> references =
                findReferenceNode(left ? compareContext.getCurrentNodeLeft() : compareContext.getCurrentNodeRight(),
                        compareContext.getResponseReferences());
        if (!references.isEmpty()) {
            List<NodeEntity> pkNodePath = getPkNodePath(references, left, obj, compareContext);
            if (pkNodePath != null) {
                if (left) {
                    log.setAddRefPkNodePathLeft(ListUti.convertPathToStringForShow(pkNodePath));
                } else {
                    log.setAddRefPkNodePathRight(ListUti.convertPathToStringForShow(pkNodePath));
                }
            }
        }
    }

}
