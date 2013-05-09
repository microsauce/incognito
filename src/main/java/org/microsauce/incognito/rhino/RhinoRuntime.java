package org.microsauce.incognito.rhino;

import org.microsauce.incognito.*;
import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.rhino.ContextUtil;
import org.mozilla.javascript.*;
import org.ringojs.wrappers.ScriptableMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class RhinoRuntime extends Runtime {

    private static String RHINO_IDENTIFIER = "org.mozilla";

    private NativeObject incognito;
    private NativeFunction executableProxy;
    private NativeFunction convertDate;
    private NativeFunction dateProxy;

    public RhinoRuntime(Object runtime) {
        super(runtime);
        lang = Lang.JAVASCRIPT;
        id = ID.RHINO;
    }

    @Override
    protected void doInitialize() {
        Context ctx = null;
        try {
            ctx = ContextUtil.enter();
            InputStreamReader reader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream("incognito-rhino.js"));
            try {
                ctx.evaluateReader((ScriptableObject)runtime, reader, "incognito-rhino.js", 1, null);
            } catch (IOException e) {throw new RuntimeException(e);}

            incognito = (NativeObject)((ScriptableObject) runtime).get("rhinoIncognito");
            executableProxy = (NativeFunction)incognito.get("executableProxy");
            convertDate = (NativeFunction)incognito.get("convertDate");
            dateProxy = (NativeFunction)incognito.get("dateProxy");
        } finally {
            ContextUtil.exit();
        }
    }

    @Override
    public MetaObject getProp(MetaObject target, String name) {
        return wrap(((ScriptableObject) target.getTargetObject()).get(name));
    }

    @Override
    public void setProp(MetaObject target, String name, MetaObject value) {
        ((ScriptableObject)target.getTargetObject()).put(
                name, (ScriptableObject)target.getTargetObject(), proxy(value));    // TODO remove wrap
    }

    @Override
    public MetaObject execMethod(MetaObject target, String name, List args) {
        ScriptableObject thisScope = (ScriptableObject) target.getTargetObject();
        MetaObject metaProperty = getProp(target, name);
        if ( metaProperty.getTargetObject() instanceof NativeFunction )
            return doExec((NativeFunction)metaProperty.getTargetObject(), thisScope, args);
        else return metaProperty;
    }

    @Override
    public boolean respondTo(MetaObject target, String methodName) {
        ScriptableObject sObject = (ScriptableObject)target.getTargetObject();
        return sObject.has(methodName, sObject);
    }

    @Override
    public Collection members(MetaObject target) {
        ScriptableObject sObject = (ScriptableObject)target.getTargetObject();
        return Arrays.asList(sObject.getIds());
    }

    @Override
    public MetaObject getMember(MetaObject target, String identifier) {
        return getProp(target, identifier);
    }

    @Override
    public MetaObject exec(MetaObject target, Object executionContext, List args) {
        return doExec((NativeFunction)target.getTargetObject(), (ScriptableObject) runtime, args);
    }

    private MetaObject doExec(NativeFunction func, ScriptableObject scope, List args) {
        org.mozilla.javascript.Context ctx = null;
        try {
            ctx = ContextUtil.enter();
            if ( ctx == null )
                ctx = org.mozilla.javascript.Context.enter();
            Object retValue = func.call(
                    ctx, scope, scope, args.toArray());
            return wrap(retValue);
        }
        finally {
            ContextUtil.exit();
        }
    }

    @Override
    public Type typeof(Object obj) {

        if (obj == null) return null;

        if ( obj instanceof String ) return Type.PRIMITIVE;
        if ( obj instanceof Number ) return Type.PRIMITIVE;
        if ( obj instanceof NativeObject) return Type.OBJECT;
        if ( obj instanceof NativeArray) return Type.ARRAY;
        // NativeDate is not public
        if ( obj.getClass().getName().equals("org.mozilla.javascript.NativeDate") )
            return Type.DATE;
        return null;
    }

    @Override
    public Object undefined() {
        return Undefined.instance;
    }

    @Override
    public Object objectProxy(MetaObject obj) {
        return new IncognitoNativeJavaObject(obj, this);
    }

    @Override
    public Object executableProxy(MetaObject obj) {
        Context ctx = null;
        try {
            ctx = ContextUtil.enter();

            return executableProxy.call(
                    ctx, incognito, incognito, new Object[] {obj, this});
        }
        finally {
            ContextUtil.exit();
        }
    }

    @Override
    public Object arrayProxy(MetaObject obj) {
        IncognitoScriptableList proxy = new IncognitoScriptableList(
                (ScriptableObject)runtime, (List)super.arrayProxy(obj));
        Object target = obj.getTargetObject();
        if ( target instanceof Set ) {
            proxy.setOriginCollection((Collection)target);
        }
        return proxy;
    }

    @Override
    public Object hashProxy(MetaObject obj) {
        return new ScriptableMap((ScriptableObject)runtime, (Map)super.hashProxy(obj));
    }

    @Override
    public Object dataSetProxy(MetaObject obj) {
        Set originalCollection = (Set)super.dataSetProxy(obj);
        IncognitoScriptableList array = new IncognitoScriptableList(
                (ScriptableObject)runtime, new ArrayList(originalCollection));
        array.setOriginCollection(originalCollection);
        return array;
    }

    @Override
    public Object dateProxy(MetaObject obj) {
        Context ctx = null;
        try {
            ctx = ContextUtil.enter();
            if ( ctx == null ) ctx = Context.enter();
            return dateProxy.call(
                    ctx, incognito, incognito, new Object[] {obj});
        }
        finally {
            ContextUtil.exit();
        }
    }

    @Override
    public CommonDate dateConversion(Object date) {
        Context ctx = null;
        try {
            ctx = ContextUtil.enter();
            return (CommonDate)convertDate.call(
                    ctx, incognito, incognito, new Object[] {date});
        }
        finally {
            ContextUtil.exit();
        }
    }

    @Override
    public boolean ownsObject(Object obj) {
        return obj.getClass().getName().startsWith(RHINO_IDENTIFIER);
    }

}
