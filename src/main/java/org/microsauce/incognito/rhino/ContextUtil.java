package org.microsauce.incognito.rhino;

import org.mozilla.javascript.Context;

public class ContextUtil {

    private static ThreadLocal<Context> threadLocalContext = new ThreadLocal<Context>();

    /**
     * used to manage nested enter/exit call pairs
     */
    private static ThreadLocal<Integer> threadLocalSymetry = new ThreadLocal<Integer>();


    public static Context enter() {
        upTick();
        Context ctx = threadLocalContext.get();
        if ( ctx == null ) {
            ctx = Context.enter();
            threadLocalContext.set(ctx);
        }
        return ctx;
    }

    public static void exit() {
        downTick();
        if ( symetric() ) {
            Context ctx = threadLocalContext.get();
            if ( ctx != null ) {
                ctx.exit();
                threadLocalContext.remove();
            }
        }

    }

    private static Integer upTick() {
        Integer cnt = threadLocalSymetry.get();
        if ( cnt == null ) cnt = 0;
        cnt+=1;
        threadLocalSymetry.set(cnt);
        return cnt;
    }

    private static Integer downTick() {
        Integer cnt = threadLocalSymetry.get();
        if ( cnt == null ) throw new RuntimeException("a context has not yet been entered for this thread");
        cnt=-1;
        threadLocalSymetry.set(cnt);
        return cnt;
    }

    private static boolean symetric() {
        Integer cnt = threadLocalSymetry.get();
        if (cnt.equals(0)) return true;
        else return false;
    }

}
