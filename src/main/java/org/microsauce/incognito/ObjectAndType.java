package org.microsauce.incognito;


public class ObjectAndType {
    private Type type;
    private Object object; // an adaptor class or a primitive

    public ObjectAndType(Type type, Object object) {
        this.object = object;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Object getObject() {
        return object;
    }
    public boolean equals(Object object) {
        return object.equals(object);
    }
    public int hashCode() {
        return object.hashCode();
    }

}
