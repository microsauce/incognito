package org.microsauce.incognito.jruby;

import org.jruby.*;
import org.jruby.embed.ScriptingContainer;
import org.microsauce.incognito.*;
import org.microsauce.incognito.Runtime;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JRubyRuntime extends Runtime {

    private static String JRUBY_IDENTIFIER = "org.jruby";

    private RubyObject incognito;

    public JRubyRuntime(Object runtime) {
        super(runtime, new SnakeCase(), new FatSnakeCase());
        lang = Lang.RUBY;
        id = ID.JRUBY;
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
    public MetaObject doGetProp(MetaObject target, String name) {
        return execMethod(target, name, new ArrayList());
    }

    @Override
    public void doSetProp(MetaObject target, String name, MetaObject value) {
        List arg = new ArrayList();
        arg.add(value);
        execMethod(target, name, arg);
    }

    @Override
    public MetaObject doExecMethod(MetaObject target, String name, List args) {
        return wrap(((ScriptingContainer) runtime).callMethod(
                target.getTargetObject(), name, args.toArray()));
    }

    @Override
    public boolean respondTo(MetaObject target, String methodName) {
        return (Boolean)((ScriptingContainer) runtime).callMethod(
                incognito, "target_respond_to", new Object[] {target.getTargetObject(), methodName});
    }

    @Override
    public Collection members(MetaObject target) {
        return (Collection)((ScriptingContainer) runtime).callMethod(
                incognito, "target_members", new Object[] {target.getTargetObject()});
    }

    @Override
    public MetaObject getMember(MetaObject target, String identifier) {
        if( !respondTo(target, identifier) ) return new MetaObject(Type.UNDEFINED, target.getOriginRuntime(), undefined());
        Long arity = (Long)((ScriptingContainer) runtime).callMethod(
            incognito, "method_arity", new Object[] {target.getTargetObject(), identifier});
        // zero-argument methods will be considered 'properties' - return value
        if ( arity == 0 )
            return execMethod(target, identifier, new ArrayList());
        else
            return new MetaObject(Type.METHOD, target.getOriginRuntime(), target.getTargetObject(), identifier);
    }

    @Override
    public String targetToString(MetaObject target) {
        return (String)((ScriptingContainer) runtime).callMethod(
                incognito, "target_to_s", new Object[] {target.getTargetObject()});
    }

    @Override
    public MetaObject exec(MetaObject target, Object executionContext, List args) {
        if ( target.getType() == Type.EXECUTABLE ) {
//            args.add(0,target.getTargetObject());
            List rbArgs = new ArrayList();
            rbArgs.add(target.getTargetObject());
            rbArgs.addAll(args);
            return wrap(((ScriptingContainer) runtime).callMethod(
                    incognito, "exec_proc", rbArgs.toArray()));
        } else {
            // Type.METHOD
            return execMethod(target, target.getIdentifier(), args);
        }
    }

    public MetaObject wrap(Object obj) {
    	if ( obj instanceof org.jruby.RubySymbol ) 
    		obj = obj.toString();
    	return super.wrap(obj);
    }
    
    public Object toSymbol(Object obj) {
    	if ( obj instanceof RubySymbol ) return obj;
    	else return (Boolean)((ScriptingContainer) runtime).callMethod(
                incognito, "to_sym", new Object[] {obj});
    }
    
    @Override
    public Type typeof(Object obj) {
        if ( obj == null ) return null;
        if ( obj instanceof String || obj instanceof org.jruby.RubySymbol) return Type.PRIMITIVE;
        if ( obj instanceof Number ) return Type.PRIMITIVE;
        if ( obj instanceof RubyArray ) return Type.ARRAY;
        if ( obj instanceof RubyHash ) return Type.HASH;
        if ( obj instanceof RubyProc ) return Type.EXECUTABLE;
        if ( obj instanceof RubyObject ) {
            RubyObject rObj = (RubyObject) obj;
            String rubyClassName = rObj.getType().getName();
            if ( rubyClassName.equals("Set") ) return Type.SET;
            else if ( rubyClassName.equals("DateTime") ) return Type.DATE;
            else return Type.OBJECT;
        }
        return null;
    }

    @Override
    public Object undefined() {
        return null;
    }

    @Override
    public CommonDate dateConversion(Object date) {
        return (CommonDate)((ScriptingContainer)runtime).callMethod(incognito, "convert_date", new Object[] {date});
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
    
    @Override
    public Object symbolProxy(MetaObject obj) {
    	return obj.getTargetObject();
    }
    
    @Override
    public String symbolToString(MetaObject symbol) {
    	return symbol.getTargetObject().toString();
    }
    
    public boolean supportSymbols() {return true;}
    
    @Override
    public boolean ownsObject(Object obj) {
        return obj.getClass().getName().startsWith(JRUBY_IDENTIFIER);
    }
}
