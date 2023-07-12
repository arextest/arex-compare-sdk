package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.GlobalOptions;
import com.arextest.diff.model.RulesConfig;
import com.arextest.diff.utils.CompareUtil;
import com.arextest.diff.utils.JSONArraySort;
import com.arextest.diff.utils.JacksonHelperUtil;
import com.arextest.diff.utils.OptionsToRulesAdapter;

import java.util.List;
import java.util.Objects;

public class CompareSDK {

    private GlobalOptions globalOptions = new GlobalOptions();

    public CompareSDK() {
    }


    public GlobalOptions getGlobalOptions() {
        return this.globalOptions;
    }

    public CompareResult compare(String baseMsg, String testMsg) {
        RulesConfig rulesConfig = OptionsToRulesAdapter.optionsToConfig(baseMsg, testMsg, null, globalOptions);
        return compare(rulesConfig);
    }

    public CompareResult compare(String baseMsg, String testMsg, CompareOptions compareOptions) {
        RulesConfig rulesConfig = OptionsToRulesAdapter.optionsToConfig(baseMsg, testMsg, compareOptions, globalOptions);
        return compare(rulesConfig);
    }

    public CompareResult quickCompare(String baseMsg, String testMsg) {
        RulesConfig rulesConfig = OptionsToRulesAdapter.optionsToConfig(baseMsg, testMsg, null, globalOptions);
        rulesConfig.setQuickCompare(true);
        return compare(rulesConfig);
    }

    public CompareResult quickCompare(String baseMsg, String testMsg, CompareOptions compareOptions) {
        RulesConfig rulesConfig = OptionsToRulesAdapter.optionsToConfig(baseMsg, testMsg, compareOptions, globalOptions);
        rulesConfig.setQuickCompare(true);
        return compare(rulesConfig);
    }

    private CompareResult compare(RulesConfig rulesConfig) {
        if (Objects.equals(rulesConfig.getBaseMsg(), rulesConfig.getTestMsg())) {
            return CompareResult.builder().noDiff(rulesConfig.getBaseMsg(), rulesConfig.getTestMsg()).build();
        }
        return CompareUtil.jsonCompare(rulesConfig);
    }

    /**
     * fromException
     *
     * @param baseMsg the based msg
     * @param testMsg the tested msg
     * @param remark  the info of exception
     * @return the compare result
     */
    public static CompareResult fromException(String baseMsg, String testMsg, String remark) {
        return CompareResult.builder().exception(baseMsg, testMsg, remark).build();
    }


    /**
     * Expose to the caller, sort a set of packets, and use split sorting
     *
     * @param baseMsgs the based msg
     * @param testMsgs the tested msg
     */
    public static void arraySort(List<String> baseMsgs, List<String> testMsgs) {

        if (baseMsgs == null || testMsgs == null) {
            return;
        }

        JSONArraySort.jsonArraySortSplitWhole(baseMsgs, testMsgs, 0.0f);

        int leftSize = baseMsgs.size();
        int rightSize = testMsgs.size();

        while (leftSize < rightSize) {
            baseMsgs.add(JacksonHelperUtil.getObjectNode().toString());
            leftSize++;
        }

        while (leftSize > rightSize) {
            testMsgs.add(JacksonHelperUtil.getArrayNode().toString());
            rightSize++;
        }
    }

}
