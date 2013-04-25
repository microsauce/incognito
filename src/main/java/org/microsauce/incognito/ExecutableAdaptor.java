package org.microsauce.incognito;

import java.util.List;

public class ExecutableAdaptor implements IncognitoAdaptor {

    protected Runtime runtime;
    protected Type type;
    protected Object target;

    public Object exec(Object executionContext, List args) {
        return runtime.exec(target, executionContext, args);
    }

}
