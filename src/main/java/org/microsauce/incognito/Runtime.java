package org.microsauce.incognito;

import java.util.Iterator;
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
    public ObjectAndType getProp(Object target, String name) {
        Object value = doGetProp(target, name);
        return wrap(value);
    }

    protected abstract ObjectAndType doGetProp(Object target, String name);
    /*
        type = typeof(target)
     */
    public void setProp(Object target, String name, Object value) {
        ObjectAndType wrapped = wrap(value);
        doSetProp(target, name, wrapped);
    }

    protected abstract void doSetProp(Object target, String name, ObjectAndType wrapped);

    // method invocation
    public abstract Object execMethod(Object target, String name, List args); // TODO runtime will handle args and return value

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
    //  - ObjectAndType equals/hashCode calls the target object preserving the integrity of get/contains/etc calls
    //  - may not need 'remove' methods - these rely on equals/hashcode
    //  - will the target iterator suffice ???  nope - I need to provide
    //      - jruby hash/array proxies and rhino/ringo 'scriptables' don't utilize iterators

    //
    // hash
    //

    public abstract ObjectAndType hashGet(Map target, Object key);
    public abstract ObjectAndType hashPut(Map target, Object key, ObjectAndType value);
//    public abstract Iterator hashIterator(Map target);
    public abstract ObjectAndType hashEntries(Map target);
//    public abstract ObjectAndType hashKeys(Map target);
//    public abstract ObjectAndType hashValues(Map target);
//    public abstract ObjectAndType hashRemove(Map target, Object key);

    //
    // set
    //

    public abstract ObjectAndType setAdd(Set target, ObjectAndType value);
//    public abstract Iterator setIterator(Set target);
//    public abstract ObjectAndType setRemove(List target, ObjectAndType value);

    //
    // array
    //

    public abstract ObjectAndType listGet(List target, int ndx);
    public abstract ObjectAndType listAdd(List target, int ndx, ObjectAndType value);
//    public abstract ObjectAndType listRemove(List target, int ndx);
//    public abstract Iterator listIterator(List target);


    public abstract Type typeof(Object obj);

    public ObjectAndType wrap(Object obj) {
        if ( obj instanceof ObjectAndType ) return (ObjectAndType)obj;  // TODO  or IncognitoAdaptor
        else if ( obj instanceof Proxy ) return ((Proxy)obj).getTarget();

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
    public abstract Object wrapArray(Object obj); // js: scriptablelist
    public abstract Object wrapHash(Object obj);  // js: scriptablemap
    public abstract Object wrapSet(Object obj);  // js: scriptablelist
    public abstract Object wrapDate(Object obj); // ??? convert to millis after unix epoch ???

    public abstract Object objectProxy(ObjectAndType obj);
    public abstract Object executableProxy(ObjectAndType obj);
    public abstract Object arrayProxy(ObjectAndType obj); // js: scriptablelist
    public abstract Object hashProxy(ObjectAndType obj);  // js: scriptablemap
    public abstract Object dataSetProxy(ObjectAndType obj);  // js: scriptablelist
    public abstract Object dateProxy(ObjectAndType obj);

    // TODO subclass scriptablelist/map override: public void put(int index, Scriptable start, Object value)
    // update underlying collection as well:
    // this.javaObject.add
    // this.javaObject.remove


    public Object proxy(ObjectAndType obj) {
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
