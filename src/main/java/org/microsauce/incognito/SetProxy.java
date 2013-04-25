package org.microsauce.incognito;

import java.util.Set;

public class SetProxy extends CollectionProxy {

    private Set trg;

    public SetProxy(Set target, Runtime origin, Runtime dest) {
        super(target, origin, dest);
        this.trg = target;
    }

    public boolean add(Object obj) {
        return trg.add(destRuntime.wrap(obj));
    }

    // TODO iterator etc

}
