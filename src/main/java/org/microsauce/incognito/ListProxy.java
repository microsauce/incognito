package org.microsauce.incognito;


import java.util.List;

public class ListProxy extends CollectionProxy {

    private List trg;

    public ListProxy(List target, Runtime origin, Runtime dest) {
        super(target, origin, dest);
        this.trg = target;
    }

    public Object get(int ndx) {
        return destRuntime.proxy(originRuntime.wrap(trg.get(ndx)));
    }

    public boolean add(Object obj) {
        return trg.add(destRuntime.wrap(obj));
    }
}
