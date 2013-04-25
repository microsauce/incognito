package org.microsauce.incognito;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO need a hash & list proxy
 * - this proxy will wrap any object retrieve from the collection in
 * an appropriate adaptor (origin rt adaptor)
 * - any object added / modified will also be wrapped (in a dest RT adaptor).
 * special care is needed to ensure objects don't get double-wrapped (add a check):
 * if obj instance of IncognitoAdaptor then don't wrap
 *
 * the proxy will override get/put/add methods
 *
 * add similar check in each proxy collection.
 *
 * TODO performance optimizations (caching)
 */
public class BaseProxy implements InvocationHandler {

    protected Object target;

    public BaseProxy(Object target) {
        this.target = target;
    }

    // TODO cache the actual target for each method (by name) to avoid overhead on subsequent calls
    public Object invoke(final Object proxy, final Method method,
                         final Object[] args) throws Throwable {
        Object value = null;
        try { // check 'this' first (override target implementation)
            // TODO
            Method targetMethod = this.getClass().getMethod(method.getName(), method.getParameterTypes());
            value = targetMethod.invoke(this, args);
        } catch (NoSuchMethodException nsme) {
            Method targetMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            value = targetMethod.invoke(target, args);
        }

        return value;
    }

}

