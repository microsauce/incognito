package org.microsauce.incognito;

import java.util.List;

/**
 *
 * @author microsauce
 */

// TODO encapsulate all rt specific code in the Runtime subclasses:
    // Object adaptor will be a concrete class
    // object adaptor will call into runtime
// TODO for objects / hashes / sets extend Map interface
    // add execMethod / exec to support executable objects
    // add method dispatch(methodName, args) this method will route the method
    //  call to the appropriate
    // add reflection,exception methods
// TODO for lists extend the List interface
    // add method dispatch(methodName, args) this method will route the method
    //  call to the appropriate
// this will allow me to consolidate the bulk of the code in the adaptor
    // leaving little of it in the proxy

// TODO create Executable interface for functions / lambdas / procs / etc
// TODO create

    // TODO Runtime holds a hash of class => adaptor - (where null indicates unsupported
    // i.e. rhino.ScriptableObject => JSObjectAdaptor (implements Map)
    //      java.lang.String       => JSPrimitive
    //      rhino.Function         => JSExecutableAdaptor
    //      jruby RUbyObject       => RubyObjectAdaptor
    //      jruby RubyArray        => RubyArrayAdaptor

// TODO for each lang need to distinguish between object attributes and methods
    // JS - all are attributes
    // jruby - all are methods (attr are protected) - none are attributes
    // groovy -

// TODO method / executable arguments
// TODO for groovy add support for Date and Joda time
// TODO in impl class create map of native class - Type
// TODO will likely need to add more methods to support iteration
public class ObjectAdaptor {

    /* supported types */
    public enum Type {
        // TODO each of these types needs its own adaptor
        PRIMITIVE("primitive"), // string, integer, float
        EXECUTABLE("executable"), OBJECT("object"),
        ARRAY("array"), HASH("hash"), SET("set"), DATE("date");

        String name;

        Type(String name) {
            this.name = name;
        }
    }

    protected Runtime runtime;
    protected Type type;
    protected Object target;

    // object
    // TODO handle any type conversions (date) / any wrapping
    // TODO use this for RHino adaptor
    public Object getProp(String name) { // primitive
        return runtime.getProp(target, name);  // TODO runtime.getProp will return object with appropriate wrapping
    }
    public void setProp(String name, Object value) { // primitive
        runtime.setProp(target, name, value); // TODO runtime.setProp will handle wrapping
    }
    // TODO for groovy and ruby use this for property access
    // TODO unused for JS
    public Object execMethod(String name, List args) { // method
        return runtime.execMethod(target, name, args); // TODO runtime will handle args and return value
    }
//
//    // get / set value that require transformation - i.e. date
//    public abstract Object getValue();
//    public abstract void setValue(Object value);
//
//    // executable - function / closure / proc / lambda / etc.
//    public abstract Object exec(Object executionContext, List args);

    // reflection
    public Object getTarget() {
        return target;
    }
    public Object getTargetMethods() {
        return runtime.getTargetMethods(target);
    }
    public Object getTargetClass() {
        return runtime.getTargetClass(target);
    }
//    public abstract Object getTargetAttributes();
    public Type getBasicType() {
        return type;
    }

//    // collections
//    // array
//    public abstract void append(Object value);
//    public abstract void prepend(Object value);
//    public abstract Object getAt(Integer ndx);
//    public abstract Object removeAt(Integer ndx);
//    public abstract Object insertAt(Integer ndx);
//
//    // hash / set
//    public abstract Object get(Object key);
//    public abstract Object put(Object key, Object value);
//    public abstract Object remove(Object key);
//
//    public abstract boolean contains(Object key);
//    public abstract void clear();
//    public abstract void addAll();
//
//    // iterator
//    public abstract Object next();
//    public abstract Object hasNext();
//    public abstract void reset();
//
//    // exceptions
//    public abstract Object raiseError(String errorMessage);
}

