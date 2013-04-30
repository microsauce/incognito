package org.microsauce.incognito;


public class MetaObject<T> implements IncognitoProxy {

    private Type type;
    protected T object;
    private Runtime originRuntime;

    public MetaObject(Type type, Runtime origin, T object) {
        this.object = object;
        this.type = type;
        this.originRuntime = origin;
    }

    public Type getType() {
        return type;
    }

    public boolean equals(Object object) {
        return object.equals(object);
    }
    public int hashCode() {
        return object.hashCode();
    }
    public Runtime getOriginRuntime() {
        return originRuntime;
    }

    @Override
    public T getTargetObject() {
        return (T)conversion();
    }

    public String toString() {
        return object.toString();
    }

    public Object conversion() {return object;}
}
