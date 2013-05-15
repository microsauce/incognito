package org.microsauce.incognito.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.reflection.CachedClass
import org.microsauce.incognito.MetaObject
import org.microsauce.incognito.Type
import org.microsauce.incognito.Runtime

class GroovyProxy {

    MetaObject obj
    org.microsauce.incognito.Runtime thisRuntime

    @CompileStatic
    GroovyProxy(MetaObject obj, org.microsauce.incognito.Runtime rt) {this.obj = obj; thisRuntime = rt}

    def methodMissing(String name, args) { // TODO this causes stack overflow when method does not exist
        org.microsauce.incognito.Runtime oRuntime = obj.getOriginRuntime()
        if ( oRuntime.id.equals(org.microsauce.incognito.Runtime.ID.GROOVY ) )
            return obj.getTargetObject()."$name"(*args)
        return thisRuntime.proxy(oRuntime.execMethod(obj, name, prepareArgs(args)))
    }

    def propertyMissing(String name, value) {
        org.microsauce.incognito.Runtime oRuntime = obj.getOriginRuntime()
        if ( oRuntime.id.equals(org.microsauce.incognito.Runtime.ID.GROOVY) )
            obj.getTargetObject()."$name" = value
        oRuntime.setProp(obj, name, thisRuntime.wrap(value))
    }

    def propertyMissing(String name) {
        org.microsauce.incognito.Runtime oRuntime = obj.getOriginRuntime()
        if ( oRuntime.id.equals(org.microsauce.incognito.Runtime.ID.GROOVY) )
            return obj.getTargetObject()."$name"
        return thisRuntime.proxy(oRuntime.getProp(obj, name))
    }
    @CompileStatic
    private prepareArgs(args) {
        args.collect {
            org.microsauce.incognito.Runtime oRuntime = obj.getOriginRuntime()
            return oRuntime.proxy(thisRuntime.wrap(it))
        }
    }

    @Override
    @CompileStatic
    List respondsTo(String identifier) {
        def respondsTo = []
        def targetMember = obj.originRuntime.getMember(obj,identifier)
        if ( targetMember == null || targetMember.type == Type.UNDEFINED )
            return respondsTo
        else {
            def metaObjectMethod = new MetaObject(Type.METHOD, obj.originRuntime, obj.targetObject, identifier)
            respondsTo << new ProxyMetaMethod(metaObjectMethod, thisRuntime)
            return respondsTo
        }
    }

    @Override
    MetaProperty hasProperty(String identifier) {
        def hasProperty = null
        def targetMember = obj.originRuntime.getMember(obj,identifier)
        if ( !(targetMember == null || targetMember.equals(obj.originRuntime.undefined()) || Type.METHOD == targetMember.type) ) {
            targetMember.identifier = identifier
            // Type type, Runtime origin, T object, String identifier, MetaObject value
            def metaProperty = new MetaObject(Type.OBJECT, obj.originRuntime, obj.targetObject, identifier, targetMember)
            hasProperty = new ProxyMetaProperty(metaProperty, thisRuntime)
        }

        hasProperty
    }

    private class ProxyMetaMethod extends MetaMethod {

        MetaObject target
        Runtime thisRuntime
        String _name

        ProxyMetaMethod(MetaObject target, Runtime thiz) {
            this._name = target.identifier
            this.target = target
            this.thisRuntime = thiz
        }

        @Override
        int getModifiers() { return 0 }

        @Override
        String getName() { return _name }

        String setName(String name) {_name = name}

        @Override
        Class getReturnType() { return Object.class }

        @Override
        CachedClass getDeclaringClass() { null }

        @Override
        Object invoke(Object o, Object[] objects) {
            thisRuntime.proxy(
                    target.originRuntime.execMethod(target, target.identifier, objects as List))
        }

        @Override
        String toString() {
            return _name
        }
    }

    private class ProxyMetaProperty extends MetaProperty {

        MetaObject target
        Runtime thisRuntime

        ProxyMetaProperty(MetaObject target, Runtime thiz) {
            super(target.identifier, Object.class)
            this.target = target
            this.thisRuntime = thiz
        }

        @Override
        Object getProperty(Object o) {
            thisRuntime.proxy(target.value)
        }

        @Override
        void setProperty(Object o, Object o1) {
            target.originRuntime.setProp(target, target.identifier, thisRuntime.wrap(o1))
        }
    }

}
