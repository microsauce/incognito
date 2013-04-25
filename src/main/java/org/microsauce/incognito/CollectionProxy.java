package org.microsauce.incognito;


public class CollectionProxy extends BaseProxy {

    protected Runtime originRuntime;
    protected Runtime destRuntime;

    public CollectionProxy(Object target, Runtime origin, Runtime dest) {
        super(target);
        originRuntime = origin;
        destRuntime = dest;
    }
}
