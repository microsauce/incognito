package org.microsauce.incognito;


public class MetaObject<T> implements IncognitoProxy {

    private Type type;
    protected T object;
    private Runtime originRuntime;
    private String identifier;
    private MetaObject value;

    public MetaObject(Type type, Runtime origin, T object) {
        this(type, origin, object, null);
    }

    public MetaObject(Type type, Runtime origin, T object, String identifier) {
        this(type, origin, object, identifier, null);
    }
    public MetaObject(Type type, Runtime origin, T object, String identifier, MetaObject value) {
        this.object = object;
        this.type = type;
        this.originRuntime = origin;
        this.identifier = identifier;
        this.value = value;
    }

    public Type getType() {
        return type;
    }

    public boolean equals(Object object) {
        return object == null ? false : object.equals(object);
    }

    public int hashCode() {
        return object == null ? 0 : object.hashCode();
    }

    public Runtime getOriginRuntime() {
        return originRuntime;
    }

    @Override
    public T getTargetObject() {
        return (T)conversion();
    }

    public T getTargetRaw() {
        return object;
    }

    public String toString() {
        return object == null ? null : object.toString();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public MetaObject getValue() {
        return value;
    }

    public Object conversion() {return object;}
}
