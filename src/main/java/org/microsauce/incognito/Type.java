package org.microsauce.incognito;

public enum Type {

    PRIMITIVE("primitive"), // string, integer, float
    EXECUTABLE("executable"), OBJECT("object"),
    ARRAY("array"), HASH("hash"), SET("set"), DATE("date");

    String name;

    Type(String name) {
        this.name = name;
    }
}