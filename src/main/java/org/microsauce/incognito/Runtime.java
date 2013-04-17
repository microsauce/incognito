package org.microsauce.incognito;

public abstract class Runtime {

    private Lang lang;
    private Object runtime;
    private Object scope;
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
    public abstract String objIdentifier();
    protected abstract void doInitialize();
}
