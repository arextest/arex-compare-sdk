package com.arextest.diff.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NameConvertUtil {

    public static void nameConvert(Object object) throws JSONException {
        if (object == null || JSONObject.NULL.equals(object)) {
            return;
        }

        if (object instanceof JSONObject) {
            JSONObject jsonObj1 = (JSONObject) object;
            String[] names = JSONObject.getNames(jsonObj1);
            if (names == null) {
                names = new String[0];
            }

            for (String fieldName : names) {
                Object obj1FieldValue = jsonObj1.get(fieldName);
                jsonObj1.put(fieldName.toLowerCase(), obj1FieldValue);
                nameConvert(obj1FieldValue);
            }
            for (String fieldName : names) {
                if (containsUpper(fieldName)) {
                    jsonObj1.remove(fieldName);
                }
            }
        } else if (object instanceof JSONArray) {
            JSONArray obj1Array = (JSONArray) object;
            int len = obj1Array.length();
            for (int i = 0; i < len; i++) {
                Object element = obj1Array.get(i);
                nameConvert(element);
            }
        }

    }

    public static boolean containsUpper(String name) {
        return name.chars().anyMatch(
                (int ch) -> Character.isUpperCase((char) ch)
        );
    }

    public static void main(String[] args) throws JSONException {
        String str1 = "{\"ADDress\":\"add\",\"nAMe\":null," + "\"family\":[{\"id\":1,\"moTHER\":\"B\",\"father\":\"A\",\"brother\":\"F\",\"sister\":\"D\"}," +
                "{\"ID\":2,\"moTHER\":\"A\",\"father\":\"F\",\"brother\":\"C\",\"sister\":\"E\"}]," +
                "\"subObj\":{\"month\":\"5\",\"year\":\"2021\",\"day\":\"17\"},\"alist\":[{\"iD\":1},{\"Id\":2}],\"age\":18}";
        String str2 = "{\"address\":\"add1\",\"name\":\"222\",\"family\":[{\"id\":3,\"mother\":\"B\",\"father\":\"B\",\"brother\":\"C\",\"sister\":\"C\"}," +
                "{\"id\":4,\"mother\":\"B\",\"father\":\"A\",\"brother\":\"F\",\"sister\":\"C\"},{\"id\":5,\"mother\":\"A\",\"father\":\"F\",\"brother\":\"G\",\"sister\":\"K\"}]," +
                "\"subObj\":null,\"alist\":[{\"id\":3},{\"id\":4},{\"id\":5}]}";

        JSONObject jsonObject = new JSONObject(str1);
        JSONObject jsonObject1 = new JSONObject(str2);

        nameConvert(jsonObject);
        nameConvert(jsonObject1);
    }
}
