package org.microsauce.incognito.rhino;

import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Runtime;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ProxyMap implements Map {

    private MetaObject target;
    private Runtime destRuntime;

    public ProxyMap(MetaObject target, Runtime dest) {
        this.target = target;
        this.destRuntime = dest;
    }

    @Override
    public Object get(Object key) {
        return destRuntime.proxy(
                target.getOriginRuntime().getProp(target, (String)key));
    }

    @Override
    public Object put(Object key, Object value) {
        target.getOriginRuntime().setProp(target, (String)key, value);
        return null; // TODO
    }


    @Override
    public int size() {
        return 0;
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
    @Override
    public boolean containsKey(Object key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public boolean containsValue(Object value) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override public Object remove(Object key) {return null;}
    @Override public void putAll(Map m) {}
    @Override public void clear() {}
    @Override public Set keySet() {return null;}
    @Override public Collection values() {return null;}
    @Override public Set entrySet() {return null;}
}
