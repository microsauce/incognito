package org.microsauce.incognito;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Proxy.newProxyInstance;

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

    public Object getScope() {
        return scope;
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
    public abstract MetaObject getProp(MetaObject target, String name);
    /*
        type = typeof(target)
     */
    public void setProp(MetaObject target, String name, Object value) {
        doSetProp(target, name, wrap(value));
    }

    protected abstract void doSetProp(MetaObject target, String name, MetaObject proxy);

    // method invocation
    public abstract MetaObject execMethod(MetaObject target, String name, List args);// TODO runtime will handle args and return value

    // reflection
//    public abstract Object getTargetClass(Object target);
//    public abstract Object getTargetMethods(Object target);

    //
    // executable
    //

    public abstract MetaObject exec(MetaObject target, Object executionContext, List args);

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

//    public abstract MetaObject hashGet(Map target, Object key);
//    public abstract MetaObject hashPut(Map target, Object key, MetaObject value);
////    public abstract Iterator hashIterator(Map target);
//    public abstract MetaObject hashEntries(Map target);
////    public abstract MetaObject hashKeys(Map target);
////    public abstract MetaObject hashValues(Map target);
////    public abstract MetaObject hashRemove(Map target, Object key);
//
//    //
//    // set
//    //
//
//    public abstract MetaObject setAdd(Set target, MetaObject value);
////    public abstract Iterator setIterator(Set target);
////    public abstract MetaObject setRemove(List target, MetaObject value);
//
//    //
//    // array
//    //
//
//    public abstract MetaObject listGet(List target, int ndx);
//    public abstract MetaObject listAdd(List target, int ndx, MetaObject value);
////    public abstract MetaObject listRemove(List target, int ndx);
////    public abstract Iterator listIterator(List target);


    public abstract Type typeof(Object obj);

    public MetaObject wrap(Object obj) {
        if ( obj instanceof MetaObject ) return (MetaObject)obj;
        else if ( obj instanceof Proxy ) return ((Proxy)obj).getTarget();

        Type type = typeof(obj);
        if ( Type.PRIMITIVE.equals(type) ) {
            return new MetaObject(Type.PRIMITIVE, this, obj);
        } else if (Type.ARRAY.equals(type)) {
            return new MetaObject<List>(Type.ARRAY, this, (List)obj);
        } else if (Type.HASH.equals(type)) {
            return new MetaObject<Map>(Type.HASH, this, (Map)obj);
        } else if (Type.SET.equals(type)) {
            return new MetaObject<Set>(Type.SET, this, (Set)obj);
        } else if (Type.EXECUTABLE.equals(type)) {
            return new MetaObject(Type.EXECUTABLE, this, obj);
        } else if (Type.DATE.equals(type)) {
            return new MetaObject(Type.DATE, this, obj);
        } else {
            return new MetaObject(Type.OBJECT, this, obj);
        }
    }

    public abstract Object objectProxy(MetaObject obj);
    public abstract Object executableProxy(MetaObject obj);
    public Object arrayProxy(MetaObject obj) {   // TODO RhinoRuntime will extend this method return new ScriptableList(super.arrayProxy(obj))
        return (List)newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { List.class },
                new ListProxy(obj, this));
    }
    public Object hashProxy(MetaObject obj) { // TODO RhinoRuntime will extend this method return new ScriptableMap(super.hashProxy(obj))
        return (Map)newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Map.class },
                new MapProxy(obj, this));
    }
    public Object dataSetProxy(MetaObject obj) { // TODO RhinoRuntime will extend this method return new ScriptableList(super.arrayProxy(obj))
        return (Set)newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Set.class },
                new SetProxy(obj, this));
    }    // js: scriptablelist
    public abstract Object dateProxy(MetaObject obj);

    // TODO subclass scriptablelist/map override: public void put(int index, Scriptable start, Object value)
    // update underlying collection as well:
    // this.javaObject.add
    // this.javaObject.remove


    public Object proxy(MetaObject obj) {
        Type type = obj.getType();
        if ( Type.PRIMITIVE.equals(type) ) {
            return obj.getTargetObject();
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
