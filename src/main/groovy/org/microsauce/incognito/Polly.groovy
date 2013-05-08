package org.microsauce.incognito

import org.jruby.embed.LocalContextScope
import org.jruby.embed.LocalVariableBehavior
import org.jruby.embed.ScriptingContainer
import org.microsauce.incognito.groovy.GroovyRuntime
import org.microsauce.incognito.jruby.JRubyRuntime
import org.microsauce.incognito.rhino.RhinoRuntime
import org.microsauce.incognito.rhino.ContextUtil
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel

import static org.microsauce.incognito.Runtime.ID

/**
 * Polly is a utility class for writing polyglot test scripts.
 *
 * Usage:
 *
 *  import static org.microsauce.incognito.Polly.*
 *
 *  def kid_proxies = jruby([arg1: 'Hello', arg2: 7]. '''
 *      class Kid
 *        attr_accessor :name, :age
 *
 *        def initialize(name, age)
 *          @name = name; @age = age
 *        end
 *
 *      end
 *
 *      Kid.new(arg1,arg2)
 *  ''', RHINO, GROOVY)
 *
 *  rhino([kid: kid_proxies[RHINO]], '''
 *      for ( prop in kid ) {
 *        println(prop + " => " + kid[prop])
 *      }
 *  ''')
 */
public class Polly {

    static Runtime.ID JRUBY = Runtime.ID.JRUBY
    static Runtime.ID GROOVY = Runtime.ID.GROOVY
    static Runtime.ID RHINO = Runtime.ID.RHINO

    static Map<Runtime.ID,Runtime> runtimes = [:]

    static Incognito incognito
    static rbNdx = 0
    static jsNdx = 0

    static registerRuntime(Runtime rt) {
        if ( rt ) runtimes[rt.id] = rt
        return rt
    }

    static groovy(...args) {
        groovy(*standardizeArgs(args))
    }

    static groovy(Map args, String scriptlet, List<ID> rtIds) {
        def shell = new GroovyShell(args ?: [:] as Binding)
        def retValue = shell.evaluate(scriptlet)
        proxies(retValue, rtIds)
    }

    static jruby(...args) {
        jruby(*standardizeArgs(args))
    }

    static jruby(Map args, String scriptlet, List<ID> rtIds) {
        def jruby = nativeRt(JRUBY)
        args.each {key,value ->
            jruby.put key, value
        }
        def stream = new ByteArrayInputStream(scriptlet.bytes)
        def retValue = nativeRt(JRUBY).runScriptlet(stream, "scriptlet_${rbNdx++}.rb")

        proxies(retValue, rtIds)
    }

    static rhino(...args) {
        rhino(*standardizeArgs(args))
    }

    static rhino(Map args, String scriptlet, List<ID> rtIds) {
        def retValue = null
        def rhino = nativeRt(RHINO)

        Context ctx = ContextUtil.enter()
        try {
            args.each { key, value ->
                rhino.put(key, rhino, value)
            }
            retValue = ctx.evaluateString(rhino, scriptlet, "scriptlet_${jsNdx++}.js", 1, null)
        }
        finally { ContextUtil.exit();}
        return proxies(retValue, rtIds)
    }


    static nativeRt(Runtime.ID rtId) {
       runtime(rtId).runtime
    }

    static Incognito incognito() {
        if ( !incognito ) {
            incognito = new Incognito()
            Runtime.ID.each {
                def rt = runtimes[it]
                if (!rt) {
                    rt = registerDefault(it)
                    runtimes[it] = rt
                }
                incognito.registerRuntime(rt)
            }
        }
        return incognito
    }

    //
    // private methods
    //

    static private registerDefault(Runtime.ID rtId) {
        if ( rtId == JRUBY ) {
            return registerRuntime(new JRubyRuntime(
                    new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.PERSISTENT)))
        } else if (rtId == RHINO) {
            Context ctx = null
            def scriptableObject = null
            try {
                ctx = ContextUtil.enter()
                scriptableObject = new ImporterTopLevel(ctx)
                scriptableObject.put('out', scriptableObject, System.out)
                ctx.evaluateString(scriptableObject, '''
                    function println(str) {
                        out.println(str);
                    }
                ''', "init.js", 1, null)
            } finally {ContextUtil.exit();}
            return registerRuntime(new RhinoRuntime(scriptableObject))
        } else if (rtId == GROOVY) return registerRuntime(new GroovyRuntime())
    }

    static proxy(ID rtId, Object obj) {
        if ( rtId ) {
            return incognito().assumeIdentity(rtId, obj)
        } else return obj
    }

    static assumeIdentity(ID rtId, Object obj) {
        proxy(rtId, obj)
    }

    static private Runtime runtime(Runtime.ID rtId) {
        def rt = runtimes[rtId]
        if ( !rt ) return registerDefault(rtId)
        rt
    }

    private static proxies(Object retValue, List<ID>rtIds) {
        def proxies = [:]
        rtIds.each { rtId ->
            proxies[rtId] = proxy(rtId, retValue)
        }
        if ( proxies.size() == 1 ) return proxies.values().iterator().next()
        else if ( proxies.size() == 0 ) return null
        else return proxies
    }

    private static List standardizeArgs(...args) {
        def standardizedArgs = []
        switch (args.length) {
            case 0: throw new RuntimeException('illegal arguments: a scriptlet must be provided')
            case 1:
                if (!(args[0] instanceof String)) throw new RuntimeException('illegal arguments: a scriptlet must be provided')
                standardizedArgs.addAll([null, args[0],[]])
                return standardizedArgs
            case 2:
                if (args[0] instanceof Map && args[1] instanceof String) {
                    standardizedArgs.addAll([args[0], args[1], []])
                    return standardizedArgs
                } else if (args[0] instanceof String && args[1] instanceof ID) {
                    standardizedArgs.addAll([null, args[0], [args[1]]])
                    return standardizedArgs
                } else throw new RuntimeException("illegal arguments: ${args.join(', ')}")
            default: // 3 or more
                if (args[0] instanceof Map && args[1] instanceof String && theRestAreRuntimeIDs(args[2..args.length-1])) {
                    standardizedArgs.addAll([args[0], args[1], args[2..args.length-1]])
                    return standardizedArgs
                } else if (args[0] instanceof String && theRestAreRuntimeIDs(args[1..args.length-1])) {
                    standardizedArgs.addAll([null, args[0], args[1..args.length-1]])
                    return standardizedArgs
                } else throw new RuntimeException("illegal arguments: ${args.join(', ')}")
        }
    }

    private static boolean theRestAreRuntimeIDs(slice) {
        for ( item in slice ) {
            if (!(item instanceof ID)) return false;
        }
        return true;
    }

}
