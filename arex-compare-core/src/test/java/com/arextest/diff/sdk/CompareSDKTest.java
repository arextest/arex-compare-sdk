package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.DecompressConfig;
import com.arextest.diff.model.TransformConfig;
import com.arextest.diff.model.TransformConfig.TransformMethod;
import com.arextest.diff.model.enumeration.CategoryType;
import com.arextest.diff.model.script.ScriptCompareConfig;
import com.arextest.diff.model.script.ScriptCompareConfig.ScriptMethod;
import com.arextest.diff.model.script.ScriptContentInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CompareSDKTest {

  @Test
  public void testCompare() throws Exception {
    long start = System.currentTimeMillis();
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    String str1 = "{\"address\":\"add\",\"name\":null,"
        + "\"family\":[{\"id\":1,\"subject\":{\"mother\":\"B\",\"father\":\"A\",\"brother\":\"F\",\"sister\":\"D\"},\"bug\":{\"helper\":\"1\"},\"list\":[\"1\",\"2\"]},"
        + "{\"id\":2,\"subject\":{\"mother\":\"A\",\"father\":\"F\",\"brother\":\"C\",\"sister\":\"E\"},\"bug\":{\"helper\":\"2\"},\"list\":[\"1\",\"2\"]}],"
        + "\"subObj\":\"H4sIAAAAAAAAAKtWys3PK8lQslIyVdJRqkxNLAIyjQyMDIG8lMRKIMfQXKkWAMavr8AmAAAA\","
        + "\"alist\":[{\"aid\":{\"id\":1},\"test\":[{\"subject\":\"1\"}]},{\"aid\":{\"id\":2},\"test\":[{\"subject\":\"2\"}],\"addtion\":\"ad\"}],\"nullList\":[null],\"age\":18}";

    String str2 = "{\"address\":\"add\",\"name\":null,"
        + "\"family\":[{\"id\":3,\"subject\":{\"mother\":\"A\",\"father\":\"F\",\"brother\":\"C\",\"sister\":\"E\"},\"bug\":{\"helper\":\"2\"},\"list\":[\"1\",\"2\"]},"
        + "{\"id\":1,\"subject\":{\"mother\":\"B\",\"father\":\"A\",\"brother\":\"C\",\"sister\":\"E\"},\"bug\":{\"helper\":\"1\"},\"list\":[\"1\",\"2\"]}],"
        + "\"subObj\":\"H4sIAAAAAAAAAKtWys3PK8lQslIyVdJRqkxNLAIyjQyMDIG8lMRKIMfQXKkWAMavr8AmAAAA\","
        + "\"alist\":[{\"aid\":{\"id\":3},\"test\":[{\"subject\":\"1\"}]},{\"aid\":{\"id\":1},\"test\":[{\"subject\":\"2\"}],\"addtion\":\"ad\"}],\"nullList\":[1],\"age\":17}";

    CompareOptions compareOptions = CompareOptions.options()
        .putReferenceConfig(Arrays.asList("alist", "aid", "id"), Arrays.asList("family", "id"))
        .putListSortConfig(new HashMap<List<String>, List<List<String>>>() {
          {
            put(Arrays.asList("family"), Arrays.asList(Arrays.asList("subject", "mother"),
                Arrays.asList("subject", "father")));
            put(Arrays.asList("alist"), Arrays.asList(Arrays.asList("aid", "id")));
          }
        })
        .putDecompressConfig(new DecompressConfig("Gzip", Arrays.asList(Arrays.asList("subObj"))))
        .putExclusions(Arrays.asList(Arrays.asList("family", "mother"), Arrays.asList("age")));
    // .putInclusions(Arrays.asList(Arrays.asList("alist", "test"), Arrays.asList("nullList")));

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    long end = System.currentTimeMillis();
    Assertions.assertEquals(7, result.getLogs().size());
    System.out.println("toatal cost:" + (end - start) + " ms");
  }

  @Test
  public void testSQLCompare() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);

    //region json msg
    String str1 = "{\n" +
        "    \"database\": \"htlgroupproductdb_dalcluster\",\n" +
        "    \"parameters\": [\n" +
        "        {\n" +
        "            \"11\": 0,\n" +
        "            \"1\": 1026268,\n" +
        "            \"12\": 492752329,\n" +
        "            \"2\": \"外观\",\n" +
        "            \"3\": \"\",\n" +
        "            \"4\": \"\",\n" +
        "            \"5\": \"外观\",\n" +
        "            \"6\": 0,\n" +
        "            \"7\": \"/0206f120009irgqljCA50.jpg\",\n" +
        "            \"8\": 100,\n" +
        "            \"9\": \"H\",\n" +
        "            \"10\": 0\n" +
        "        }\n" +
        "    ],\n" +
        "    \"body\": \"UPDATE `hotelpicture` SET `hotelid`=?, `title`=?, `smallpicurl`=?, `largepicurl`=?, `description`=?, `sort`=?, `newpicurl`=?, `pictype`=?, `position`=?, `typeid`=?, `sharpness`=? WHERE `id`=?\"\n"
        +
        "}";

    String str2 = "{\n" +
        "    \"database\": \"htlgroupproductdb_dalcluster\",\n" +
        "    \"parameters\": [\n" +
        "        {\n" +
        "            \"11\": null,\n" +
        "            \"1\": 1026268,\n" +
        "            \"12\": 492752329,\n" +
        "            \"2\": \"外观\",\n" +
        "            \"3\": \"\",\n" +
        "            \"4\": \"\",\n" +
        "            \"5\": \"外观\",\n" +
        "            \"6\": 0,\n" +
        "            \"7\": \"/0206f120009irgqljCA50.jpg\",\n" +
        "            \"8\": 100,\n" +
        "            \"9\": \"H\",\n" +
        "            \"10\": 0\n" +
        "        }\n" +
        "    ],\n" +
        "    \"body\": \"UPDATE `hotelpicture` SET `hotelid`=?, `title`=?, `smallpicurl`=?, `largepicurl`=?, `description`=?, `sort`=?, `newpicurl`=?, `pictype`=?, `position`=?, `typeid1`=?, `sharpness`=? WHERE `id`=?\"\n"
        +
        "}";
    //endregion

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);
    compareOptions.putOnlyCompareCoincidentColumn(true);
    compareOptions.putSelectIgnoreCompare(true);

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testTimePrecisionFilter() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putIgnoredTimePrecision(1000);

    String str1 = "{\"time\":\"2022-05-27T15:35:37.213+0800\"}";

    String str2 = "{\"time\":\"2022-05-27T15:35:37.223+0800\"}";

    CompareOptions compareOptions = CompareOptions.options();

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testTimePrecisionFilter2() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putIgnoredTimePrecision(2000);

    String str1 = "{\"time\":\"15:35:37\"}";

    String str2 = "{\"time\":\"15:35:39\"}";

    CompareOptions compareOptions = CompareOptions.options();

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testNullAndNotExist() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNullEqualsNotExist(true);
    String str1 = "{\"array\":null}";
    String str2 = "{\"arr\":null}";
    CompareOptions compareOptions = CompareOptions.options();
    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testArexPrefixFilter() {
    CompareSDK sdk = new CompareSDK();
    String str1 = "{\"array\":\"http://www.baidu.com\"}";
    String str2 = "{\"array\":\"http://arex_www.baidu.com\"}";
    CompareResult result = sdk.compare(str1, str2);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testUuidFilter() {
    CompareSDK sdk = new CompareSDK();
    String str1 = "{\"uuid\":\"f4c6d9c9-9d8f-4b1f-9d5c-6e9d7a8c6b2e\"}";
    String str2 = "{\"uuid\":\"f4c6d9c9-9d8f-4b1f-9d5c-6e9d7a8c6b2f\"}";
    CompareResult result = sdk.compare(str1, str2);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testIgnoreSelectSql() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);

    String str1 =
        "{\"dbname\":\"dbmame\",\"body\":\"SELECT e.c3, e.c4, e.c5 FROM t1 e JOIN t2 d USING (id) "
            +
            "WHERE c2 = 'SA_REP' AND c6 = 2500  ORDER BY e.c3 FOR UPDATE OF e ;\"}";

    String str2 =
        "{\"dbname\":\"dbmame\",\"body\":\"SELECT e.c3, e.c4, e.c5 FROM t1 e JOIN t2 d USING (id)" +
            " WHERE c2 = 'SA_REP' AND c6 = 2500  ORDER BY e.c3 FOR UPDATE OF e ;\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);
    compareOptions.putSelectIgnoreCompare(true);

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getCode());
  }

  @Test
  public void testDecompress() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");
    String str1 = "{\"content\":\"eyJhIjoiMSJ9\"}";
    String str2 = "{\"content\":\"eyJhIjoiMiJ9\"}";
    CompareOptions options = CompareOptions.options();
    DecompressConfig decompressConfig = new DecompressConfig();
    decompressConfig.setNodePath(Arrays.asList(Arrays.asList("content")));
    decompressConfig.setName("Base64");
    options.putDecompressConfig(decompressConfig);

    CompareResult compare = sdk.compare(str1, str2, options);
    System.out.println();
  }

  @Test
  public void testCompare1() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsNotExist(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");
    String str1 = "{\"content\":null}";
    String str2 = "{}";
    CompareOptions options = CompareOptions.options();
    DecompressConfig decompressConfig = new DecompressConfig();
    decompressConfig.setNodePath(Arrays.asList(Arrays.asList("content")));
    decompressConfig.setName("Base64");
    options.putDecompressConfig(decompressConfig);

    CompareResult compare = sdk.compare(str1, str2, options);
    System.out.println();
  }

  @Test
  public void testQuickCompare() {
    long start = System.currentTimeMillis();
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    String str1 = "{\"address\":\"add\",\"name\":null,"
        + "\"family\":[{\"id\":1,\"subject\":{\"mother\":\"B\",\"father\":\"A\",\"brother\":\"F\",\"sister\":\"D\"},\"bug\":{\"helper\":\"1\"},\"list\":[\"1\",\"2\"]},"
        + "{\"id\":2,\"subject\":{\"mother\":\"A\",\"father\":\"F\",\"brother\":\"C\",\"sister\":\"E\"},\"bug\":{\"helper\":\"2\"},\"list\":[\"1\",\"2\"]}],"
        + "\"subObj\":\"H4sIAAAAAAAAAKtWys3PK8lQslIyVdJRqkxNLAIyjQyMDIG8lMRKIMfQXKkWAMavr8AmAAAA\","
        + "\"alist\":[{\"aid\":{\"id\":1},\"test\":[{\"subject\":\"1\"}]},{\"aid\":{\"id\":2},\"test\":[{\"subject\":\"2\"}],\"addtion\":\"ad\"}],\"nullList\":[null],\"age\":18}";

    String str2 = "{\"address\":\"add\",\"name\":null,"
        + "\"family\":[{\"id\":3,\"subject\":{\"mother\":\"A\",\"father\":\"F\",\"brother\":\"C\",\"sister\":\"E\"},\"bug\":{\"helper\":\"2\"},\"list\":[\"1\",\"2\"]},"
        + "{\"id\":1,\"subject\":{\"mother\":\"B\",\"father\":\"A\",\"brother\":\"C\",\"sister\":\"E\"},\"bug\":{\"helper\":\"1\"},\"list\":[\"1\",\"2\"]}],"
        + "\"subObj\":\"H4sIAAAAAAAAAKtWys3PK8lQslIyVdJRqkxNLAIyjQyMDIG8lMRKIMfQXKkWAMavr8AmAAAA\","
        + "\"alist\":[{\"aid\":{\"id\":3},\"test\":[{\"subject\":\"1\"}]},{\"aid\":{\"id\":1},\"test\":[{\"subject\":\"2\"}],\"addtion\":\"ad\"}],\"nullList\":[1],\"age\":17}";

    CompareOptions compareOptions = CompareOptions.options()
        .putReferenceConfig(Arrays.asList("alist", "aid", "id"), Arrays.asList("family", "id"))
        .putListSortConfig(new HashMap<List<String>, List<List<String>>>() {
          {
            put(Arrays.asList("family"), Arrays.asList(Arrays.asList("subject", "mother"),
                Arrays.asList("subject", "father")));
            put(Arrays.asList("alist"), Arrays.asList(Arrays.asList("aid", "id")));
          }
        })
        .putDecompressConfig(new DecompressConfig("Gzip", Arrays.asList(Arrays.asList("subObj"))))
        .putExclusions(Arrays.asList(Arrays.asList("family", "mother"), Arrays.asList("age")));

    CompareResult result = sdk.quickCompare(str1, str2, compareOptions);
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }

  @Test
  public void testProcedureExec() {
    CompareSDK compareSDK = new CompareSDK();
    compareSDK.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String str1 = "{\"body\":\"EXEC cp_petowner @CreateTime='2017-09-07 20:46:24.877'\"}";
    String str2 = "{\"body\":\"EXEC cp_petowner @CreateTime='2017-09-07 20:46:24.977'\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);

    CompareResult result = compareSDK.compare(str1, str2, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void addRootCompress() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    String str1 = "H4sIAAAAAAAAAKtWykvMTVWyUkrPz0/PSVWqBQBU4FMBEQAAAA==";
    String str2 = "H4sIAAAAAAAAAKtWykvMTVWyUkpKzEwpVaoFANs7JyUQAAAA";

    CompareOptions compareOptions = CompareOptions.options()
        .putDecompressConfig(
            new DecompressConfig("Gzip", Arrays.asList(Arrays.asList("arex_root"))));

    CompareResult result = sdk.compare(str1, str2, compareOptions);

    Assertions.assertEquals("{\"name\":\"google\"}", result.getProcessedBaseMsg());
  }

  @Test
  public void testSqlBodyNull() {
    CompareSDK compareSDK = new CompareSDK();
    compareSDK.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);
    String str1 = "{\"table\":\"cp_petowner\"}";
    String str2 = "{\"body\":\"EXEC cp_petowner @CreateTime='2017-09-07 20:46:24.977'\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);

    CompareResult result = compareSDK.compare(str1, str2, compareOptions);
    Assertions.assertEquals(2, result.getLogs().size());

  }

  @Test
  public void testGlobalOptionsSelectIgnoreCompare() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putSelectIgnoreCompare(true);

    String str1 =
        "{\"dbname\":\"dbmame\",\"body\":\"SELECT e.c3, e.c4, e.c5 FROM t1 e JOIN t2 d USING (id) "
            +
            "WHERE c2 = 'SA_REP' AND c6 = 2500  ORDER BY e.c3 FOR UPDATE OF e ;\"}";

    String str2 =
        "{\"dbname\":\"dbmame\",\"body\":\"SELECT e.c3, e.c4, e.c5 FROM t1 e JOIN t2 d USING (id)" +
            " WHERE c2 = 'SA_REP' AND c6 = 2300  ORDER BY e.c3 FOR UPDATE OF e ;\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getCode());
  }

  @Test
  public void testGlobalOptionsOnlyCompareCoincidentColumn() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putOnlyCompareCoincidentColumn(true);

    String str1 = "{\"dbname\":\"dbmame\",\"body\":\"REPLACE INTO "
        + "orderTable(OrderId, InfoId, DataChange_LastTime, userdata_location) "
        + "VALUES (36768383786, 36768317034, '2023-05-14 18:00:34.556', '')\"}";

    String str2 = "{\"dbname\":\"dbmame\",\"body\":\"REPLACE INTO "
        + "orderTable(OrderId, InfoId, DataChange_LastTime, userdata_location1) "
        + "VALUES (36768383786, 36768317034, '2023-05-14 18:00:34.556', '')\"}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getCode());
  }

  @Test
  public void testGlobalOptionsLowerUuidIgnore() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putUuidIgnore(true);

    String str1 = "{\"uuid\":\"5a6798c4-e57c-481d-b6b7-00f5866350c0\"}";
    String str2 = "{\"uuid\":\"41cd4916-9ff5-413e-812a-5f620e2ae589\"}";

    CompareResult result = sdk.compare(str1, str2);
    Assertions.assertEquals(0, result.getCode());
  }

  @Test
  public void testGlobalOptionsUpperUuidIgnore() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putUuidIgnore(true);

    String str1 = "{\"uuid\":\"5A6798C4-E57C-481D-B6B7-00F5866350C0\"}";
    String str2 = "{\"uuid\":\"41CD4916-9FF5-413E-812A-5F620E2AE589\"}";

    CompareResult result = sdk.compare(str1, str2);
    Assertions.assertEquals(0, result.getCode());
  }


  @Test
  public void testWhiteListObj() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true);

    //region json msg
    String str1 = "{\n"
        + "    \"response\": {\n"
        + "        \"students\": [\n"
        + "            {\n"
        + "                \"info\": {\n"
        + "                    \"name\": \"xiaomi\",\n"
        + "                    \"roomName\": \"A\"\n"
        + "                },\n"
        + "                \"age\": 18\n"
        + "            },\n"
        + "            {\n"
        + "                \"info\": {\n"
        + "                    \"name\": \"apple\",\n"
        + "                    \"roomName\": \"A\"\n"
        + "                },\n"
        + "                \"age\": 19\n"
        + "            }\n"
        + "        ]\n"
        + "    }\n"
        + "}";

    String str2 = "{\n"
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
        + "                \"age\": 20\n"
        + "            }\n"
        + "        ]\n"
        + "    }\n"
        + "}";
    //endregion

    CompareOptions compareOptions = CompareOptions.options()
        .putInclusions(
            Arrays.asList(Arrays.asList("response", "Students", "info", "name")));

    CompareResult result = sdk.quickCompare(str1, str2, compareOptions);

    System.out.println();
  }

  @Test
  public void testExpressionNodeIgnore() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true);

    //region json msg
    String str1 = "{\n"
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
        + "            }\n"
        + "        ],\n"
        + "        \"region\": \"shanghai\"\n"
        + "    }\n"
        + "}";

    String str2 = "{\n"
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
        + "                    \"name\": \"tiktok\"\n"
        + "                },\n"
        + "                \"age\": 20\n"
        + "            }\n"
        + "        ],\n"
        + "        \"region\": \"beijing\"\n"
        + "    }\n"
        + "}";
    //endregion

    CompareOptions compareOptions = CompareOptions.options()
        .putExclusions(
            Arrays.asList(Arrays.asList("response", "Students", "[info/Name=apple]", "age"))
        )
        .putExclusions(
            Arrays.asList(Arrays.asList("response", "Region"))
        );

    CompareResult result = sdk.quickCompare(str1, str2, compareOptions);
    System.out.println();
  }

  @Test
  public void testPrimitiveArrayNodeAutoSort() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true);

    String str1 = "{\n"
        + "    \"classroom\": [\n"
        + "        \"classroomA\",\n"
        + "        \"classroomB\",\n"
        + "        \"classroomA\",\n"
        + "        \"classroomC\"\n"
        + "    ]\n"
        + "}";

    String str2 = "{\n"
        + "    \"classroom\": [\n"
        + "        \"classroomB\",\n"
        + "        \"classroomA\",\n"
        + "        \"classroomA\",\n"
        + "        \"classroomC\"\n"
        + "    ]\n"
        + "}";

    CompareResult result = sdk.compare(str1, str2);
    Assertions.assertEquals(0, result.getCode());
  }

  @Test
  public void testIpFilter() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putIpIgnore(true);

    String str1 = "{\"ip\":\"0001:0:0:0:0:0:0:1\"}";
    String str2 = "{\"ip\":\"0:0:0:0:0:0:0:1\"}";

    CompareResult result = sdk.compare(str1, str2);
    Assertions.assertEquals(0, result.getCode());
  }


  @Test
  public void testListKeyWithPrefix() throws Exception {
    long start = System.currentTimeMillis();
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true);

    String str1 = "{\n"
        + "    \"key\": \"testListKeyWithPrefix\",\n"
        + "    \"weblist\": [\n"
        + "        \"http://www.baidu.com\",\n"
        + "        \"http://www.didi.com\",\n"
        + "        \"http://www.taobao.com\"\n"
        + "    ]\n"
        + "}";

    String str2 = "{\n"
        + "    \"key\": \"testListKeyWithPrefix\",\n"
        + "    \"weblist\": [\n"
        + "        \"http://www.arex.baidu.com\",\n"
        + "        \"http://www.arex.taobao.com\",\n"
        + "        \"http://www.arex.didi.com\"\n"
        + "    ]\n"
        + "}";

    CompareOptions compareOptions = CompareOptions.options()
        .putListSortConfig(new HashMap<List<String>, List<List<String>>>() {
          {
            put(Arrays.asList("weblist"), Arrays.asList(Arrays.asList("%value%")));
          }
        });
    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getCode());
  }


  @Test
  public void testReferenceWithPrefix() throws Exception {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true);

    //region json msg
    String str1 = "{\n"
        + "    \"alist\": [\n"
        + "        {\n"
        + "            \"aid\": {\n"
        + "                \"id\": \"1\"\n"
        + "            },\n"
        + "            \"test\": [\n"
        + "                {\n"
        + "                    \"subject\": \"1\"\n"
        + "                }\n"
        + "            ]\n"
        + "        },\n"
        + "        {\n"
        + "            \"aid\": {\n"
        + "                \"id\": \"2\"\n"
        + "            },\n"
        + "            \"test\": [\n"
        + "                {\n"
        + "                    \"subject\": \"2\"\n"
        + "                }\n"
        + "            ],\n"
        + "            \"addtion\": \"ad\"\n"
        + "        }\n"
        + "    ],\n"
        + "    \"family\": [\n"
        + "        {\n"
        + "            \"id\": \"1\",\n"
        + "            \"subject\": {\n"
        + "                \"mother\": \"B\",\n"
        + "                \"father\": \"A\",\n"
        + "                \"brother\": \"F\",\n"
        + "                \"sister\": \"D\"\n"
        + "            },\n"
        + "            \"bug\": {\n"
        + "                \"helper\": \"1\"\n"
        + "            },\n"
        + "            \"list\": [\n"
        + "                \"1\",\n"
        + "                \"2\"\n"
        + "            ]\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": \"2\",\n"
        + "            \"subject\": {\n"
        + "                \"mother\": \"A\",\n"
        + "                \"father\": \"F\",\n"
        + "                \"brother\": \"C\",\n"
        + "                \"sister\": \"E\"\n"
        + "            },\n"
        + "            \"bug\": {\n"
        + "                \"helper\": \"2\"\n"
        + "            },\n"
        + "            \"list\": [\n"
        + "                \"1\",\n"
        + "                \"2\"\n"
        + "            ]\n"
        + "        }\n"
        + "    ]\n"
        + "}";

    String str2 = "{\n"
        + "    \"alist\": [\n"
        + "        {\n"
        + "            \"aid\": {\n"
        + "                \"id\": \"arex_a2\"\n"
        + "            },\n"
        + "            \"test\": [\n"
        + "                {\n"
        + "                    \"subject\": \"2\"\n"
        + "                }\n"
        + "            ],\n"
        + "            \"addtion\": \"ad\"\n"
        + "        },\n"
        + "        {\n"
        + "            \"aid\": {\n"
        + "                \"id\": \"a1\"\n"
        + "            },\n"
        + "            \"test\": [\n"
        + "                {\n"
        + "                    \"subject\": \"1\"\n"
        + "                }\n"
        + "            ]\n"
        + "        }\n"
        + "    ],\n"
        + "    \"family\": [\n"
        + "        {\n"
        + "            \"id\": \"a1\",\n"
        + "            \"subject\": {\n"
        + "                \"mother\": \"B\",\n"
        + "                \"father\": \"A\",\n"
        + "                \"brother\": \"F\",\n"
        + "                \"sister\": \"D\"\n"
        + "            },\n"
        + "            \"bug\": {\n"
        + "                \"helper\": \"1\"\n"
        + "            },\n"
        + "            \"list\": [\n"
        + "                \"1\",\n"
        + "                \"2\"\n"
        + "            ]\n"
        + "        },\n"
        + "        {\n"
        + "            \"id\": \"arex_a2\",\n"
        + "            \"subject\": {\n"
        + "                \"mother\": \"A\",\n"
        + "                \"father\": \"F\",\n"
        + "                \"brother\": \"C\",\n"
        + "                \"sister\": \"E\"\n"
        + "            },\n"
        + "            \"bug\": {\n"
        + "                \"helper\": \"2\"\n"
        + "            },\n"
        + "            \"list\": [\n"
        + "                \"1\",\n"
        + "                \"2\"\n"
        + "            ]\n"
        + "        }\n"
        + "    ]\n"
        + "}";
    //endregion

    CompareOptions compareOptions = CompareOptions.options()
        .putReferenceConfig(Arrays.asList("alist", "aid", "id"), Arrays.asList("family", "id"))
        .putListSortConfig(new HashMap<List<String>, List<List<String>>>() {
          {
            put(Arrays.asList("family"), Arrays.asList(Arrays.asList("subject", "mother"),
                Arrays.asList("subject", "father")));
            put(Arrays.asList("alist"), Arrays.asList(Arrays.asList("aid", "id")));
          }
        });

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testTransFormConfigTest() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions()
        .putNameToLower(true)
        .putNullEqualsEmpty(true)
        .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

    String str1 = "{\n"
        + "    \"subObj\": \"SDRzSUFBQUFBQUFBQUt0V3lzM1BLOGxRc2xJeVZkSlJxa3hOTEFJeWpReU1ESUc4bE1SS0lNZlFYS2tXQU1hdnI4QW1BQUFB\"\n"
        + "}";

    String str2 = "{\n"
        + "    \"subObj\": \"SDRzSUFBQUFBQUFBQUt0V3lzM1BLOGxRc2xJeVZkSlJxa3hOTEFJeWpReU1ESUc4bE1SS0lNZlFRcWtXQVB2bzg4c21BQUFB\"\n"
        + "}";

    TransformMethod transformMethodZstd = new TransformMethod("Base64", "");
    TransformMethod transformMethodGzip = new TransformMethod("Gzip", null);

    CompareOptions compareOptions = CompareOptions.options();

    compareOptions.putTransformConfig(new TransformConfig(Arrays.asList(Arrays.asList("subObj")),
        Arrays.asList(transformMethodZstd, transformMethodGzip)));

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testParmeter() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putOnlyCompareCoincidentColumn(true);

    String str1 = "{\n"
        + "  \"parameters\": {\n"
        + "    \"parammap\": \"{\\\"link_type\\\":1,\\\"token\\\":\\\"1\\\"}\",\n"
        + "    \"tablename\": \"table\"\n"
        + "  },\n"
        + "  \"body\": \"insert into log (table_name, param_map) values (?, ?)\",\n"
        + "  \"dbname\": \"\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";

    String str2 = "{\n"
        + "  \"parameters\": {\n"
        + "    \"parammap\": \"{\\\"link_type\\\":1}\",\n"
        + "    \"tablename\": \"table\"\n"
        + "  },\n"
        + "  \"body\": \"insert into log (table_name, param_map) values (?, ?)\",\n"
        + "  \"dbname\": \"\"\n"
        + "    }\n"
        + "  ]\n"
        + "}";

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);
    compareOptions.putOnlyCompareCoincidentColumn(true);

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(1, result.getLogs().size());
  }

  @Test
  public void testOnlyCompareExistListElements() {
    CompareSDK sdk = new CompareSDK();
//    sdk.getGlobalOptions().putOnlyCompareExistListElements(true);

    String str1 = "{\n"
        + "    \"resultList\": [\n"
        + "        {\n"
        + "            \"student\": \"stuA\",\n"
        + "            \"age\": 18\n"
        + "        },\n"
        + "        {\n"
        + "            \"student\": \"stuB\",\n"
        + "            \"age\": 19,\n"
        + "            \"sex\": 0\n"
        + "        }\n"
        + "    ]\n"
        + "}";

    String str2 = "{\n"
        + "    \"resultList\": [\n"
        + "        {\n"
        + "            \"student\": \"stuA\",\n"
        + "            \"age\": 18\n"
        + "        }\n"
        + "    ]\n"
        + "}";

    CompareOptions compareOptions = CompareOptions.options().putOnlyCompareExistListElements(true);
    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assertions.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testNullEqualsNotExist() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNullEqualsNotExist(true);

    CompareResult result1 = sdk.compare("{\"name\":null}", "{}");
    Assertions.assertEquals(0, result1.getLogs().size());

    CompareResult result2 = sdk.compare("{\"name\":null}", "{\"name\":\"\"}");
    Assertions.assertEquals(0, result2.getLogs().size());

    CompareResult result3 = sdk.compare("{}", "{\"name\":\"\"}");
    Assertions.assertEquals(0, result3.getLogs().size());
  }

  @Test
  public void testSimplifyLogEntityMsg() {
    CompareSDK sdk = new CompareSDK();

    String baseMsg = "{\n"
        + "    \"studentName\": \"xiaoming\",\n"
        + "    \"classInfo\": {\n"
        + "        \"name\": \"classA\",\n"
        + "        \"location\": \"floor_A\"\n"
        + "    }\n"
        + "}";
    String testMsg = "{\n"
        + "    \"studentName\": \"xiaoming\"\n"
        + "}";

    CompareResult compare = sdk.compare(baseMsg, testMsg);
    Assertions.assertEquals("[Object]", compare.getLogs().get(0).getBaseValue());
  }

  @Test
  public void testOutPutErrorValueType() {
    CompareSDK sdk = new CompareSDK();

    String baseMsg = "{\n"
        + "    \"score\": \"18\""
        + "}";
    String testMsg = "{\n"
        + "    \"score\": 19\n"
        + "}";

    CompareResult compare = sdk.compare(baseMsg, testMsg);
    Assertions.assertEquals("18", compare.getLogs().get(0).getBaseValue());
  }

  @Test
  public void testMultiCompare() {
    String baseMsg = "{\n"
        + "    \"parameters\": [\n"
        + "        {}\n"
        + "    ],\n"
        + "    \"body\": \"select * from table where ID = 1;select * from table where ID = 2;\",\n"
        + "    \"dbname\": \"db\"\n"
        + "}";
    String testMsg = "{\n"
        + "    \"parameters\": [\n"
        + "        {}\n"
        + "    ],\n"
        + "    \"body\": \"select * from table where ID = 2;select * from table where ID = 1;\",\n"
        + "    \"dbname\": \"db\"\n"
        + "}";
    CompareSDK sdk = new CompareSDK();
    CompareOptions compareOptions = CompareOptions.options()
        .putCategoryType(CategoryType.DATABASE)
        .putSelectIgnoreCompare(true);

    CompareResult compare = sdk.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, compare.getCode());
  }

  @Test
  public void testScriptCompare() {
    CompareSDK compareSDK = new CompareSDK();

    compareSDK.getGlobalOptions().putCompareScript(
        Arrays.asList(
            new ScriptContentInfo(
                "tolerance",
                "function tolerance(context, baseValue, testValue, arg) {\n"
                    + "    if(Math.abs(baseValue - testValue) <= 3) {\n"
                    + "        return true;\n"
                    + "    }else{\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "}"
            )
        )
    );

    CompareOptions compareOptions = new CompareOptions();
    compareOptions.putScriptCompareConfig(
        new ScriptCompareConfig(Arrays.asList("score"), new ScriptMethod("tolerance", ""))
    );

    String baseMsg = "{\n"
        + "    \"score\": 18"
        + "}";
    String testMsg = "{\n"
        + "    \"score\": 20\n"
        + "}";
    CompareResult compare = compareSDK.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, compare.getLogs().size());
  }

  @Test
  public void testScriptCompare2() {
    CompareSDK compareSDK = new CompareSDK();

    compareSDK.getGlobalOptions().putCompareScript(
        Arrays.asList(
            new ScriptContentInfo(
                "equals",
                "function equals(context, baseValue, testValue, arg) {\n"
                    + "    if(baseValue === null && testValue !== null) {\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "    if(baseValue !== null && testValue === null) {\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "    if(baseValue !== testValue) {\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "    return true;\n"
                    + "}"
            )
        )
    );

    CompareOptions compareOptions = new CompareOptions();
    compareOptions.putScriptCompareConfig(
        new ScriptCompareConfig(Arrays.asList("scores", "maths"), new ScriptMethod("equals", ""))
    );

    String baseMsg = "{\n"
        + "    \"name\": \"xiaoming\",\n"
        + "    \"scores\": [\n"
        + "        {\n"
        + "            \"maths\": 90\n"
        + "        },\n"
        + "        {\n"
        + "            \"maths\": 70\n"
        + "        }\n"
        + "    ],\n"
        + "    \"alias\": null,\n"
        + "    \"age\": 18\n"
        + "}";
    String testMsg = "{\n"
        + "    \"name\": \"xiaoming\",\n"
        + "    \"scores\": [\n"
        + "        {\n"
        + "            \"maths\": 18\n"
        + "        }\n"
        + "    ],\n"
        + "    \"alias\": null,\n"
        + "    \"age\": 18\n"
        + "}";
    CompareResult compare = compareSDK.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(2, compare.getLogs().size());
  }


  @Test
  public void testScriptCompare3() {
    CompareSDK compareSDK = new CompareSDK();

    compareSDK.getGlobalOptions().putCompareScript(
        Arrays.asList(
            new ScriptContentInfo(
                "func_67356dbc7ac2aa763be9f8af",
                "function func_67356dbc7ac2aa763be9f8af(context, baseValue, testValue, arg) {return true;}"
            )
        )
    );

    CompareOptions compareOptions = new CompareOptions();
    compareOptions.putScriptCompareConfig(
        new ScriptCompareConfig(Arrays.asList("score"), new ScriptMethod("func_67356dbc7ac2aa763be9f8af", ""))
    );

    String baseMsg = "{\n"
        + "    \"score\": 18"
        + "}";
    String testMsg = "{\n"
        + "    \"score\": 20\n"
        + "}";
    CompareResult compare = compareSDK.quickCompare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(0, compare.getCode());
  }

  @Test
  public void testScriptValueMissing() {
    CompareSDK compareSDK = new CompareSDK();

    compareSDK.getGlobalOptions().putCompareScript(
        Arrays.asList(
            new ScriptContentInfo(
                "equals",
                "function equals(context, baseValue, testValue, arg) {\n"
                    + "    if(baseValue === null && testValue !== null) {\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "    if(baseValue !== null && testValue === null) {\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "    if(baseValue !== testValue) {\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "    return true;\n"
                    + "}"
            )
        )
    );

    CompareOptions compareOptions = new CompareOptions();
    compareOptions.putScriptCompareConfig(
        new ScriptCompareConfig(Arrays.asList("scores", "maths"), new ScriptMethod("equals", ""))
    );

    String baseMsg = "{\n"
        + "    \"name\": \"xiaoming\",\n"
        + "    \"scores\": [\n"
        + "        {\n"
        + "            \"maths\": 90\n"
        + "        },\n"
        + "        {\n"
        + "            \"maths\": 70\n"
        + "        }\n"
        + "    ],\n"
        + "    \"alias\": null,\n"
        + "    \"age\": 18\n"
        + "}";
    String testMsg = "{\n"
        + "    \"name\": \"xiaoming\",\n"
        + "    \"scores\": [\n"
        + "        {\n"
        + "            \"maths\": 90\n"
        + "        },\n"
        + "        {\n"
        + "            \"english\": 70\n"
        + "        }\n"
        + "    ],\n"
        + "    \"alias\": null,\n"
        + "    \"age\": 18\n"
        + "}";
    CompareResult compare = compareSDK.compare(baseMsg, testMsg, compareOptions);
    Assertions.assertEquals(2, compare.getLogs().size());
  }

}