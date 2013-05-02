package org.microsauce.incognito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO null checks
public class Incognito {

    private Map<Lang,Runtime> runtimeByLang;
//    private Map<String,Runtime> runtimeByPkg;

    public Incognito() {
        runtimeByLang = new ConcurrentHashMap<Lang, Runtime>();
//        runtimeByPkg  = new ConcurrentHashMap<String, Runtime>();
    }

    public void registerRuntime(Runtime runtime) {
        runtimeByLang.put(runtime.getLang(), runtime); // TODO one RT per lang? not necessary

        runtime.initialize();
    }

    public Object proxy(Lang lang, Object rawObject) {
        Runtime rt = runtimeByLang.get(lang);
        if ( rt == null )
            throw new RuntimeException("a runtime is not registered for lang: " + lang.getName());
        return rt.proxy(wrap(rawObject));
    }

    public Object assumeIdentity(Lang lang, Object rawObject) {
        return proxy(lang, rawObject);
    }

    private MetaObject wrap(Object rawObject) {
        Runtime sourceRt = sourceRuntime(rawObject);
        if ( sourceRt != null ) return sourceRt.wrap(rawObject);
        else return new MetaObject(Type.PRIMITIVE, null, rawObject); // primitives are ambiguous
    }

    private Runtime sourceRuntime(Object rawObject) {

        if ( rawObject == null ) return null;
        if ( rawObject instanceof Number ) return null;
        if ( rawObject instanceof String ) return null;

        for (Runtime thisRt : runtimeByLang.values()) {
            if ( thisRt.ownsObject(rawObject) ) return thisRt;
        }
        // this is a non-primitive java object - use GROOVY if available
        Runtime rt = runtimeByLang.get(Lang.GROOVY);
        if ( rt == null ) throw new RuntimeException("No suitable runtime is registered for class " + rawObject.getClass());
        return rt;
    }

}
