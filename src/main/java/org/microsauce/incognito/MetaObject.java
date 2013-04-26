package org.microsauce.incognito;


public class MetaObject implements IncognitoProxy {

    private Type type;
    private Object object;
    private Runtime originRuntime;

    public MetaObject(Type type, Runtime origin, Object object) {
        this.object = object;
        this.type = type;
        this.originRuntime = originRuntime;
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
    public Runtime getOriginRuntime() {
        return originRuntime;
    }

    @Override
    public Object getTarget() {
        return object;
    }
}
