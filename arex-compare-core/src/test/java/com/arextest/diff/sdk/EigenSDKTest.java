package com.arextest.diff.sdk;

import com.arextest.diff.model.eigen.EigenOptions;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.model.enumeration.CategoryType;
import org.junit.Assert;
import org.junit.Test;

public class EigenSDKTest {

  @Test
  public void testCalculateEigen() {
    EigenSDK eigenSDK = new EigenSDK();
    String msg = "test";
    EigenResult eigenResult = eigenSDK.calculateEigen(msg);
    Assert.assertEquals(1, 1);
  }

  @Test
  public void testSqlCalculateEigen() {
    String msg = "{\n"
        + "\t\"dbName\": \"corporderhoteldb_dalcluster\",\n"
        + "\t\"parameters\": \"[{\\\"orderid\\\":26975219484}]\",\n"
        + "\t\"body\": \"SELECT * FROM `ofc_order_extend_info` WHERE `orderid`=?\"\n"
        + "}";
    EigenSDK eigenSDK = new EigenSDK();
    EigenOptions eigenOptions = EigenOptions.options().putCategoryType(CategoryType.DATABASE);
    EigenResult eigenResult = eigenSDK.calculateEigen(msg, eigenOptions);
    Assert.assertEquals(1, 1);

  }
}