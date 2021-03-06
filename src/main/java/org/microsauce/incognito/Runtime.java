package org.microsauce.incognito;

import static java.lang.reflect.Proxy.newProxyInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jruby.embed.ScriptingContainer;
import org.microsauce.incognito.groovy.GroovyRuntime;
import org.microsauce.incognito.jruby.JRubyRuntime;
import org.microsauce.incognito.rhino.RhinoRuntime;
import org.mozilla.javascript.Scriptable;

/**
 * This adaptor class defines a standard interface for interacting
 * with disparate language runtimes.
 *
 * @author microsauce
 */
public abstract class Runtime {

	public static Runtime getRuntime(Object rawRt) {
		if ( rawRt == null ) return new GroovyRuntime(rawRt);
		else if ( rawRt instanceof ScriptingContainer )
			return new JRubyRuntime(rawRt);
		else if ( rawRt instanceof Scriptable ) 
			return new RhinoRuntime(rawRt);
		else throw new RuntimeException("Invalid language runtime reference: " + rawRt);
	}
	
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
    protected List<IdentifierConvention> idConventions;

    private boolean initialized = false;

    public Runtime(Object runtime) {
        this.runtime = runtime;
    }

    public Runtime(Object runtime, IdentifierConvention ... conventions) {
        this(runtime);
        idConventions = new ArrayList<IdentifierConvention>();
        for ( IdentifierConvention conv : conventions ) {
            idConventions.add(conv);
        }
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

    private String resolvedIdentifier(MetaObject target, String name) {
        for ( IdentifierConvention thisConvention : idConventions ) {
            String id = thisConvention.enforce(name);
            if ( respondTo(target, id) ) return id;
        }
        return name;
    }

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
    public MetaObject getProp(MetaObject target, String name) {
        return doGetProp(target, resolvedIdentifier(target, name));
    }

    protected abstract MetaObject doGetProp(MetaObject target, String name);

    /**
     * Set the property of an object.
     *
     * @param target
     * @param name
     * @param value
     */
    public void setProp(MetaObject target, String name, MetaObject value) {
        doSetProp(target, resolvedIdentifier(target, name), value);
    }

    protected abstract void doSetProp(MetaObject target, String name, MetaObject value);


    /**
     * Execute a method.
     *
     * @param target
     * @param name
     * @param args
     * @return
     */
    public MetaObject execMethod(MetaObject target, String name, List args) {
        return doExecMethod(target, resolvedIdentifier(target, name), args);
    }

    protected abstract MetaObject doExecMethod(MetaObject target, String name, List args);

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

    public abstract String targetToString(MetaObject target);

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

    public Object toSymbol(Object obj) {
    	return obj.toString();
    }
    
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
        } else if (Type.SYMBOL.equals(type)) {
        	return new MetaObject(Type.SYMBOL, this, obj);
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
        } else if ( Type.UNDEFINED.equals(type) ) {
            return new MetaObject(Type.UNDEFINED, this, obj);
//        } else if ( Type.HASH_ENTRY.equals(type) ) {
//            // TODO
//        	return null;
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
                new Class[] { List.class, Cloneable.class },
                new ListProxy(obj, this));
        return array;
    }
    public Object hashProxy(MetaObject obj) {
        return (Map) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Map.class, Cloneable.class },
                new MapProxy(obj, this));
    }
    public Object dataSetProxy(MetaObject obj) {
        return (Set) newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[] { Set.class, Cloneable.class },
                new SetProxy(obj, this));
    }
    public abstract Object dateProxy(MetaObject obj);
    public abstract Object symbolProxy(MetaObject obj);
    public abstract String symbolToString(MetaObject obj);
    public abstract boolean supportSymbols();

    public Object proxy(MetaObject obj) { // TODO
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
        } else if (Type.SYMBOL.equals(type)) {
        	return symbolProxy(obj);
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

    /**
     * Convert the given identifier into a form that complies with local runtime coding conventions (i.e. snake_case vs camelCase)
     *
     * Note: check for the given identifier first, if not found use the localized version
     *
     * @param identifier
     * @return
     */
    public String localizeIdentifier(String identifier) {
        return idConventions.get(0).enforce(identifier);
    }

    private String camelize(String identifier) {

        if ( identifier.indexOf("_") == -1 ) {
            String[] words = identifier.split("_");
            if ( words.length > 0 ) {
                StringBuilder buffer = new StringBuilder();
                buffer.append(words[0].toLowerCase());
                for ( int i = 1; i < words.length; i++ ) {
                    buffer.append(words[i].substring(0,1).toUpperCase() + words[i].substring(1).toLowerCase());
                }
                return buffer.toString();
            } else return identifier;
        } else return identifier;
    }

    private String snakify(String identifier) {
        if ( identifier.indexOf("_") != -1 ) {
            String[] words = identifier.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
            if ( words.length > 0 ) {
                StringBuilder buffer = new StringBuilder();
                for ( String word : words ) {
                    buffer.append(word).append("_");
                }
                return buffer.substring(0,buffer.length()-1);
            } else return identifier;
        } else return identifier;

    }


}
