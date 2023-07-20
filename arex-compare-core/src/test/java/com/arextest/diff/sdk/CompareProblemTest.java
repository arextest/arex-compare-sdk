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
        Assert.assertEquals(result.getLogs().size(), 0);
    }

    @Test
    public void testInsertParse() {
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
        String baseMsg = "{\n" +
                "    \"parameters\": [\n" +
                "        {\n" +
                "            \"pr\": {\n" +
                "                \"dd\": \"18\",\n" +
                "                \"re\": \"2023-06-08 23:30\",\n" +
                "                \"ud\": \"Mxxxx\",\n" +
                "                \"rk\": \"客人\",\n" +
                "                \"rn\": \"Message\",\n" +
                "                \"re\": 21\n" +
                "            },\n" +
                "            \"sd\": 39,\n" +
                "            \"oe\": 3,\n" +
                "            \"od\": 1,\n" +
                "            \"is\": true,\n" +
                "            \"de\": \"2023-06-08 22:32:18.059\",\n" +
                "            \"fd\": 27\n" +
                "        }\n" +
                "    ],\n" +
                "    \"body\": \"INSERT INTO `oy` (`sd`, `oe`, `od`, `pr`, `is`, `De`, `fd`) VALUES (?, ?, ?, ?, ?, ?, ?)\",\n" +
                "    \"dbname\": \"fb\"\n" +
                "}";
        String testMsg = "{\n" +
                "    \"parameters\": [\n" +
                "        {\n" +
                "            \"pr\": {\n" +
                "                \"dd\": \"18\",\n" +
                "                \"re\": \"2023-06-08 23:30\",\n" +
                "                \"ud\": \"Mxxxx\",\n" +
                "                \"rk\": \"客人\",\n" +
                "                \"rn\": \"Message\",\n" +
                "                \"re\": 21\n" +
                "            },\n" +
                "            \"un\": \"\",\n" +
                "            \"sd\": 39,\n" +
                "            \"oe\": 3,\n" +
                "            \"od\": 1,\n" +
                "            \"is\": true,\n" +
                "            \"de\": \"2023-06-08 22:32:17.963\",\n" +
                "            \"fd\": 27\n" +
                "        }\n" +
                "    ],\n" +
                "    \"body\": \"INSERT INTO `oy` (`sd`, `oe`, `od`, `pr`, `is`, `De`, `fd`, `un`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)\",\n" +
                "    \"dbname\": \"fb\"\n" +
                "}";

        CompareOptions compareOptions = CompareOptions.options();
        compareOptions.putExclusions(Arrays.asList("body"));
        compareOptions.putCategoryType(CategoryType.DATABASE);
        compareOptions.putSelectIgnoreCompare(true);
        compareOptions.putIgnoredTimePrecision(1000L);

        CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
        Assert.assertEquals(result.getLogs().size(), 2);
    }

    @Test
    public void testPrefixFilter() {
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
        String baseMsg = "{\"url\":\"http://pic.baidu.png\"}";
        String testMsg = "{\"url\":\"http://arex.pic.baidu.png\"}";

        CompareResult result = sdk.compare(baseMsg, testMsg);
        Assert.assertEquals(result.getLogs().size(), 0);
    }

    @Test
    public void testYearAndNsTimeIgnore() {
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
        String baseMsg = "{\"time\":\"2023-06-08 23:30:00.000\"}";
        String testMsg = "{\"time\":\"2023-06-08 23:30:00.999999\"}";

        CompareOptions compareOptions = CompareOptions.options();
        compareOptions.putIgnoredTimePrecision(1000L);

        CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
        Assert.assertEquals(result.getLogs().size(), 0);
    }

    @Test
    public void testNoYearAndNsTimeIgnore() {
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
        String baseMsg = "{\"time\":\"2023-06-08 23:30:00.000+08:00\"}";
        String testMsg = "{\"time\":\"2023-06-08T23:30:00.100Z\"}";

        CompareOptions compareOptions = CompareOptions.options();
        compareOptions.putIgnoredTimePrecision(1000L);

        CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
        Assert.assertEquals(result.getLogs().size(), 1);
    }

    @Test
    public void testMultiLevelArray() {
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
        String baseMsg = "{\"data\":[[{\"name\":\"test1\"},{\"name\":\"testa\"}], [{\"name\":\"testb\"},{\"name\":\"test2\"}]]}";
        String testMsg = "{\"data\":[[{\"name\":\"test2\"},{\"name\":\"testb\"}], [{\"name\":\"testa\"},{\"name\":\"test1\"}]]}";
        CompareOptions compareOptions = new CompareOptions();
        compareOptions.putListSortConfig(Arrays.asList("data"), Arrays.asList(Arrays.asList("name")));
        CompareResult result = sdk.compare(baseMsg, testMsg);
        Assert.assertEquals(result.getLogs().size(), 0);
    }
}
