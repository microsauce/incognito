package org.microsauce.incognito;

import java.util.Iterator;
import java.util.List;

public class ListProxy extends CollectionProxy {

    private MetaObject<List> trg;

    private List trgList;

    public ListProxy(MetaObject<List> target, Runtime dest) {
        super(target, dest);
        this.trgList = target.getTargetObject();
        this.trg = target;
    }

    public Object get(int ndx) {
        return destRuntime.proxy(trg.getOriginRuntime().wrap(trgList.get(ndx)));
    }

    public boolean add(Object obj) {
        return trgList.add(trg.getOriginRuntime().wrap(obj));
    }

    public Iterator iterator() {
        return new ProxyListIterator();
    }

    private class ProxyListIterator implements Iterator {

        private Iterator iterator;

        public ProxyListIterator() {
            iterator = trgList.iterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Object next() {
            Object value = iterator.next();
            return destRuntime.proxy(trg.getOriginRuntime().wrap(value));
        }

        @Override
        public void remove() {
            iterator.remove();
        }
    }

}
