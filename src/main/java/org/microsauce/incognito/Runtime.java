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

    public abstract Object proxy(ObjectAdaptor objAdaptor);
    public abstract ObjectAdaptor wrap(Object nativeObject);
    public void initialize() {
        if ( !initialized ) {
            doInitialize();
            initialized = true;
        }
    }
    public abstract String objIdentifier();   // TODO ???
    protected abstract void doInitialize();

    //
    // object
    //

    // property access JS
    public abstract Object getProp(String name);  // TODO runtime.setProp will handle wrapping
    public abstract void setProp(String name, Object value);

    // method invocation
    public abstract Object execMethod(String name, List args); // TODO runtime will handle args and return value

    // reflection
    public abstract Object getTargetClass();
    public abstract Object getTargetMethods();

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

}
