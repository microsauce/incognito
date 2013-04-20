package org.microsauce.incognito;

import java.util.HashSet;

public class AdaptorBackedSet extends HashSet {
    private ObjectAdaptor adaptor;

    public AdaptorBackedSet(ObjectAdaptor adaptor) {
        this.adaptor = adaptor;
    }
}
