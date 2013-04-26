package org.microsauce.incognito.rhino;

import org.microsauce.incognito.Lang;
import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.Type;
import org.mozilla.javascript.*;
import org.ringojs.wrappers.ScriptableMap;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class RhinoRuntime extends Runtime {

    private static String RHINO_IDENTIFIER = "org.mozilla";

    private NativeFunction newRhinoExecutableProxy;

    public RhinoRuntime(Lang lang, Object runtime, Object scope) {
        super(lang, runtime, scope);
    }

    @Override
    protected void doInitialize() {
        Context ctx = null;
        try {
            ctx = Context.getCurrentContext();
            if ( ctx == null ) ctx = Context.enter();
            InputStreamReader reader = new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream("incognito-rhino.js"));
            try {
                ctx.evaluateReader((ScriptableObject)scope, reader, "incognito-rhino.js", 1, null);
            } catch (IOException e) {throw new RuntimeException(e);}
            newRhinoExecutableProxy = (NativeFunction)((ScriptableObject) scope).get("newRhinoExecutableProxy");
        }
        finally {
            if (ctx != null) ctx.exit();
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected String objIdentifier() {
        return RHINO_IDENTIFIER;
    }

    @Override
    public MetaObject getProp(MetaObject target, String name) {
        return wrap(((ScriptableObject) target.getTargetObject()).get(name));
    }

    @Override
    protected void doSetProp(MetaObject target, String name, MetaObject value) {
        ((ScriptableObject)target.getTargetObject()).put(
                name, (ScriptableObject)target.getTargetObject(), proxy(wrap(value)));
    }

    @Override
    public MetaObject execMethod(MetaObject target, String name, List args) {
        ScriptableObject thisScope = (ScriptableObject) target.getTargetObject();
        MetaObject metaFunc = getProp(target, name);
        return doExec((NativeFunction)metaFunc.getTargetObject(), thisScope, args);
    }

    @Override
    public MetaObject exec(MetaObject target, Object executionContext, List args) {
        return doExec((NativeFunction)target.getTargetObject(), (ScriptableObject) scope, args);
    }

    private MetaObject doExec(NativeFunction func, ScriptableObject scope, List args) {
        org.mozilla.javascript.Context ctx = null;
        try {
            ctx = org.mozilla.javascript.Context.getCurrentContext();
            if ( ctx == null )
                ctx = org.mozilla.javascript.Context.enter();
            Object retValue = func.call(
                    ctx, scope, scope, args.toArray());
            return wrap(retValue);
        }
        finally {
            if ( ctx != null ) ctx.exit();
        }
    }

    @Override
    public Type typeof(Object obj) {
        if ( obj instanceof String ) return Type.PRIMITIVE;
        if ( obj instanceof Integer ) return Type.PRIMITIVE;
        if ( obj instanceof Float ) return Type.PRIMITIVE;
        if ( obj instanceof Double ) return Type.PRIMITIVE;
        if ( obj instanceof NativeObject) return Type.OBJECT;
        if ( obj instanceof NativeArray) return Type.ARRAY;
        // NativeDate is not public
        if ( obj.getClass().getName().equals("org.mozilla.javascript.NativeDate") )
            return Type.DATE;
        return null;
    }

    @Override
    public Object objectProxy(MetaObject obj) {
        ProxyMap proxy = new ProxyMap(obj, this);
        return new ScriptableMap((ScriptableObject)scope, proxy);
    }

    @Override
    public Object executableProxy(MetaObject obj) {
        Context ctx = null;
        try {
            ctx = Context.getCurrentContext();
            if ( ctx == null ) ctx = Context.enter();
            return newRhinoExecutableProxy.call(
                    ctx, (ScriptableObject)scope, (ScriptableObject)scope, new Object[] {});
        }
        finally {
            ctx.exit();
        }
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
