package org.microsauce.incognito.groovy

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.microsauce.incognito.CommonDate
import org.microsauce.incognito.Lang
import org.microsauce.incognito.MetaObject
import org.microsauce.incognito.Runtime
import org.microsauce.incognito.Type
import static org.microsauce.incognito.Runtime.ID

class GroovyRuntime extends Runtime {

    public GroovyRuntime(Object runtime) {
        super(runtime)
        lang = Lang.GROOVY
        id = ID.GROOVY
    }

    @Override
    protected void doInitialize() {}

    @Override
    MetaObject getProp(MetaObject target, String name) {
        wrap(target.targetObject."$name")
    }

    @Override
    void setProp(MetaObject target, String name, MetaObject value) {
        target.targetObject."$name" = proxy(value)
    }

    @Override
    MetaObject execMethod(MetaObject target, String name, List args) {
        wrap(target.targetObject."$name" *args)
    }

    @Override
    boolean respondTo(MetaObject target, String methodName) {
        def trg = target.getTargetObject()
        def metaClass = target.getTargetObject().metaClass
        def hasProperties = metaClass.hasProperty(trg, methodName)
        def respondsTo = metaClass.respondsTo(trg, metaClass)
        hasProperties || respondsTo
    }

    @Override
    Collection members(MetaObject target) {
        List members = []
        def metaClass = target.getTargetObject().metaClass
        members.addAll(metaClass.methods.name)
        members.addAll(metaClass.properties.name)
        members
    }

    @Override
    MetaObject getMember(MetaObject target, String identifier) {
        // if it's a property return the value
        def metaProperty = target.targetObject.metaClass.properties.find {it.name == identifier}
        if ( metaProperty ) return target.targetObject[identifier]        // TODO broken
        else return MetaObject(Type.METHOD, target.originRuntime, target.targetObject, identifier)
    }

    @Override
    MetaObject execMember(MetaObject target, Object executionContext, List args) {
        execMethod(target, target.identifier, args)
    }

    @Override
    MetaObject exec(MetaObject target, Object executionContext, List args) {
        if (target.getType() == Type.EXECUTABLE)
            return target.targetObject.call(*args)
        else
            return execMethod(target. target.getIdentifier(), args);
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

        return Type.OBJECT
    }

    @Override
    @CompileStatic
    Object dateConversion(Object date) {
        if ( !date )  return date
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


}
