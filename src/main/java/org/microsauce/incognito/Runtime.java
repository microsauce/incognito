package org.microsauce.incognito;

import java.util.*;

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

    /**
     * This method is called to perform any necessary runtime initialization.
     */
    protected abstract void doInitialize();

    //
    // object
    //

    /**
     * Retrieve a property from an object.
     *
     * Note: not all languages make a distinction between property accessors and other
     * method types (ruby for example)
     *
     * @param target
     * @param name
     * @return
     */
    public abstract MetaObject getProp(MetaObject target, String name);

    /**
     * Set the property of an object.
     *
     * @param target
     * @param name
     * @param value
     */
    public abstract void setProp(MetaObject target, String name, MetaObject value);

    /**
     * Execute an object method.
     *
     * @param target
     * @param name
     * @param args
     * @return
     */
    public abstract MetaObject execMethod(MetaObject target, String name, List args);

    /**
     * Does the target object respond to the given method name.
     *
     * @param target
     * @param methodName
     * @return
     */
    public abstract boolean respondTo(MetaObject target, String methodName);

    /**
     * Collect the names of all class members.
     *
     * @param target
     * @return
     */
    public abstract Collection members(MetaObject target); // member names

    /**
     * Retrieve an object member by id/name.
     *
     * Note: for the benefit of JS
     *
     * @param target
     * @param identifier
     * @return
     */
    public abstract MetaObject getMember(MetaObject target, String identifier);

    //
    // executable
    //

    /**
     * Execute the closure/function/lambda encapsulated by target.
     *
     * @param target
     * @param executionContext
     * @param args
     * @return
     */
    public abstract MetaObject exec(MetaObject target, Object executionContext, List args);

    /**
     * Determine the Type of the given object.
     *
     * @param obj
     * @return
     */
    public abstract Type typeof(Object obj);

    public abstract Object undefined();

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

    /**
     * Convert rt specific date to a CommonDate
     *
     * @param date
     * @return
     */
    public abstract CommonDate dateConversion(Object date);

    /**
     * Create a proxy object instance for the givent MetaObject
     *
     * @param obj
     * @return
     */
    public abstract Object objectProxy(MetaObject obj);

    /**
     * Create an executable proxy for the given MetaObject
     *
     * Note: executable types are closures/lambdas/callbacks etc.
     *
     * @param obj
     * @return
     */
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
        } else if (Type.METHOD.equals(type)) {
            return executableProxy(obj);
        } else if (Type.DATE.equals(type)) {
            return dateProxy(obj);
        } else if (Type.UNDEFINED.equals(type)) {
            return undefined();
        } else {
            return objectProxy(obj);
        }
    }

    public ID getId() {
        return id;
    }

    /**
     * Does this runtime own the given obj.
     *
     * @param obj
     * @return
     */
    public abstract boolean ownsObject(Object obj);

}
