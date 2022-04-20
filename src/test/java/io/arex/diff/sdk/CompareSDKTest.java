package io.arex.diff.sdk;

import io.arex.diff.model.CompareOptions;
import io.arex.diff.model.CompareResult;
import org.junit.Test;

import java.util.*;

public class CompareSDKTest {

    @Test
    public void testCompare() throws Exception {
        long start = System.currentTimeMillis();
        CompareSDK sdk = new CompareSDK();
        sdk.getGlobalOptions().putNameToLower(true).putNullEqualsEmpty(true)
            .putPluginJarUrl("./lib/arex-compare-sdk-plugin-0.0.1-jar-with-dependencies.jar");

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

        CompareOptions compareOptions = CompareOptions.options().putReferenceConfig("alist\\aid\\id", "family\\id")
            .putListSortConfig(new HashMap<String, String>() {
                {
                    put("family", "subject\\mother,subject\\father");
                    put("alist", "aid\\id");
                }
            }).putDecompressConfig(new HashMap<String, List<String>>() {
                {
                    put("Gzip", Arrays.asList("subObj"));
                }
            }).putExclusions(Arrays.asList("family\\mother", "age"))
            .putInclusions(Arrays.asList("alist\\test", "nullList"));
        CompareResult result = sdk.compare(str1, str2, compareOptions);
        long end = System.currentTimeMillis();
        System.out.println("toatal cost:" + (end - start) + " ms");

    }
}
