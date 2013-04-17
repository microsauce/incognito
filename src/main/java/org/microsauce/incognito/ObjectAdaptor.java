package org.microsauce.incognito;

import java.util.List;

/**
 * This class defines an interface for uniform access to object properties
 * and methods.
 *
 * @author microsauce
 */
public abstract class ObjectAdaptor {

    /* supported types */
    public enum Type {

        PRIMITIVE("primitive"), // string, integer, float
        EXECUTABLE("executable"), OBJECT("object"),
        ARRAY("array"), HASH("hash"), DATE("date");

        String name;

        Type(String name) {
            this.name = name;
        }
    }

    protected Type type;

    // object
    public abstract Object getProp(String name);
    public abstract void setProp(String name, Object value);
    public abstract Object execMethod(String name, List args); // method

    // executable - function / closure / proc / lambda / etc.
    public abstract Object exec(List args);

    // array
    public abstract void append(Object value);
    public abstract void prepend(Object value);
    public abstract Object getAt(Integer ndx);
    public abstract Object removeAt(Integer ndx);

    // hash
    public abstract Object hashGet(Object key);
    public abstract Object hashPut(Object key, Object value);
    public abstract Object hashRemove(Object key);
    public abstract boolean hashContains(Object key);

    public abstract Object getTarget();
}

