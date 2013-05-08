package org.microsauce.incognito;


/**
 * The set of basic types recognized by Incognito.
 *
 */
public enum Type {

    PRIMITIVE("primitive"), // string, integer, float, null/nil
    EXECUTABLE("executable"), OBJECT("object"), METHOD("method"),
    ARRAY("array"), HASH("hash"), SET("set"), DATE("date");

    String name;

    Type(String name) {
        this.name = name;
    }
}