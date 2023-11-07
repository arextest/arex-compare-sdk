package com.arextest.diff.utils;

import org.slf4j.MDC;

public class MDCCompareUtil {

    public static final String SERVICE_NAME_VALUE = "compareSDK";

    private static final String SERVICE_NAME = "serviceName";
    private static final String QUICK_COMPARE = "quickCompare";

    private static final String COMPARE_TYPE = "compareType";

    public static void addServiceName(String value) {
        MDC.put(SERVICE_NAME, value);
    }

    public static void removeServiceName() {
        MDC.remove(SERVICE_NAME);
    }

    public static void addFastCompare(boolean quickCompare) {
        MDC.put(QUICK_COMPARE, String.valueOf(quickCompare));
    }

    public static void removeFastCompare() {
        MDC.remove(QUICK_COMPARE);
    }

    public static void addCompareType(String value) {
        MDC.put(COMPARE_TYPE, value);
    }

    public static void removeCompareType() {
        MDC.remove(COMPARE_TYPE);
    }

}
