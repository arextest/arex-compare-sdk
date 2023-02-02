package com.arextest.diff.model.enumeration;

/**
 * Created by rchen9 on 2023/1/16.
 */
public interface CategoryType {
    /**
     * regular json comparison
     */
    int NORMAL = 0;

    /**
     * when categoryType is database,
     * the field of "body" is sql
     */
    int DATABASE = 1;
}
