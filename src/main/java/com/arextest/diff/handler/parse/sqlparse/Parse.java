package com.arextest.diff.handler.parse.sqlparse;

import org.json.JSONObject;

public interface Parse<T> {
    JSONObject parse(T parseObj);
}
