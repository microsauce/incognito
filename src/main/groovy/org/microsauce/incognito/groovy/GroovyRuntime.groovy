package org.microsauce.incognito.groovy

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.microsauce.incognito.CommonDate
import org.microsauce.incognito.Lang
import org.microsauce.incognito.MetaObject
import org.microsauce.incognito.Runtime
import org.microsauce.incognito.Type
import org.mozilla.javascript.NativeArray
import org.mozilla.javascript.NativeObject

class GroovyRuntime extends Runtime {

    public GroovyRuntime(Object runtime) {
        super(runtime)
        lang = Lang.GROOVY
        id = org.microsauce.incognito.Runtime.RT.GROOVY
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
    MetaObject exec(MetaObject target, Object executionContext, List args) {
        target.targetObject.call(*args)
    }

    @Override
    Type typeof(Object obj) {
        if ( !obj ) return null
        if ( obj instanceof String ) return Type.PRIMITIVE
        if ( obj instanceof Number ) return Type.PRIMITIVE
        if ( obj instanceof List) return Type.ARRAY
        if ( obj.class.isArray() ) return Type.ARRAY
        if ( obj instanceof Map) return Type.HASH
        if ( obj instanceof DateTime ) return Type.DATE
        return Type.OBJECT
    }

    @Override
    @CompileStatic
    Object dateConversion(Object date) { // TODO Joda time
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
    Object objectProxy(MetaObject obj) {
        // TODO
    }

    @Override
    @CompileStatic
    Object executableProxy(MetaObject obj) {
        return { ...args ->
            return proxy(obj.getOriginRuntime().exec(obj, null, Arrays.asList(args)))
        }
    }

    @Override
    Object dateProxy(MetaObject obj) {
        CommonDate cd = obj.getTargetObject()
        new DateTime(cd.year, cd.month, cd.dayOfMonth, cd.hour, cd.minute, cd.second)
    }

    @Override
    public boolean ownsObject(Object obj) {
        return obj instanceof GroovyObject
    }

}
