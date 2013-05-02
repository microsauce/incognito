package org.microsauce.incognito.jruby;

import org.jruby.*;
import org.jruby.embed.ScriptingContainer;
import org.microsauce.incognito.Lang;
import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.Type;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JRubyRuntime extends Runtime {

    private static String JRUBY_IDENTIFIER = "org.jruby";

    private RubyObject incognito;

    public JRubyRuntime(Object runtime, Object scope) {
        super(runtime, scope);
        lang = Lang.RUBY;
        id = RT.JRUBY;
    }

    @Override
    protected void doInitialize() {
        ScriptingContainer container = (ScriptingContainer)runtime;
        InputStream in =
                this.getClass().getClassLoader().getResourceAsStream("incognito-jruby.rb");
        container.runScriptlet(in, "incongnito-jruby.rb");

        incognito = (RubyObject)container.get("jruby_incognito");
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
System.out.println("JRubyRuntime.execMethod: target: " + target + " - name: " + name + " - args: " + args);
        return wrap(((ScriptingContainer) runtime).callMethod(
                target.getTargetObject(), name, args.toArray()));
    }

    @Override
    public MetaObject exec(MetaObject target, Object executionContext, List args) {
        args.add(0,target.getTargetObject());
        return wrap(((ScriptingContainer) runtime).callMethod(
                incognito, "exec_proc", args));
    }

    @Override
    public Type typeof(Object obj) {
System.out.println("JRubyRuntime.typeof: obj: " + obj);

System.out.println("\t1");
        if ( obj instanceof String ) return Type.PRIMITIVE;
System.out.println("\t2");
        if ( obj instanceof Integer ) return Type.PRIMITIVE;
System.out.println("\t3");
        if ( obj instanceof Float ) return Type.PRIMITIVE;
System.out.println("\t4");
        if ( obj instanceof Double ) return Type.PRIMITIVE;
System.out.println("\t5");
        if ( obj instanceof RubyObject ) {
System.out.println("\t6");
            RubyObject rObj = (RubyObject) obj;
            String rubyClassName = rObj.getType().getName();
System.out.println("\t7");
            if ( rubyClassName.equals("Set") ) return Type.SET;
            else if ( rubyClassName.equals("DateTime") ) return Type.DATE;
            else return Type.OBJECT;
        }
        if ( obj instanceof RubyArray ) return Type.ARRAY;
System.out.println("\t8");
        if ( obj instanceof RubyHash ) return Type.HASH;
System.out.println("\t9");
        if ( obj instanceof RubyProc ) return Type.EXECUTABLE;
System.out.println("\t10");
        return null;
    }

    @Override
    public Object dateConversion(Object date) {
        return ((ScriptingContainer)runtime).callMethod(incognito, "convert_date", new Object[] {date});
    }

    @Override
    public Object objectProxy(MetaObject obj) {
        return ((ScriptingContainer)runtime).callMethod(incognito, "create_obj_proxy", new Object[] {obj, this});
    }

    @Override
    public Object executableProxy(MetaObject obj) {
        return ((ScriptingContainer)runtime).callMethod(incognito, "create_exec_proxy", new Object[] {obj, this});
    }

    @Override
    public Object dateProxy(MetaObject obj) {
        return ((ScriptingContainer)runtime).callMethod(incognito, "create_ruby_date", new Object[] {obj});
    }
}
