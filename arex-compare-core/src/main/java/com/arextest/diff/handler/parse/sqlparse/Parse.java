package com.arextest.diff.handler.parse.sqlparse;


import com.fasterxml.jackson.databind.node.ObjectNode;

public interface Parse<T> {

  ObjectNode parse(T parseObj);
}
