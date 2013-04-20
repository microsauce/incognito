package org.microsauce.incognito;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Hash / Set adaptor
 */
public class HashAdaptor implements Map {

    protected Runtime runtime;
    protected Type type;
    protected Object target;
    // 12 methods

    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isEmpty() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsKey(Object key) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean containsValue(Object value) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object get(Object key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object put(Object key, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object remove(Object key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void putAll(Map m) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set keySet() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Collection values() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set entrySet() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
