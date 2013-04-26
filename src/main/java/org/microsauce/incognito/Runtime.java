package org.microsauce.incognito;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public Object getProp(MetaObject target, String name) {
        Object value = doGetProp(target, name);
        return proxy(wrap(value));
    }

    protected abstract MetaObject doGetProp(MetaObject target, String name);
    /*
        type = typeof(target)
     */
    public void setProp(Object target, String name, Object value) {
        MetaObject wrapped = wrap(value);
        doSetProp(target, name, wrapped);
    }

    protected abstract void doSetProp(Object target, String name, MetaObject proxy);

    // method invocation
    public abstract Object execMethod(Object target, String name, List args);// TODO runtime will handle args and return value

    // reflection
    public abstract Object getTargetClass(Object target);
    public abstract Object getTargetMethods(Object target);

    //
    // executable
    //

    public abstract Object exec(Object target, Object executionContext, List args);

    //
    // data structures: arrays, sets, and hashes
    //

    //
    // collections strategy: - TODO prove this out
    //
    // proxy override get/set/add/addAll etc
    //  - MetaObject equals/hashCode calls the target object preserving the integrity of get/contains/etc calls
    //  - may not need 'remove' methods - these rely on equals/hashcode
    //  - will the target iterator suffice ???  nope - I need to provide
    //      - jruby hash/array proxies and rhino/ringo 'scriptables' don't utilize iterators

    //
    // hash
    //

    public abstract MetaObject hashGet(Map target, Object key);
    public abstract MetaObject hashPut(Map target, Object key, MetaObject value);
//    public abstract Iterator hashIterator(Map target);
    public abstract MetaObject hashEntries(Map target);
//    public abstract MetaObject hashKeys(Map target);
//    public abstract MetaObject hashValues(Map target);
//    public abstract MetaObject hashRemove(Map target, Object key);

    //
    // set
    //

    public abstract MetaObject setAdd(Set target, MetaObject value);
//    public abstract Iterator setIterator(Set target);
//    public abstract MetaObject setRemove(List target, MetaObject value);

    //
    // array
    //

    public abstract MetaObject listGet(List target, int ndx);
    public abstract MetaObject listAdd(List target, int ndx, MetaObject value);
//    public abstract MetaObject listRemove(List target, int ndx);
//    public abstract Iterator listIterator(List target);


    public abstract Type typeof(Object obj);

    public MetaObject wrap(Object obj) {
        if ( obj instanceof MetaObject ) return (MetaObject)obj;  // TODO  or IncognitoProxy
        else if ( obj instanceof Proxy ) return ((Proxy)obj).getTarget();

        Type type = typeof(obj);
        if ( Type.PRIMITIVE.equals(type) ) {
            return new MetaObject(Type.PRIMITIVE, this, obj);
        } else if (Type.ARRAY.equals(type)) {
            return new MetaObject(Type.ARRAY, this, obj);
        } else if (Type.HASH.equals(type)) {
            return new MetaObject(Type.HASH, this, obj);
        } else if (Type.SET.equals(type)) {
            return new MetaObject(Type.SET, this, obj);
        } else if (Type.EXECUTABLE.equals(type)) {
            return new MetaObject(Type.EXECUTABLE, this, obj);
        } else if (Type.DATE.equals(type)) {
            return new MetaObject(Type.DATE, this, obj);
        } else {
            return new MetaObject(Type.OBJECT, this, obj);
        }
    }
//
//    public abstract Object wrapObject(Object obj);
//    public abstract Object wrapExecutable(Object obj);
//    public abstract Object wrapArray(Object obj); // js: scriptablelist
//    public abstract Object wrapHash(Object obj);  // js: scriptablemap
//    public abstract Object wrapSet(Object obj);  // js: scriptablelist
//    public abstract Object wrapDate(Object obj); // ??? convert to millis after unix epoch ???

    public abstract Object objectProxy(MetaObject obj);
    public abstract Object executableProxy(MetaObject obj);
    public abstract Object arrayProxy(MetaObject obj);      // js: scriptablelist
    public abstract Object hashProxy(MetaObject obj);       // js: scriptablemap
    public abstract Object dataSetProxy(MetaObject obj);    // js: scriptablelist
    public abstract Object dateProxy(MetaObject obj);

    // TODO subclass scriptablelist/map override: public void put(int index, Scriptable start, Object value)
    // update underlying collection as well:
    // this.javaObject.add
    // this.javaObject.remove


    public Object proxy(MetaObject obj) {
        Type type = obj.getType();
        if ( Type.PRIMITIVE.equals(type) ) {
            return obj.getObject();
        } else if (Type.ARRAY.equals(type)) {
            return arrayProxy(obj);
        } else if (Type.HASH.equals(type)) {
            return hashProxy(obj);
        } else if (Type.SET.equals(type)) {
            return dataSetProxy(obj);
        } else if (Type.EXECUTABLE.equals(type)) {
            return executableProxy(obj);
        } else if (Type.DATE.equals(type)) {
            return dateProxy(obj);
        } else {
            return objectProxy(obj);
        }
    }

}
