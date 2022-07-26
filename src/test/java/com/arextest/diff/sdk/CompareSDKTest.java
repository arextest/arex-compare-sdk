package com.arextest.diff.sdk;

import com.arextest.diff.model.CompareOptions;
import com.arextest.diff.model.CompareResult;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void verifyTypeCompare() throws IOException {
        long start = System.currentTimeMillis();
        CompareSDK sdk = new CompareSDK();

        sdk.getGlobalOptions().putNameToLower(true);

        String str1 = readFileToString("D:\\flight_code\\messageUnzip\\keyCombine2\\baseMsgUn.txt");
        String str2 = readFileToString("D:\\flight_code\\messageUnzip\\keyCombine2\\testMsgUn.txt");

        Map<String, String> references = new HashMap<String, String>() {
            {
                put("Body\\RouteList\\DirectRoute\\FlightIDRefList\\%value%", "Body\\ProductItemList\\FlightList\\FlightID");
                put("Body\\RouteList\\TransferRouteList\\FlightUnitList\\FlightIDRef", "Body\\ProductItemList\\FlightList\\FlightID");
                put("Body\\RouteList\\TransferRouteList\\ProductGroupList\\SegmentProductUnitList\\FlightIDRef", "Body\\ProductItemList\\FlightList\\FlightID");
                put("Body\\RouteGroupList\\FlightUnitList\\FlightIDRef", "Body\\ProductItemList\\FlightList\\FlightID");
                put("Body\\RouteGroupList\\ProductGroupList\\RouteList\\DirectProduct\\FlightIDRef", "Body\\ProductItemList\\FlightList\\FlightID");
                put("Body\\RouteGroupList\\ProductGroupList\\RouteList\\TransferProduct\\SegmentProductUnitList\\FlightIDRef", "Body\\ProductItemList\\FlightList\\FlightID");
                put("Body\\ProductItemList\\FlightList\\SubclassIDRefList\\%value%", "Body\\ProductItemList\\SubclassList\\SubclassID");
                put("Body\\RouteList\\TransferRouteList\\ProductGroupList\\SegmentProductUnitList\\SubclassIDRef", "Body\\ProductItemList\\SubclassList\\SubclassID");
                put("Body\\ProductItemList\\SubclassList\\MultiSegmentRestriction\\BindingSubclassIDRefList\\%value%", "Body\\ProductItemList\\SubclassList\\SubclassID");
                put("Body\\RouteGroupList\\ProductGroupList\\RouteList\\DirectProduct\\SubclassIDRef", "Body\\ProductItemList\\SubclassList\\SubclassID");
                put("Body\\RouteGroupList\\ProductGroupList\\RouteList\\TransferProduct\\SegmentProductUnitList\\SubclassIDRef", "Body\\ProductItemList\\SubclassList\\SubclassID");
            }
        };

        Map<String, String> keys = new HashMap<String, String>() {{
            put("Body\\RouteList", "RouteNo,");
            put("Body\\RouteList\\DirectRoute\\FlightIDRefList", "%value%,");
            put("Body\\ProductItemList\\FlightList", "MarketingFlight,");
            put("Body\\ProductItemList\\FlightList\\SubclassIDRefList", "%value%,");
            put("Body\\ProductItemList\\FlightList\\FlightAttributes", "%value%,");
            put("Body\\ProductItemList\\FlightList\\StandardPolicyList", "CabinClass,Subclass,");
            put("Body\\ProductItemList\\SubclassList", "Subclass,PolicyKeyInfo\\MainPolicy\\PolicyType,PolicyKeyInfo\\MainPolicy\\PolicyID,PID,PolicyDetail\\ProductSourceNum,Prices\\Price,ProductKeyInfo\\ProductItems\\ProductType,");
            put("Body\\ProductItemList\\FlightList\\ClassAreaInfos", "AreaName,AreaCode,");
            put("Body\\ProductItemList\\SubclassList\\PolicyKeyInfo\\MainPolicy", "PolicyID,");
            put("Body\\RouteList\\TransferRouteList", "FlightUnitList\\FlightIDRef,");
            put("Body\\RouteList\\TransferRouteList\\FlightUnitList", "RouteNo,FlightIDRef,SegmentNo,");
            put("Body\\RouteList\\TransferRouteList\\ProductGroupList", "SegmentProductUnitList\\FlightIDRef,SegmentProductUnitList\\SubclassIDRef,TransferProductToken,TransferProductProperties\\ProductCombinationTypeList\\%value%,");
            put("Body\\ProductItemList\\SubclassList\\SceneSpecifiedInfo\\BookingInfo\\DeprecatedInfo\\AdditionalInfoList", "InfoType,");
            put("Body\\ProductItemList\\SubclassList\\AdditionalInfoList", "InfoType,");
            put("Body\\RouteList\\TransferRouteList\\ProductGroupList\\SegmentProductUnitList", "FlightIDRef,SubclassIDRef,");
            put("Body\\ProductItemList\\SubclassList\\Bundles\\BundleItemList", "PriorityType,MandatoryType,Category,Subcategory,");
            put("Body\\RouteGroupList", "RouteGroupType,FlightUnitList\\FlightIDRef,");
            put("Body\\RouteGroupList\\FlightUnitList", "RouteNo,FlightIDRef,SegmentNo,");
            put("Body\\RouteGroupList\\ProductGroupList", "ProductGroupProperties\\ProductCombinationTypeList\\%value%,RouteList\\DirectProduct\\FlightIDRef,RouteList\\DirectProduct\\SubclassIDRef,");
            put("Body\\ProductItemList\\SubclassList\\ProductKeyInfo\\ProductItems", "ProductType,");
            put("Body\\ProductItemList\\SubclassList\\ProductDetail\\AdditionalFee\\DiscountFeeList", "DiscountFeeType,");
            put("Body\\RouteGroupList\\ProductGroupList\\RouteList", "RouteNo,");
            put("Body\\RouteGroupList\\ProductGroupList\\RouteList\\TransferProduct\\SegmentProductUnitList", "SegmentNo,");
            put("Body\\RouteGroupList\\ProductGroupList\\ProductGroupProperties\\ProductCombinationTypeList", "%value%,");
            put("Body\\RouteGroupList\\ProductGroupList\\ProductGroupProperties", "ProductCombinationTypeList\\%value%,");
            put("Body\\ProductItemList\\SubclassList\\SceneSpecifiedInfo\\DeprecatedInfo\\PostCityCandidates", "%value%,");
            put("Body\\ProductItemList", "FlightList\\SubclassIDRefList\\%value%,");

        }};

        CompareOptions compareOptions = CompareOptions.options().putReferenceConfig(references)
                .putListSortConfig(keys);
        CompareResult result = sdk.compare(str1, str2, compareOptions);
        long end = System.currentTimeMillis();
        System.out.println("toatal cost:" + (end - start) + " ms");

    }

    public static String readFileToString(String filePath) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader bf = new BufferedReader(new FileReader(filePath));
        String s = null;
        while ((s = bf.readLine()) != null) {//使用readLine方法，一次读一行
            buffer.append(s.trim());
        }
        String xml = buffer.toString();

        return xml;
    }

}
