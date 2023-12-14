package com.arextest.diff.sdk;

import com.arextest.diff.model.eigen.EigenOptions;
import com.arextest.diff.model.eigen.EigenResult;
import com.arextest.diff.model.enumeration.CategoryType;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class EigenSDKTest {

  @Test
  public void testCalculateEigen() {
    EigenSDK eigenSDK = new EigenSDK();
    String msg = "test";
    EigenResult eigenResult = eigenSDK.calculateEigen(msg);
    Assert.assertEquals(true, eigenResult.getEigenMap().containsKey(0));
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

  @Test
  public void testCalculateEigenIgnoreCase() {
    EigenSDK eigenSDK = new EigenSDK();
    EigenOptions eigenOptions = EigenOptions.options().putExclusions(Arrays.asList("student"));
    String msg = "{\"Student\":\"john\",\"Room\":\"101\"}";
    EigenResult eigenResult = eigenSDK.calculateEigen(msg, eigenOptions);
    Assert.assertEquals(1, eigenResult.getEigenMap().keySet().size());
  }
}