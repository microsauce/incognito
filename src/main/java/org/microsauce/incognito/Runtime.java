package org.microsauce.incognito;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * This class is an adaptor that defines a standard interface for interacting
 * with disparate language runtimes.
 *
 * @author microsauce
 */
public abstract class Runtime {

    public static enum ID {
        JRUBY("jruby"), RHINO("rhino"), GROOVY("groovy");

        private String name;

        ID(String name) {
            this.name = name;
        }

        public String getName() {return name;}
    }

    protected ID id;
    protected Lang lang;
    protected Object runtime;

    private boolean initialized = false;

    public Runtime(Object runtime) {
        this.runtime = runtime;
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

    //
    // object
    //

    public abstract MetaObject getProp(MetaObject target, String name);
    public abstract void setProp(MetaObject target, String name, MetaObject value);
    public abstract MetaObject execMethod(MetaObject target, String name, List args);

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
            return new MetaObject<List>(Type.ARRAY, this, (List)obj) {
                public Object conversion() {
                    if ( object instanceof List ) return object;
                    else return Arrays.asList(object);
                }
            };
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
    public abstract Object dateConversion(Object date);
    public abstract Object objectProxy(MetaObject obj);
    public abstract Object executableProxy(MetaObject obj);
    public Object arrayProxy(MetaObject obj) {
        List array = (List) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { List.class },
                new ListProxy(obj, this));
        return array;
    }
    public Object hashProxy(MetaObject obj) {
        return (Map) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Map.class },
                new MapProxy(obj, this));
    }
    public Object dataSetProxy(MetaObject obj) {
        return (Set) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Set.class },
                new SetProxy(obj, this));
    }
    public abstract Object dateProxy(MetaObject obj);

    public Object proxy(MetaObject obj) { // TODO
//System.out.println("proxy: " + obj);
//System.out.println("proxy.target: " + obj.getTargetObject());
//if ( obj.getTargetObject() != null )
//    System.out.println("proxy.target.class: " + obj.getTargetObject().getClass());
//System.out.println("proxy.type: " + obj.getType().name());
//if ( obj.getTargetObject() instanceof RubyEnumerator )
//    new Exception().printStackTrace();

        if ( obj == null ) return obj;

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

    public ID getId() {
        return id;
    }

    public abstract boolean ownsObject(Object obj);

}
