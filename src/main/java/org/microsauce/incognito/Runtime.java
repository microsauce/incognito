package org.microsauce.incognito;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Proxy.newProxyInstance;

public abstract class Runtime {

    public static enum RT {
        JRUBY("jruby"), RHINO("rhino"), GROOVY("groovy");

        private String name;

        RT(String name) {
            this.name = name;
        }

        public String getName() {return name;}
    }

    protected RT id;
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
    public abstract void setProp(MetaObject target, String name, MetaObject value);

    // method invocation
    public abstract MetaObject execMethod(MetaObject target, String name, List args);// TODO runtime will handle args and return value

    //
    // executable
    //

    public abstract MetaObject exec(MetaObject target, Object executionContext, List args);

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
            return new MetaObject(Type.DATE, this, obj) {
                public Object conversion() {
                    return dateConversion(object);
                }
            };
        } else {
            return new MetaObject(Type.OBJECT, this, obj);
        }
    }
    public abstract Object dateConversion(Object date);  // convert to millis sinse unix epoc  ??? nope
    public abstract Object objectProxy(MetaObject obj);
    public abstract Object executableProxy(MetaObject obj);
    public Object arrayProxy(MetaObject obj) {
        return (List)newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { List.class },
                new ListProxy(obj, this));
    }
    public Object hashProxy(MetaObject obj) {
        return (Map)newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Map.class },
                new MapProxy(obj, this));
    }
    public Object dataSetProxy(MetaObject obj) {
        return (Set)newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Set.class },
                new SetProxy(obj, this));
    }
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
