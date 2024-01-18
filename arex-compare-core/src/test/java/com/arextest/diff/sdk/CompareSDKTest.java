package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import com.arextest.diff.model.DecompressConfig;
import com.arextest.diff.model.enumeration.CategoryType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

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
    Assert.assertEquals(7, result.getLogs().size());
    System.out.println("toatal cost:" + (end - start) + " ms");
  }

  @Test
  public void testSQLCompare() {
    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true);

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

    CompareOptions compareOptions = CompareOptions.options();
    compareOptions.putExclusions(Arrays.asList("body"));
    compareOptions.putCategoryType(CategoryType.DATABASE);
    compareOptions.putOnlyCompareCoincidentColumn(true);
    compareOptions.putSelectIgnoreCompare(true);

    CompareResult result = sdk.compare(str1, str2, compareOptions);
    Assert.assertEquals(1, result.getLogs().size());
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
    Assert.assertEquals(0, result.getLogs().size());
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
    Assert.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testArexPrefixFilter() {
    CompareSDK sdk = new CompareSDK();
    String str1 = "{\"array\":\"http://www.baidu.com\"}";
    String str2 = "{\"array\":\"http://arex_www.baidu.com\"}";
    CompareResult result = sdk.compare(str1, str2);
    Assert.assertEquals(0, result.getLogs().size());
  }

  @Test
  public void testUuidFilter() {
    CompareSDK sdk = new CompareSDK();
    String str1 = "{\"uuid\":\"f4c6d9c9-9d8f-4b1f-9d5c-6e9d7a8c6b2e\"}";
    String str2 = "{\"uuid\":\"f4c6d9c9-9d8f-4b1f-9d5c-6e9d7a8c6b2f\"}";
    CompareResult result = sdk.compare(str1, str2);
    Assert.assertEquals(1, result.getLogs().size());
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
    Assert.assertEquals(0, result.getCode());
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
    Assert.assertEquals(1, result.getLogs().size());
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

    Assert.assertEquals("{\"name\":\"google\"}", result.getProcessedBaseMsg());
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
    Assert.assertEquals(2, result.getLogs().size());

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
    Assert.assertEquals(0, result.getCode());
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
    Assert.assertEquals(0, result.getCode());
  }

  @Test
  public void testGlobalOptionsUuidIgnore() {

    CompareSDK sdk = new CompareSDK();
    sdk.getGlobalOptions().putUuidIgnore(true);

    String str1 = "{\"uuid\":\"5a6798c4-e57c-481d-b6b7-00f5866350c0\"}";
    String str2 = "{\"uuid\":\"41cd4916-9ff5-413e-812a-5f620e2ae589\"}";

    CompareResult result = sdk.compare(str1, str2);
    Assert.assertEquals(0, result.getCode());
  }

  @Test
  public void testIpFilter() {

    CompareSDK sdk = new CompareSDK();

    String str1 = "{\"ip\":\"0001:0:0:0:0:0:0:1\"}";
    String str2 = "{\"ip\":\"0:0:0:0:0:0:0:1\"}";

    CompareResult result = sdk.compare(str1, str2);
    Assert.assertEquals(0, result.getCode());
  }

}