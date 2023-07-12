package com.arextest.diff.handler.log.filterrules;

import com.arextest.diff.model.enumeration.UnmatchedType;
import com.arextest.diff.model.log.LogEntity;

import java.util.function.Predicate;

/**
 * Created by rchen9 on 2023/4/13.
 */
public class ArexPrefixFilter implements Predicate<LogEntity> {

    private static final String PREFIX_FIRST = "arex_";
    private static final String PREFIX_SECOND = "_arex";

    @Override
    public boolean test(LogEntity logEntity) {
        int unmatchedType = logEntity.getPathPair().getUnmatchedType();
        if (unmatchedType == UnmatchedType.UNMATCHED) {
            Object baseValue = logEntity.getBaseValue();
            Object testValue = logEntity.getTestValue();
            if (baseValue != null && testValue != null) {
                return !isEqualsWithoutPrefix((String) baseValue, (String) testValue);
            }
        }
        return true;
    }

    public static boolean isEqualsWithoutPrefix(String baseStr, String testStr) {

        int baseValueLen = baseStr.length();
        int testValueLen = testStr.length();
        if (testValueLen - baseValueLen <= 0 || (testValueLen - baseValueLen) % 5 != 0) {
            return false;
        }

        int baseIndex = 0;
        int testIndex = 0;

        if (baseValueLen == 0) {
            while (testIndex < testValueLen) {
                if (isPrefix(testIndex, testStr)) {
                    testIndex = testIndex + 5;
                } else {
                    return false;
                }
            }
            return true;
        } else {
            while (baseIndex < baseValueLen && testIndex < testValueLen) {
                if (isPrefix(testIndex, testStr)) {
                    testIndex = testIndex + 5;
                    continue;
                }
                if (baseStr.charAt(baseIndex) != testStr.charAt(testIndex)) {
                    return false;
                }
                baseIndex++;
                testIndex++;
            }
            return true;
        }
    }

    private static boolean isPrefix(int testIndex, String testValue) {
        if (testIndex > testValue.length() - 5) {
            return false;
        }

        if (testValue.charAt(testIndex) != 'a' && testValue.charAt(testIndex) != '_') {
            return false;
        }

        String substring = testValue.substring(testIndex, testIndex + 5);
        if (PREFIX_FIRST.equals(substring) || PREFIX_SECOND.equals(substring)) {
            return true;
        }
        return false;
    }
}
