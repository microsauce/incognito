package org.microsauce.incognito;

import org.microsauce.incognito.util.CloneUtil;

import java.util.Iterator;
import java.util.Set;

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

    // TODO

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
            return destRuntime.proxy(trg.getOriginRuntime().wrap(next));
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }
}
