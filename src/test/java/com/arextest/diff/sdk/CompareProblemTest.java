package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.enumeration.CategoryType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by rchen9 on 2023/4/21.
 */
public class CompareProblemTest {

    @Test
    public void fixCastToJSONNodeException() {
        CompareSDK sdk = new CompareSDK();
        String baseMsg = "{\"array\":[1,2,3]}";
        String testMsg = "{\"array\":[1,2]}";
        CompareResult result = sdk.compare(baseMsg, testMsg);
        Assert.assertEquals(result.getLogs().size(), 1);
    }

    @Test
    public void testReplaceParse() {
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
        String baseMsg = "{\"body\":\"REPLACE INTO orderTable(OrderId, InfoId, DataChange_LastTime, userdata_location) VALUES " +
                "(36768383786, 36768317034, '2023-05-14 18:00:34.556', '')," +
                "(36768317034, 36768317034, '2023-05-14 18:00:34.556', '')\"}";
        String testMsg = "{\"body\":\"REPLACE INTO orderTable(OrderId, InfoId, DataChange_LastTime, userdata_location) VALUES " +
                "(36768383786, 36768317034, '2023-05-14 18:00:34.556', '')," +
                "(36768317034, 36768317034, '2023-05-14 18:00:34.656', '')\"}";

        CompareOptions compareOptions = CompareOptions.options();
        compareOptions.putExclusions(Arrays.asList("body"));
        compareOptions.putCategoryType(CategoryType.DATABASE);
        compareOptions.putSelectIgnoreCompare(true);
        compareOptions.putIgnoredTimePrecision(1000L);

        CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
        Assert.assertEquals(result.getLogs().size(), 1);
    }
}
