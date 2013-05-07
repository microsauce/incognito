import groovy.transform.CompileStatic
import org.microsauce.incognito.MetaObject

class GroovyProxy implements GroovyInterceptable {

    MetaObject obj
    org.microsauce.incognito.Runtime thisRuntime

    @CompileStatic
    GroovyProxy(MetaObject obj, org.microsauce.incognito.Runtime rt) {this.obj = obj; thisRuntime = rt}

    def invokeMethod(String name, args) {
        org.microsauce.incognito.Runtime oRuntime = obj.getOriginRuntime()
        if ( oRuntime.id.equals(org.microsauce.incognito.Runtime.ID.GROOVY) )
            return obj.getTargetObject()."$name" *args
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
}
