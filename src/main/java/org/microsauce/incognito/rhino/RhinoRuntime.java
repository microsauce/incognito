package org.microsauce.incognito.rhino;

import org.microsauce.incognito.Lang;
import org.microsauce.incognito.ObjectAndType;
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
        // TODO implement this last
    }

    @Override
    protected String objIdentifier() {
        return RHINO_IDENTIFIER;
    }

    @Override
    protected ObjectAndType doGetProp(Object target, String name) {
        ScriptableObject sObject = (ScriptableObject) target;
        return wrap(sObject.get(name)); // TODO verify
    }

    @Override
    protected void doSetProp(Object target, String name, ObjectAndType wrapped) {
        ScriptableObject sObject = (ScriptableObject) target;
        //sObject.put
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
    public ObjectAndType hashGet(Map target, Object key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectAndType hashPut(Map target, Object key, ObjectAndType value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectAndType hashEntries(Map target) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectAndType setAdd(Set target, ObjectAndType value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectAndType listGet(List target, int ndx) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectAndType listAdd(List target, int ndx, ObjectAndType value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Type typeof(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object wrapObject(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object wrapExecutable(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object wrapArray(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object wrapHash(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object wrapSet(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object wrapDate(Object obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object objectProxy(ObjectAndType obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object executableProxy(ObjectAndType obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object arrayProxy(ObjectAndType obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object hashProxy(ObjectAndType obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object dataSetProxy(ObjectAndType obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object dateProxy(ObjectAndType obj) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
