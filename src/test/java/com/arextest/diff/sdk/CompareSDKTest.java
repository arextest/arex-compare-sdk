package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CompareSDKTest {

    @Test
    public void testCompare() throws Exception {
        long start = System.currentTimeMillis();
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true)
                .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.1.0-jar-with-dependencies.jar");

        System.out.println(System.currentTimeMillis() - start);

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

        CompareOptions compareOptions = CompareOptions.options().putReferenceConfig(Arrays.asList("alist", "aid", "id"), Arrays.asList("family", "id"))
                .putListSortConfig(new HashMap<List<String>, List<List<String>>>() {
                    {
                        put(Arrays.asList("family"), Arrays.asList(Arrays.asList("subject", "mother"), Arrays.asList("subject", "father")));
                        put(Arrays.asList("alist"), Arrays.asList(Arrays.asList("aid", "id")));
                    }
                }).putDecompressConfig(new HashMap<String, List<List<String>>>() {
                    {
                        put("Gzip", Arrays.asList(Arrays.asList("subObj")));
                    }
                }).putExclusions(Arrays.asList(Arrays.asList("family", "mother"), Arrays.asList("age")));
                // .putInclusions(Arrays.asList(Arrays.asList("alist", "test"), Arrays.asList("nullList")));

        CompareResult result = sdk.compare(str1, str2, compareOptions);
        long end = System.currentTimeMillis();
        System.out.println("toatal cost:" + (end - start) + " ms");
    }
}
