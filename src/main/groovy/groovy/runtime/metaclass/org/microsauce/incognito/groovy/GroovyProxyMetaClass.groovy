package groovy.runtime.metaclass.org.microsauce.incognito.groovy

import org.codehaus.groovy.reflection.CachedClass
import org.microsauce.incognito.MetaObject

//
// TODO implement this to support advanced duck-typing and dynamic OO features
//  // TODO robust duck-typing functionality will be very difficult to implement for groovy proxies
// //  a paired down implementation may be feasible: bare bones MetaMethod/MetaProperty
//          - respondsTo
//
class GroovyProxyMetaClass extends groovy.lang.DelegatingMetaClass {

    GroovyProxyMetaClass(MetaClass delegate) {
        super(delegate)
    }

    Object invokeMethod(Object object, String method, Object[] arguments) {
        super.invokeMethod object, method, arguments
    }

    // TODO get/setProperty, respondsTo

    private class ProxyMetaMethod extends MetaMethod {

        MetaObject target
        Runtime thisRuntime

        ProxyMetaMethod(MetaObject target, Runtime thiz) {
            this.name = target.identifier
            this.target = target
            this.thisRuntime = thiz
        }

        @Override
        int getModifiers() { return 0 }

        @Override
        String getName() { return name }

        @Override
        Class getReturnType() { return Object.class }

        @Override
        CachedClass getDeclaringClass() { null }

        @Override
        Object invoke(Object o, Object[] objects) {
            thisRuntime.proxy(
                target.originRuntime.execMethod(target, target.identifier, objects as List))
        }

    }

    private class ProxyMetaProperty extends MetaProperty {

        MetaObject target

        ProxyMetaProperty(String name, Class type) {
            super(name, type)
        }

        @Override
        Object getProperty(Object o) {
            return null  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        void setProperty(Object o, Object o1) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
