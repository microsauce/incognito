package org.microsauce.incognito;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jruby.RubySymbol;
import org.microsauce.incognito.util.CloneUtil;

public class SetProxy extends CollectionProxy {

    private MetaObject<Set> trg;
    private Set trgSet;

    public SetProxy(MetaObject<Set> target, Runtime dest) {
        super(target, dest);
        this.trg = target;
        this.trgSet = target.getTargetObject();
    }

    public Object clone() {
        return CloneUtil.doClone(trg);
    }

    public boolean add(Object obj) {
        return trg.getTargetObject().add(trg.getOriginRuntime().wrap(obj));
    }

    public Iterator iterator() {
        return new ProxySetIterator();
    }

    private class ProxySetIterator implements Iterator {

        private Iterator iterator;

        public ProxySetIterator() {
            this.iterator = trgSet.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Object next() { 
            Object next = iterator.next();
            if ( next instanceof Map.Entry ) {
				Object key = ((Map.Entry) next).getKey();
				key = destRuntime.proxy(trg.getOriginRuntime().wrap(key));
				Object value = ((Map.Entry) next).getValue();
				value = destRuntime.proxy(trg.getOriginRuntime().wrap(value));
				
				Map.Entry entry = new AbstractMap.SimpleEntry<Object,Object>(key, value);
				return entry;
            }
            return destRuntime.proxy(trg.getOriginRuntime().wrap(next)); 
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
