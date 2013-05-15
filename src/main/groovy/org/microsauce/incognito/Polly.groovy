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
 * Polly is a utility class for writing polyglot scripts.
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

    /**
     * The JRuby runtime ID
     */
    static Runtime.ID JRUBY = Runtime.ID.JRUBY

    /**
     * The Groovy runtime ID
     */
    static Runtime.ID GROOVY = Runtime.ID.GROOVY

    /**
     * The Rhino runtime ID
     */
    static Runtime.ID RHINO = Runtime.ID.RHINO

    static Map<Runtime.ID,Runtime> runtimes = [:]
    static Incognito incognito
    static rbNdx = 0
    static jsNdx = 0

    /**
     * Register an Incognito Runtime with Polly.
     *
     * @param rt
     * @return
     */
    static registerRuntime(Runtime rt) {
        if ( rt ) runtimes[rt.id] = rt
        return rt
    }

    /**
     * Execute a Groovy scriptlet, return the resulting value.  If there is no GroovyRuntime registered with Polly then
     * a default GroovyRuntime is registered.
     *
     * @param args an argument array of the form ([argumentMap,] scriptlet[, Runtime.ID_1..Runtime.ID_N]).  scriptlet is the only
     * required parameter. argumentMap - a map of identifiers/values to bind to the scriptlet runtime, runtime.ids - specify each proxy
     * in which to wrap the script return value
     *
     * @return when no Runtime.ID is specified -- the script return value (raw).  when one Runtime.ID is specified - a proxy of the
     * specified type.  when multiple Runtime.IDs are specified - a Runtime.ID => proxy mapping
     */
    static groovy(...args) {
        groovy(*standardizeArgs(args))
    }

    /**
     * Execute a Groovy scriptlet, return the resulting value.  If there is no GroovyRuntime registered with Polly then
     * a default GroovyRuntime is registered.
     *
     * @param args
     * @param scriptlet
     * @param rtIds
     * @return
     */
    static groovy(Map args, String scriptlet, List<ID> rtIds) {
        def binding = new Binding(args ?: [:])
        def shell = new GroovyShell(binding)
        def retValue = shell.evaluate(scriptlet)
        proxies(retValue, rtIds)
    }

    /**
     * Execute a Jruby scriptlet, return the resulting value.  If there is no JrubyRuntime registered with Polly then
     * a default JRubyRuntime is registered.
     *
     * @param args an argument array of the form ([argumentMap,] scriptlet[, Runtime.ID_1..Runtime.ID_N]).  scriptlet is the only
     * required parameter. argumentMap - a map of identifiers/values to bind to the scriptlet runtime, runtime.ids - specify each proxy
     * in which to wrap the script return value
     *
     * @return when no Runtime.ID is specified -- the script return value (raw).  when one Runtime.ID is specified - a proxy of the
     * specified type.  when multiple Runtime.IDs are specified - a Runtime.ID => proxy mapping
     */
    static jruby(...args) {
        jruby(*standardizeArgs(args))
    }

    /**
     * Execute a Jruby scriptlet, return the resulting value.  If there is no JrubyRuntime registered with Polly then
     * a default JrubyRuntime is registered.
     *
     * @param args
     * @param scriptlet
     * @param rtIds
     * @return
     */
    static jruby(Map args, String scriptlet, List<ID> rtIds) {
        def jruby = nativeRt(JRUBY)
        args.each {key,value ->
            jruby.put key, value
        }
        def stream = new ByteArrayInputStream(scriptlet.bytes)
        def retValue = nativeRt(JRUBY).runScriptlet(stream, "scriptlet_${rbNdx++}.rb")

        proxies(retValue, rtIds)
    }

    /**
     * Execute a Rhino scriptlet, return the resulting value.  If there is no RhinoRuntime registered with Polly then
     * a default RhinoRuntime is registered.
     *
     * @param args an argument array of the form ([argumentMap,] scriptlet[, Runtime.ID_1..Runtime.ID_N]).  scriptlet is the only
     * required parameter. argumentMap - a map of identifiers/values to bind to the scriptlet runtime, runtime.ids - specify each proxy
     * in which to wrap the script return value
     *
     * @return when no Runtime.ID is specified -- the script return value (raw).  when one Runtime.ID is specified - a proxy of the
     * specified type.  when multiple Runtime.IDs are specified - a Runtime.ID => proxy mapping
     */
    static rhino(...args) {
        rhino(*standardizeArgs(args))
    }

    /**
     * Execute a Rhino scriptlet, return the resulting value.  If there is no RhinoRuntime registered with Polly then
     * a default RhinoRuntime is registered.
     *
     * @param args
     * @param scriptlet
     * @param rtIds
     * @return
     */
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

    /**
     * Retrieve the 'native' runtime object (Scriptable, ScriptingContainer, etc).
     *
     * @param rtId
     * @return
     */
    static nativeRt(Runtime.ID rtId) {
       runtime(rtId).runtime
    }

    /**
     * Retrieve the Incognito instance associate with Polly.
     *
     * @return
     */
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

    /**
     * Wrap the given object in a proxy suitable for the given Runtime.ID
     *
     * @param rtId the Runtime.ID
     * @param obj the target object
     * @return a proxy suitable for the given Runtime.ID
     */
    static proxy(ID rtId, Object obj) {
        if ( rtId ) {
            return incognito().assumeIdentity(rtId, obj)
        } else return obj
    }

    /**
     * An alias for proxy
     *
     * @param rtId
     * @param obj
     * @return
     */
    static assumeIdentity(ID rtId, Object obj) {
        proxy(rtId, obj)
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
            case 0: throw new RuntimeException('illegal arguments: a scriptlet must be provided: ([params,] scriptlet[, Runtime.ID_1..Runtime.ID_N])')
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
