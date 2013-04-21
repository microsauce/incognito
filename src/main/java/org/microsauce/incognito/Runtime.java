package org.microsauce.incognito;

import java.util.List;

public abstract class Runtime {

    protected Lang lang;
    protected Object runtime;
    protected Object scope;
    private boolean initialized = false;

    public Runtime(Lang lang, Object runtime, Object scope) {
        this.lang = lang;
        this.runtime = runtime;
        this.scope = scope;
    }

    public Lang getLang() {
        return lang;
    }

    public abstract Object proxy(ObjectAndType objAdaptor);
    public void initialize() {
        if ( !initialized ) {
            doInitialize();
            initialized = true;
        }
    }

    protected abstract void doInitialize();

    protected abstract String objIdentifier();     // see Incognito.sourceRuntime

    //
    // object
    //

    // property access JS
    public abstract Object getProp(Object target, String name);  // TODO runtime.setProp will handle wrapping
    /*
        type = typeof(target)
     */
    public abstract void setProp(Object target, String name, Object value);

    // method invocation
    public abstract Object execMethod(String name, List args); // TODO runtime will handle args and return value

    // reflection
    public abstract Object getTargetClass(Object target);
    public abstract Object getTargetMethods(Object target);

    //
    // executable
    //
    public abstract Object exec(Object target, Object executionContext, List args);

    //
    // hash / set
    //

    //
    // array
    //

    public abstract Type typeof(Object obj);

    public ObjectAndType wrap(Object obj) {
        Type type = typeof(obj);
        if ( Type.PRIMITIVE.equals(type) ) {
            return new ObjectAndType(Type.PRIMITIVE, obj);
        } else if (Type.ARRAY.equals(type)) {
            return new ObjectAndType(Type.ARRAY, wrapArray(obj));
        } else if (Type.HASH.equals(type)) {
            return new ObjectAndType(Type.HASH, wrapHash(obj));
        } else if (Type.SET.equals(type)) {
            return new ObjectAndType(Type.SET, wrapSet(obj));
        } else if (Type.EXECUTABLE.equals(type)) {
            return new ObjectAndType(Type.EXECUTABLE, wrapExecutable(obj));
        } else if (Type.DATE.equals(type)) {
            return new ObjectAndType(Type.DATE, wrapDate(obj));
        } else {
            return new ObjectAndType(Type.OBJECT, wrapObject(obj));
        }
    }

    public abstract Object wrapObject(Object obj);
    public abstract Object wrapExecutable(Object obj);
    public abstract Object wrapArray(Object obj);
    public abstract Object wrapHash(Object obj);
    public abstract Object wrapSet(Object obj);
    public abstract Object wrapDate(Object obj);

}
