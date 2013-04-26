package org.microsauce.incognito.rhino;

import org.microsauce.incognito.Lang;
import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.Type;
import org.mozilla.javascript.ScriptableObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RhinoRuntime extends Runtime {

    private static String RHINO_IDENTIFIER = "org.mozilla";

    public RhinoRuntime(Lang lang, Object runtime, Object scope) {
        super(lang, runtime, scope);
    }

    @Override
    protected void doInitialize() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String objIdentifier() {
        return RHINO_IDENTIFIER;
    }

    @Override
    protected MetaObject doGetProp(MetaObject target, String name) {
        return wrap(((ScriptableObject)target.getTarget()).get(name));
    }

    @Override
    protected void doSetProp(Object target, String name, Object value) {
        ((ScriptableObject)target).put(name, proxy(wrap(value)));
    }

    @Override
    public Object execMethod(Object target, String name, List args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getTargetClass(Object target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getTargetMethods(Object target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object exec(Object target, Object executionContext, List args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject hashGet(Map target, Object key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject hashPut(Map target, Object key, MetaObject value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject hashEntries(Map target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject setAdd(Set target, MetaObject value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject listGet(List target, int ndx) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaObject listAdd(List target, int ndx, MetaObject value) {
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
    public Object arrayProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object hashProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object dataSetProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object dateProxy(MetaObject obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
