package org.microsauce.incognito.groovy

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.microsauce.incognito.CamelCase
import org.microsauce.incognito.CommonDate
import org.microsauce.incognito.FatSnakeCase
import org.microsauce.incognito.Lang
import org.microsauce.incognito.MetaObject
import org.microsauce.incognito.Runtime
import org.microsauce.incognito.SnakeCase
import org.microsauce.incognito.Type
import static org.microsauce.incognito.Runtime.ID

class GroovyRuntime extends Runtime {

    public GroovyRuntime(Object runtime) {
        super(runtime, new CamelCase(), new FatSnakeCase())
        lang = Lang.GROOVY
        id = ID.GROOVY
    }

    @Override
    protected void doInitialize() {}

    @Override
    MetaObject doGetProp(MetaObject target, String name) {
        wrap(target.targetObject."$name")
    }

    @Override
    void doSetProp(MetaObject target, String name, MetaObject value) {
        target.targetObject."$name" = proxy(value)
    }

    @Override
    MetaObject doExecMethod(MetaObject target, String name, List args) {
        wrap(target.targetObject."$name"(*args))
    }

    @Override
    boolean respondTo(MetaObject target, String methodName) {
        def trg = target.getTargetObject()
        def metaClass = target.getTargetObject().metaClass
        def hasProperties = metaClass.hasProperty(trg, methodName)
        def respondsTo = metaClass.respondsTo(trg, methodName)
        hasProperties || respondsTo
    }

    @Override
    Collection members(MetaObject target) {
        List members = []
        def metaClass = target.getTargetObject().metaClass
        members.addAll((metaClass.methods*.name - Object.class.metaClass.methods*.name))
        members.addAll(metaClass.properties*.name)
        members
    }

    @Override
    MetaObject getMember(MetaObject target, String identifier) {
        // if it's a property return the value
        def metaProperty = target.targetObject.metaClass.properties.find {it.name == identifier}
        if ( metaProperty ) return getProp(target, identifier)
        def metaMethod = target.targetObject.metaClass.methods.find {it.name == identifier}  // TODO this is groovy - there may be multiple siggies
        if ( metaMethod ) {
            return new MetaObject(Type.METHOD, target.originRuntime, target.targetObject, identifier)
        }
        else return new MetaObject(Type.UNDEFINED, target.originRuntime, undefined())
    }

    @Override
    String targetToString(MetaObject target) {
        target.toString()
    }

    @Override
    MetaObject exec(MetaObject target, Object executionContext, List args) {
        if (target.getType() == Type.EXECUTABLE) {
            return wrap(target.targetObject.call(*args))
        }
        else if (target.getType() == Type.METHOD) {
            def retValue = execMethod(target, target.identifier, args)
            return retValue
        }
        else throw new RuntimeException("attempting to execute non-executable type: ${target.type}")
    }

    @Override
    @CompileStatic
    Type typeof(Object obj) {
        if ( !obj ) return null
        if ( obj instanceof String ) return Type.PRIMITIVE
        if ( obj instanceof Number ) return Type.PRIMITIVE
        if ( obj instanceof List) return Type.ARRAY
        if ( obj.class.isArray() ) return Type.ARRAY
        if ( obj instanceof Closure) return Type.EXECUTABLE
        if ( obj instanceof Map) return Type.HASH
        if ( obj instanceof DateTime ) return Type.DATE
        if ( obj instanceof Set ) return Type.SET

        return Type.OBJECT
    }

    @Override
    Object undefined() {
        return null
    }

    @Override
    @CompileStatic
    CommonDate dateConversion(Object date) {
        if ( !date ) return null
        DateTime dt = (DateTime) date
        new CommonDate(
                dt.year,
                dt.monthOfYear,
                dt.dayOfMonth().get(),
                dt.hourOfDay().get(),
                dt.minuteOfHour().get(),
                dt.secondOfMinute().get(),
                dt.millisOfSecond().get())
    }

    @Override
    @CompileStatic
    Object objectProxy(MetaObject obj) {
        (Object)new GroovyProxy(obj, this)
    }

    @Override
    @CompileStatic
    Object executableProxy(MetaObject obj) {
        return { ...args ->
            return proxy(obj.getOriginRuntime().exec(obj, null, Arrays.asList(args)))
        }
    }

    @Override
    @CompileStatic
    Object dateProxy(MetaObject obj) {
        CommonDate cd = (CommonDate)obj.getTargetObject()
        new DateTime(cd.year, cd.month, cd.dayOfMonth, cd.hour, cd.minute, cd.second)
    }

    @Override
    @CompileStatic
    public boolean ownsObject(Object obj) {
        return obj instanceof GroovyObject
    }

	@Override
	public Object symbolProxy(MetaObject obj) {
		return obj.getOriginRuntime().symbolToString(obj);
	}
	
	public boolean supportSymbols() {return false;}

	@Override
	public String symbolToString(MetaObject obj) {
		throw new UnsupportedOperationException("RhinoRuntime.symbolToString(obj) is not implemented");
	}

}
