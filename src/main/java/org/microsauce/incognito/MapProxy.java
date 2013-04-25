package org.microsauce.incognito;

import java.util.Map;


public class MapProxy extends CollectionProxy {

    private Map trg;

    MapProxy(Map target, Runtime origin, Runtime dest) {
        super(target, origin, dest);
        this.trg = target;
    }

    public Object get(Object key) {
        return destRuntime.proxy(originRuntime.wrap(trg.get(key)));
    }

    public Object put(Object key, Object value) {
        return trg.put(key, destRuntime.wrap(value));  // TODO may need to destRuntime.wrap the return value
    }

}
