package org.microsauce.incognito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO null checks

/**
 * Incognito is a proxy factory. It takes raw, runtime specific object implementations and produces
 * proxies suitable for idiomatic use in the receiving runtime.
 *
 * @author microsauce
 */
public class Incognito {

    private Map<Runtime.ID,Runtime> runtimeByID;

    /**
     * Instantiate an Incognito instance
     */
    public Incognito() {
        runtimeByID = new ConcurrentHashMap<Runtime.ID, Runtime>();
    }

    /**
     * Register a runtime with this instance of Incognito
     *
     * @param runtime
     */
    public void registerRuntime(Runtime runtime) {
        runtimeByID.put(runtime.getId(), runtime);

        runtime.initialize();
    }

    /**
     * Return a proxy for the given object suitable for the given Runtime.ID
     *
     * @param rtId the Runtime.ID for which the proxy is created
     * @param rawObject the target object
     * @return a proxy suitable for the given Runtime.ID
     */
    public Object proxy(Runtime.ID rtId, Object rawObject) {
        Runtime rt = runtimeByID.get(rtId);
        if ( rt == null )
            throw new RuntimeException("runtime id " + rtId.getName() + " is not registered.");
        return rt.proxy(wrap(rawObject));
    }

    /**
     * This method is an alias for proxy
     *
     * @param rtId
     * @param rawObject
     * @return
     */
    public Object assumeIdentity(Runtime.ID rtId, Object rawObject) {
        return proxy(rtId, rawObject);
    }

    private MetaObject wrap(Object rawObject) {
        Runtime sourceRt = sourceRuntime(rawObject);
        if ( sourceRt != null ) return sourceRt.wrap(rawObject);
        else return new MetaObject(Type.PRIMITIVE, null, rawObject); // primitives are ambiguous
    }

    private Runtime sourceRuntime(Object rawObject) {

        // null / primitives are ambiguous - return null
        if ( rawObject == null ) return null;
        if ( rawObject instanceof Number ) return null;
        if ( rawObject instanceof String ) return null;

        for (Runtime thisRt : runtimeByID.values()) {
            if ( thisRt.ownsObject(rawObject) ) return thisRt;
        }
        // this is a non-primitive java object - use GROOVY if available
        Runtime rt = runtimeByID.get(Runtime.ID.GROOVY);
        if ( rt == null ) throw new RuntimeException("no suitable runtime is registered for class " + rawObject.getClass());
        return rt;
    }

}
