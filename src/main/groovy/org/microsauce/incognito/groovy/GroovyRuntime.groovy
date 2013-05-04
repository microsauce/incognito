package org.microsauce.incognito.groovy

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.microsauce.incognito.CommonDate
import org.microsauce.incognito.Lang
import org.microsauce.incognito.MetaObject
import org.microsauce.incognito.Runtime
import org.microsauce.incognito.Type

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
    MetaObject exec(MetaObject target, Object executionContext, List args) {
        target.targetObject.call(*args)
    }

    @Override
    @CompileStatic
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
        return new GroovyProxy(obj, this)
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

    private class GroovyProxy implements GroovyInterceptable {
        MetaObject obj
        Runtime thisRuntime

        @CompileStatic
        GroovyProxy(MetaObject obj, Runtime rt) {this.obj = obj; thisRuntime = rt}

        def invokeMethod(String name, args) {
            Runtime oRuntime = obj.getOriginRuntime()
            if ( oRuntime.id.equals(Runtime.ID.GROOVY) )
                return obj.getTargetObject()."$name" *args
            return thisRuntime.proxy(oRuntime.execMethod(obj, name, prepareArgs(args)))
        }

        def propertyMissing(String name, value) {
            Runtime oRuntime = obj.getOriginRuntime()
            if ( oRuntime.id.equals(Runtime.ID.GROOVY) )
                obj.getTargetObject()."$name" = value
            oRuntime.setProp(obj, name, thisRuntime.wrap(value))
        }

        def propertyMissing(String name) {
            Runtime oRuntime = obj.getOriginRuntime()
            if ( oRuntime.id.equals(Runtime.ID.GROOVY) )
                return obj.getTargetObject()."$name"
            return thisRuntime.proxy(oRuntime.getProp(obj, name))
        }
        @CompileStatic
        private prepareArgs(args) {
            args.collect {
                Runtime oRuntime = obj.getOriginRuntime()
                return oRuntime.proxy(thisRuntime.wrap(it))
            }
        }
    }

}
