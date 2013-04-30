package org.microsauce.incognito.jruby;

import org.jruby.*;
import org.jruby.embed.ScriptingContainer;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.microsauce.incognito.Lang;
import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.Type;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JRubyRuntime extends Runtime {

    private static String JRUBY_IDENTIFIER = "org.jruby";

    private RubyModule incognitoModule;

    public JRubyRuntime(Lang lang, Object runtime, Object scope) {
        super(lang, runtime, scope);
    }

    @Override
    protected void doInitialize() {
        ScriptingContainer container = (ScriptingContainer)runtime;
        InputStream in =
                this.getClass().getClassLoader().getResourceAsStream("incognito-jruby.rb");
        container.runScriptlet(in, "incongnito-jruby.rb");

        incognitoModule = (RubyModule)container.get("Incognito");
    }

    @Override
    protected String objIdentifier() {
        return JRUBY_IDENTIFIER;
    }

    @Override
    public MetaObject getProp(MetaObject target, String name) {
        return execMethod(target, name, new ArrayList());
    }

    @Override
    public void setProp(MetaObject target, String name, MetaObject value) {
        List arg = new ArrayList();
        arg.add(value);
        execMethod(target, name, arg);
    }

    @Override
    public MetaObject execMethod(MetaObject target, String name, List args) {
        return wrap(((ScriptingContainer) runtime).callMethod(
                target.getTargetObject(), name, args));
    }

    @Override
    public MetaObject exec(MetaObject target, Object executionContext, List args) {
        ThreadContext ctx = ((ScriptingContainer)runtime).getProvider().getRuntime().getCurrentContext();
        IRubyObject[] rArgs = new IRubyObject[args.size()];
        for ( int i = 0; i < args.size(); i++ ) {
            rArgs[i] = (IRubyObject)args.get(i);
        }
        ((RubyProc)target.getTargetObject()).call(ctx, args.toArray());
        return null;
    }

    @Override
    public Type typeof(Object obj) {
        if ( obj instanceof String ) return Type.PRIMITIVE;
        if ( obj instanceof Integer ) return Type.PRIMITIVE;
        if ( obj instanceof Float ) return Type.PRIMITIVE;
        if ( obj instanceof Double ) return Type.PRIMITIVE;
        if ( obj instanceof RubyObject ) {
            RubyObject rObj = (RubyObject) obj;
            String rubyClassName = rObj.getType().getName();
            if ( rubyClassName.equals("Set") ) return Type.SET;
            else if ( rubyClassName.equals("DateTime") ) return Type.DATE;
            else return Type.OBJECT;
        }
        if ( obj instanceof RubyArray ) return Type.ARRAY;
        if ( obj instanceof RubyHash ) return Type.HASH;
        if ( obj instanceof RubyProc ) return Type.EXECUTABLE;
        return null;
    }

    @Override
    public Object dateConversion(Object date) {
        return null;
    }

    @Override
    public Object objectProxy(MetaObject obj) {
        return null;
    }

    @Override
    public Object executableProxy(MetaObject obj) {
        return null;
    }

    @Override
    public Object dateProxy(MetaObject obj) {
        return null;
    }
}
