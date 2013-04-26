package org.microsauce.incognito;


import java.util.Collection;

public class CollectionProxy extends BaseProxy {

    protected Runtime destRuntime;

    public CollectionProxy(MetaObject<? extends Collection> target, Runtime dest) {
        super(target.getTargetObject());
        destRuntime = dest;
    }
}
