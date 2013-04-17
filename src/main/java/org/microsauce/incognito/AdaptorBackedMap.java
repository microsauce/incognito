package org.microsauce.incognito;


import java.util.HashMap;

public class AdaptorBackedMap extends HashMap {
    private ObjectAdaptor adaptor;

    public AdaptorBackedMap(ObjectAdaptor adaptor) {
        this.adaptor = adaptor;
    }

}
