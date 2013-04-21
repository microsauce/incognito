package org.microsauce.incognito;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 */
abstract class BaseProxy implements InvocationHandler {

    protected Object target;

    BaseProxy(Object target) {
        this.target = target;
    }

    // TODO cache the actual target for each method (by name) to avoid overhead on subsequent calls
    public Object invoke(final Object proxy, final Method method,
                         final Object[] args) throws Throwable {
        Object value = null;
        try { // check 'this' first to override target implementation
            Method targetMethod = this.getClass().getMethod(method.getName(), method.getParameterTypes());
            value = targetMethod.invoke(this, args);
        }
        catch (NoSuchMethodException nsme) {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            value = targetMethod.invoke(target, args);
        }

        return value;
    }

}

