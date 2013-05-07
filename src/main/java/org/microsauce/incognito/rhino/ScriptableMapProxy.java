package org.microsauce.incognito.rhino;

import org.microsauce.incognito.*;
import org.microsauce.incognito.Runtime;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.lang.reflect.Proxy.newProxyInstance;

public class ScriptableMapProxy extends BaseProxy {

//    private MetaObject trg;
//    private Runtime destRuntime;
//    private Object trgMap;


    public ScriptableMapProxy(MetaObject target, Runtime dest) {
        super(target.getTargetObject());
//        this.trg = target;
//        this.trgMap = target.getTargetObject();
//        this.destRuntime = dest;
    }

//    public Object get(Object key) {
//        return destRuntime.proxy(
//                trg.getOriginRuntime().wrap(trg.getTargetObject().get(key)));
//    }
//
//    public Object put(Object key, Object value) {
//        return trgMap.put(key, trg.getOriginRuntime().wrap(value));  // TODO may need to destRuntime.wrap the return value
//    }
//
//    public Set<Map.Entry> entrySet() {
//        return (Set<Map.Entry>)newProxyInstance(
//                this.getClass().getClassLoader(),
//                new Class[] { Set.class },
//                new SetProxy(trg.getOriginRuntime().wrap(trgMap.entrySet()), destRuntime));
//    }
//
//    public Set keySet() {
//        return (Set)newProxyInstance(
//                this.getClass().getClassLoader(),
//                new Class[] { Set.class },
//                new SetProxy(trg.getOriginRuntime().wrap(trgMap.keySet()), destRuntime));
//    }
//
//    public Collection values() {
//        return (Collection)newProxyInstance(
//                this.getClass().getClassLoader(),
//                new Class[] { Set.class },
//                new SetProxy(trg.getOriginRuntime().wrap(trgMap.values()), destRuntime)); // TODO .wrap(new HashMap().addAll(trgMap.values()))
//    }
}
