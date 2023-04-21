package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareResult;
import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertEquals(result.getLogs().size(),1);
    }
}
