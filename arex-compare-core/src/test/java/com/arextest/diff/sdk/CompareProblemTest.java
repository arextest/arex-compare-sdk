package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.DecompressConfig;
import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.enumeration.DiffResultCode;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testReplaceParse() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String baseMsg =
        "{\"body\":\"REPLACE INTO orderTable(OrderId, InfoId, DataChange_LastTime, userdata_location) VALUES "
            +
            "(36768383786, 36768317034, '2023-05-14 18:00:34.556', '')," +
            "(36768317034, 36768317034, '2023-05-14 18:00:34.556', '')\"}";
    String testMsg =
        "{\"body\":\"REPLACE INTO orderTable(OrderId, InfoId, DataChange_LastTime, userdata_location) VALUES "
            +
            "(36768383786, 36768317034, '2023-05-14 18:00:34.556', '')," +
            "(36768317034, 36768317034, '2023-05-14 18:00:34.656', '')\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);
    compareOptions.putSelectIgnoreCompare(true);
    compareOptions.putIgnoredTimePrecision(1000L);

    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
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
        "    \"body\": \"INSERT INTO `oy` (`sd`, `oe`, `od`, `pr`, `is`, `De`, `fd`) VALUES (?, ?, ?, ?, ?, ?, ?)\",\n"
        +
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
        "    \"body\": \"INSERT INTO `oy` (`sd`, `oe`, `od`, `pr`, `is`, `De`, `fd`, `un`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)\",\n"
        +
        "    \"dbname\": \"fb\"\n" +
        "}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);
    compareOptions.putSelectIgnoreCompare(true);
    compareOptions.putIgnoredTimePrecision(1000L);

    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(2, result.getLogs().size());
  }

  @Test
  public void testPrefixFilter() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String baseMsg = "{\"url\":\"http://pic.baidu.png\"}";
    String testMsg = "{\"url\":\"http://arex.pic.baidu.png\"}";

    CompareResult result = sdk.compare(baseMsg, testMsg);
    Assertions.assertEquals(0, result.getLogs().size());
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
    Assertions.assertEquals(0, result.getLogs().size());
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
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testMultiLevelArray() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String baseMsg = "{\"data\":[[{\"name\":\"test1\"},{\"name\":\"testa\"}], [{\"name\":\"testb\"},{\"name\":\"test2\"}]]}";
    String testMsg = "{\"data\":[[{\"name\":\"test2\"},{\"name\":\"testb\"}], [{\"name\":\"testa\"},{\"name\":\"test1\"}]]}";
    CompareOptions compareOptions = new CompareOptions();
    compareOptions.putListSortConfig(Arrays.asList("data"), Arrays.asList(Arrays.asList("name")));
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
  }


  @Test
  public void testSqlIgnore() {
    CompareSDK compareSDK = new CompareSDK();
    compareSDK.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String str1 = "{\"body\":\"EXEC cp_petowner @CreateTime='2017-09-07 20:46:24.877', @name=11\"}";
    String str2 = "{\"body\":\"EXEC cp_petowner @CreateTime='2017-09-07 20:46:24.977', @name=22\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putExclusions(Arrays.asList("parsedSql", "columns", "@CreateTime"));
    compareOptions.putCategoryType(CategoryType.DATABASE);

    CompareResult result = compareSDK.compare(str1, str2, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testKeyComputeCircleLoop() {
    CompareSDK compareSDK = new CompareSDK();
    compareSDK.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String str1 = "{\n" +
        "    \"studentList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"sex\": \"man\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"sex\": \"woman\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"nameList\": [\n" +
        "        {\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"name\": \"name2\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"name\": \"name1\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"studentInfoList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"studentName\": \"name1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"studentName\": \"name2\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"addressList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"address\": \"address1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"address\": \"address2\"\n" +
        "        }\n" +
        "    ]\n" +
        "}";
    String str2 = "{\n" +
        "    \"studentList\": [\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"sex\": \"woman\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"sex\": \"man\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"nameList\": [\n" +
        "        {\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"name\": \"name2\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"name\": \"name1\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"studentInfoList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"studentName\": \"name1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"studentName\": \"name2\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"addressList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"address\": \"address1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"address\": \"address2\"\n" +
        "        }\n" +
        "    ]\n" +
        "}";

    CompareOptions compareOptions = new CompareOptions();
    compareOptions.putListSortConfig(Arrays.asList("studentList"),
        Arrays.asList(Arrays.asList("alias")));
    compareOptions.putListSortConfig(Arrays.asList("nameList"),
        Arrays.asList(Arrays.asList("name")));
    compareOptions.putListSortConfig(Arrays.asList("studentInfoList"),
        Arrays.asList(Arrays.asList("id")));
    compareOptions.putListSortConfig(Arrays.asList("addressList"),
        Arrays.asList(Arrays.asList("id")));

    compareOptions.putReferenceConfig(Arrays.asList("studentInfoList", "id"),
        Arrays.asList("studentList", "id"));
    compareOptions.putReferenceConfig(Arrays.asList("studentList", "alias"),
        Arrays.asList("nameList", "alias"));
    compareOptions.putReferenceConfig(Arrays.asList("nameList", "name"),
        Arrays.asList("studentInfoList", "studentName"));

    CompareResult result = compareSDK.compare(str1, str2, compareOptions);
    Assertions.assertEquals(DiffResultCode.COMPARED_INTERNAL_EXCEPTION, result.getCode());
  }


  @Test
  public void testReferenceCircleLoop() {
    CompareSDK compareSDK = new CompareSDK();
    compareSDK.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String str1 = "{\n" +
        "    \"studentList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"name\": \"name1\",\n" +
        "            \"sex\": \"man\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"name\": \"name2\",\n" +
        "            \"sex\": \"woman\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"nameList\": [\n" +
        "        {\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"name\": \"name2\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"name\": \"name1\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"studentInfoList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"studentName\": \"name1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"studentName\": \"name2\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"addressList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"address\": \"address1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"address\": \"address2\"\n" +
        "        }\n" +
        "    ]\n" +
        "}";
    String str2 = "{\n" +
        "    \"studentList\": [\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"name\": \"name2\",\n" +
        "            \"sex\": \"woman\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"name\": \"name1\",\n" +
        "            \"sex\": \"man\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"nameList\": [\n" +
        "        {\n" +
        "            \"alias\": \"alias2\",\n" +
        "            \"name\": \"name2\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"alias\": \"alias1\",\n" +
        "            \"name\": \"name1\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"studentInfoList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"studentName\": \"name1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"studentName\": \"name2\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"addressList\": [\n" +
        "        {\n" +
        "            \"id\": 1,\n" +
        "            \"address\": \"address1\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": 2,\n" +
        "            \"address\": \"address2\"\n" +
        "        }\n" +
        "    ]\n" +
        "}";

    CompareOptions compareOptions = new CompareOptions();

    compareOptions.putReferenceConfig(Arrays.asList("studentInfoList", "id"),
        Arrays.asList("studentList", "id"));
    compareOptions.putReferenceConfig(Arrays.asList("studentList", "name"),
        Arrays.asList("studentInfoList", "studentName"));
    CompareResult result = compareSDK.compare(str1, str2, compareOptions);
    Assertions.assertEquals(4, result.getLogs().size());
  }

  @Test
  public void testNullEqualsEmptyProblem() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String baseMsg = "{\"body\":\"\"}";
    String testMsg = "{\"body\":null}";
    CompareResult result = sdk.compare(baseMsg, testMsg);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testNullString() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String baseMsg = null;
    String testMsg = "";
    CompareResult result = sdk.compare(baseMsg, testMsg);
    Assertions.assertEquals(1, result.getCode());
  }

  @Test
  public void testRootDecompress() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    CompareOptions compareOptions = CompareOptions.options()
        .putDecompressConfig(new DecompressConfig("Gzip",
            Arrays.asList(Arrays.asList("arex_root"))));

    String baseMsg = "H4sIAAAAAAAAAEtMTAQALXMH8AMAAAA=";
    String testMsg = "H4sIAAAAAAAAAEtMTEwEAEXlmK0EAAAA";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals("aaa", result.getProcessedBaseMsg());

    CompareResult quickResult = sdk.quickCompare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals("H4sIAAAAAAAAAEtMTAQALXMH8AMAAAA=", quickResult.getProcessedBaseMsg());
  }

  @Test
  public void testRootDecompress2() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    CompareOptions compareOptions = CompareOptions.options()
        .putDecompressConfig(new DecompressConfig("Gzip",
            Arrays.asList(Arrays.asList("arex_root"))));

    String baseMsg = null;
    String testMsg = "H4sIAAAAAAAAAEtMTEwEAEXlmK0EAAAA";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals("aaaa", result.getProcessedTestMsg());

    CompareResult quickResult = sdk.quickCompare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals("H4sIAAAAAAAAAEtMTEwEAEXlmK0EAAAA", quickResult.getProcessedTestMsg());

  }

  @Test
  public void testRootDecompress3() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    CompareOptions compareOptions = CompareOptions.options()
        .putDecompressConfig(new DecompressConfig("Gzip",
            Arrays.asList(Arrays.asList("arex_root"))));

    String baseMsg = "H4sIAAAAAAAAAKtWSlSyUkpSqgUAnFz2awkAAAA=";
    String testMsg = "H4sIAAAAAAAAAKtWSlSyAuJaAMXisGkJAAAA";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(1, result.getCode());

    CompareResult quickResult = sdk.quickCompare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals("H4sIAAAAAAAAAKtWSlSyAuJaAMXisGkJAAAA",
        quickResult.getProcessedTestMsg());

  }

  @Test
  public void testRootDecompress4() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    CompareOptions compareOptions = CompareOptions.options()
        .putDecompressConfig(new DecompressConfig("Gzip",
            Arrays.asList(Arrays.asList("arex_root"))));

    String baseMsg = "H4sIAAAAAAAAAKtOTEwEAL5SPJIEAAAA";
    String testMsg = "H4sIAAAAAAAAAKtOBAIAmhz8xAUAAAA=";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(1, result.getCode());

    CompareResult quickResult = sdk.quickCompare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals("H4sIAAAAAAAAAKtOBAIAmhz8xAUAAAA=", quickResult.getProcessedTestMsg());

  }

  @Test
  public void testListSortNullPointer() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);

    CompareOptions compareOptions = CompareOptions.options()
        .putListSortConfig(Arrays.asList("refundapplyresultlist"),
            Arrays.asList(Arrays.asList("reorderid")))
        .putListSortConfig(Arrays.asList("parameters"),
            Arrays.asList(Arrays.asList("seq", "pname", "reorderid")))
        .putReferenceConfig(Arrays.asList("parameters", "stype"),
            Arrays.asList("parameters", "stype"));

    String baseMsg = "{\n"
        + "    \"parameters\": [\n"
        + "        {\n"
        + "            \"seq\": 1,\n"
        + "            \"afford\": true,\n"
        + "            \"latest\": \"String\",\n"
        + "            \"orderid\": 1,\n"
        + "            \"stype\": 1,\n"
        + "            \"sstatus\": 1,\n"
        + "            \"pname\": \"String\",\n"
        + "            \"reorderid\": 1,\n"
        + "            \"rebookingid\": 1,\n"
        + "            \"cancan\": true\n"
        + "        }\n"
        + "    ],\n"
        + "    \"body\": \"String\",\n"
        + "    \"dbname\": \"String\",\n"
        + "    \"parsedsql\": [\n"
        + "        {\n"
        + "            \"columns\": [\n"
        + "                {\n"
        + "                    \"`latest`\": \"String\",\n"
        + "                    \"`afford`\": \"String\",\n"
        + "                    \"`rebookingid`\": \"String\",\n"
        + "                    \"`seq`\": \"String\",\n"
        + "                    \"`pname`\": \"String\",\n"
        + "                    \"`reorderid`\": \"String\",\n"
        + "                    \"`sstatus`\": \"String\",\n"
        + "                    \"`orderid`\": \"String\",\n"
        + "                    \"`cancan`\": \"String\",\n"
        + "                    \"`stype`\": \"String\"\n"
        + "                }\n"
        + "            ],\n"
        + "            \"action\": \"String\",\n"
        + "            \"table\": \"String\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"dbName\": \"String\"\n"
        + "}";
    String testMsg = "{\n"
        + "    \"parameters\": [\n"
        + "        {\n"
        + "            \"seq\": 2,\n"
        + "            \"afford\": true,\n"
        + "            \"latest\": \"String\",\n"
        + "            \"orderid\": 1,\n"
        + "            \"stype\": 1,\n"
        + "            \"sstatus\": 1,\n"
        + "            \"pname\": \"String\",\n"
        + "            \"reorderid\": 1,\n"
        + "            \"rebookingid\": 1,\n"
        + "            \"cancan\": true\n"
        + "        }\n"
        + "    ],\n"
        + "    \"body\": \"String\",\n"
        + "    \"dbname\": \"String\",\n"
        + "    \"parsedsql\": [\n"
        + "        {\n"
        + "            \"columns\": [\n"
        + "                {\n"
        + "                    \"`latest`\": \"String\",\n"
        + "                    \"`afford`\": \"String\",\n"
        + "                    \"`rebookingid`\": \"String\",\n"
        + "                    \"`seq`\": \"String\",\n"
        + "                    \"`pname`\": \"String\",\n"
        + "                    \"`reorderid`\": \"String\",\n"
        + "                    \"`sstatus`\": \"String\",\n"
        + "                    \"`orderid`\": \"String\",\n"
        + "                    \"`cancan`\": \"String\",\n"
        + "                    \"`stype`\": \"String\"\n"
        + "                }\n"
        + "            ],\n"
        + "            \"action\": \"String\",\n"
        + "            \"table\": \"String\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"dbName\": \"String\"\n"
        + "}";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testStringEqualsWithArexPrefix() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    CompareOptions compareOptions = CompareOptions.options()
        .putDecompressConfig(new DecompressConfig("Base64",
            Arrays.asList(Arrays.asList("arex_root"))));

    String baseMsg = "aHR0cHM6Ly9iYWlkdS5jb20=";
    String testMsg = "aHR0cHM6Ly9hcmV4LmJhaWR1LmNvbQ==";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, result.getCode());
  }

  @Disabled
  @Test
  public void testCpuUsed() throws IOException {
    CompareSDK compareSDK = new CompareSDK();
    compareSDK.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true).putIgnoreNodeSet(
        Collections.singleton("timestamp"));

    CompareOptions compareOptions = CompareOptions.options().putListSortConfig(
        Arrays.asList("fareinfolist"),
        Arrays.asList(Arrays.asList("id"))
    );

    String baseMsg = readStringFromFile("./baseDecompress.txt");
    String testMsg = readStringFromFile("./testDecompress.txt");
    CompareResult result = compareSDK.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, result.getCode());

  }

  // reading a string from a file
  public static String readStringFromFile(String filePath) throws IOException {
    FileInputStream fis = new FileInputStream(filePath);
    byte[] buffer = new byte[1024];
    StringBuilder sb = new StringBuilder();
    int bytesRead;
    while ((bytesRead = fis.read(buffer)) != -1) {
      sb.append(new String(buffer, 0, bytesRead));
    }
    fis.close();
    return sb.toString();
  }

  @Test
  public void testArexPrefixFilter() {
    CompareSDK sdk = new CompareSDK();
    CompareOptions compareOptions = CompareOptions.options();
    String baseMsg = "{\n"
        + "    \"time\": \"2024-10-18 14:32:26\"\n"
        + "}";
    String testMsg = "{\n"
        + "    \"time\": \"2024-10-18 14:32:26xxxxx\"\n"
        + "}";
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(1, result.getCode());
  }


  @Test
  public void testArrayListMissingExpressMissing() {
    CompareSDK sdk = new CompareSDK();
    String baseMsg = "{\n"
        + "    \"students\": [\n"
        + "        {\n"
        + "            \"key\": \"xiaoming\",\n"
        + "            \"value\": \"18\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"key\": \"xiaomei\",\n"
        + "            \"value\": \"19\"\n"
        + "        }\n"
        + "    ]\n"
        + "}";
    String testMsg = "{\n"
        + "    \"students\": [\n"
        + "        {\n"
        + "            \"key\": \"xiaomei\",\n"
        + "            \"value\": \"18\"\n"
        + "        }\n"
        + "    ]\n"
        + "}";

    CompareOptions compareOptions = CompareOptions.options()
        .putListSortConfig(Arrays.asList("students"), Arrays.asList(Arrays.asList("key")))
        .putExclusions(Arrays.asList("students", "[key=xiaoming]"))
//        .putExclusions(Arrays.asList("students","[index=0]"));
        ;
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }


  @Test
  public void testConditionIgnore() {
    CompareSDK sdk = new CompareSDK();
    String baseMsg = "{\n"
        + "    \"students\": [\n"
        + "        {\n"
        + "            \"key\": \"xiaoming\",\n"
        + "            \"value\": \"18\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"key\": \"xiaomei\",\n"
        + "            \"value\": \"19\"\n"
        + "        }\n"
        + "    ]\n"
        + "}";
    String testMsg = "{\n"
        + "    \"students\": [\n"
        + "        {\n"
        + "            \"key\": \"xiaomei\",\n"
        + "            \"value\": \"18\"\n"
        + "        }\n"
        + "    ]\n"
        + "}";

    CompareOptions compareOptions = CompareOptions.options()
        .putListSortConfig(Arrays.asList("students"), Arrays.asList(Arrays.asList("key")))
        .putExclusions(Arrays.asList("students", "[key=xiaoming]"));
    CompareResult result = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testArexPrefix() {
    CompareSDK compareSDK = new CompareSDK();
    String baseMsg = "{\n"
        + "    \"time\": \"2024-10-18 14:32:26\"\n"
        + "}";

    String testMsg = "{\n"
        + "    \"time\": \"2024-10-18 14:32:26xxxxx\"\n"
        + "}";
    CompareResult result = compareSDK.compare(baseMsg, testMsg);
    Assertions.assertEquals(1, result.getCode());
  }


}
