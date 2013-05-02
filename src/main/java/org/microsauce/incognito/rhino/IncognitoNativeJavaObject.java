package org.microsauce.incognito.rhino;

import org.microsauce.incognito.Runtime;
import org.microsauce.incognito.MetaObject;
import org.mozilla.javascript.*;

public class IncognitoNativeJavaObject extends NativeJavaObject {

    MetaObject target;
    Runtime thisRuntime;

    public IncognitoNativeJavaObject(MetaObject target, Runtime thisRuntime) {
        this.target = target;
        this.thisRuntime = thisRuntime;
    }

    public Object get(String name, Scriptable start) {
//System.out.println("IncognitoNativeJavaObject.get: " + name);
//MetaObject mo = target.getOriginRuntime().getProp(target, name);
//System.out.println("\tmo: " + mo);
//System.out.println("\tmo.target: " + mo.getTargetObject());
//System.out.println("\tmo.target.type: " + mo.getType());
        return thisRuntime.proxy(target.getOriginRuntime().getProp(target, name));
    }
    public void put(String name, Scriptable start, Object value) {
        Runtime oRuntime = target.getOriginRuntime();
        oRuntime.setProp(target, name, thisRuntime.wrap(value));
    }
    public Object getDefaultValue(Class hint) {
        return target.toString();
    }
}
