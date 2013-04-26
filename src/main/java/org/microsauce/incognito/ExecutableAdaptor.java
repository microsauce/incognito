package org.microsauce.incognito;

import java.util.List;

public class ExecutableAdaptor implements IncognitoProxy {

    protected Runtime runtime;
    protected Type type;
    protected MetaObject target;

    public Object exec(Object executionContext, List args) {
        return runtime.exec(target, executionContext, args);
    }

    @Override
    public Object getTargetObject() {
        return target.getTargetObject();
    }
}
