package org.microsauce.incognito;

import java.util.ArrayList;

public class AdaptorBackedList extends ArrayList {
    private ObjectAdaptor adaptor;

    public AdaptorBackedList(ObjectAdaptor adaptor) {
        this.adaptor = adaptor;
    }
}
