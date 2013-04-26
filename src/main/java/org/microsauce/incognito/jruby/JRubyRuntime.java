package org.microsauce.incognito.jruby;

import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.Type;

import java.util.List;

public class JRubyRuntime extends Runtime {
    @Override
    protected void doInitialize() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String objIdentifier() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject getProp(MetaObject target, String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void doSetProp(MetaObject target, String name, MetaObject proxy) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject execMethod(MetaObject target, String name, List args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject exec(MetaObject target, Object executionContext, List args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Type typeof(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object objectProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object executableProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object dateProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
