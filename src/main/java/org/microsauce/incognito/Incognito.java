package org.microsauce.incognito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO null checks
// TODO proxy should use adaptor for foreign objects only
public class Incognito {

    private Map<Lang,Runtime> runtimeByLang;
    private Map<String,Runtime> runtimeByPkg;

    public Incognito() {
        runtimeByLang = new ConcurrentHashMap<Lang, Runtime>();
        runtimeByPkg  = new ConcurrentHashMap<String, Runtime>();
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
        String packageName = rawObject.getClass().getPackage().getName();
        Runtime rt = runtimeByPkg.get(packageName);
        if ( rt == null ) {
            for (Runtime thisRt : runtimeByLang.values()) {
                if (packageName.startsWith(thisRt.objIdentifier()) ) {  // objIdentifer i.e. 'org.jruby'
                    runtimeByPkg.put(packageName, thisRt);
                    rt = thisRt;
                    break;
                }
            }
        }
        return rt;
    }

}
