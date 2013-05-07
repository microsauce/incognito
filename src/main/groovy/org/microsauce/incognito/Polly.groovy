package org.microsauce.incognito

import org.jruby.embed.LocalContextScope
import org.jruby.embed.LocalVariableBehavior
import org.jruby.embed.ScriptingContainer
import org.microsauce.incognito.groovy.GroovyRuntime
import org.microsauce.incognito.jruby.JRubyRuntime
import org.microsauce.incognito.rhino.RhinoRuntime
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel

import static org.microsauce.incognito.Runtime.ID
import static org.microsauce.incognito.Runtime.ID.*

/**
 * Polly is a utility class for writing polyglot test scripts.
 *
 * Usage:
 * in your groovy test script:
 *
 *  import static org.microsauce.incognito.Polly.*
 *  import static org.microsauce.incognito.Runtime.ID.*
 *
 *  def kid_rhinoProxy = jruby([arg1: 'Hello', arg2: 7]. '''
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
 *  ''', RHINO)
 *
 *  rhino([kid: kid_rhinoProxy], '''
 *      for ( prop in kid ) {
 *        println(prop + " => " + kid[prop])
 *      }
 *  ''')
 */
public class Polly {

    static Map<Runtime.ID,Runtime> runtimes = [:]

    static Incognito incognito
    static rbNdx = 0
    static jsNdx = 0

    static registerRuntime(Runtime rt) {
        if ( rt ) runtimes[rt.id] = rt
        return rt
    }

    static groovy(Map args, Closure scriptlet) {
        groovy(args, scriptlet, null)
    }

    static groovy(Map args, Closure scriptlet, ID rtId) {
        scriptlet.delegate = args as Binding
        scriptlet.resolveStrategy = Closure.DELEGATE_FIRST
        def retValue = scriptlet.call()
        proxy(rtId, retValue)
    }

    static jruby(Map args, String scriptlet) {
        jruby(args, scriptlet, null)
    }

    static jruby(Map args, String scriptlet, ID rtId) {
        def jruby = nativeRt(JRUBY)
        args.each {key,value ->
            jruby.put key, value
        }
        def stream = new ByteArrayInputStream(scriptlet.bytes)
        def retValue = nativeRt(JRUBY).runScriptlet(stream, "scriptlet_${rbNdx++}.rb")
        proxy(rtId, retValue)
    }

    static rhino(Map args, String scriptlet) {
        rhino(args, scriptlet, null)
    }

    static rhino(Map args, String scriptlet, ID rtId) {
        Context ctx = Context.enter()
        def retValue = null
        def rhino = nativeRt(RHINO)
        try {
            args.each { key, value ->
                rhino.put(key, rhino, value)
            }
            retValue = ctx.evaluateString(rhino, scriptlet, "scriptlet_${jsNdx++}.js", 1, null)
        }
        finally { ctx.exit() }
        return proxy(rtId, retValue)
    }

    static private proxy(ID rtId, Object obj) {
        if ( rtId ) {
            return incognito().assumeIdentity(rtId, obj)
        } else return obj
    }

    static private Runtime runtime(Runtime.ID rtId) {
        def rt = runtimes[rtId]
        if ( !rt ) return registerDefault(rtId)
        rt
    }

    static nativeRt(Runtime.ID rtId) {
       runtime(rtId).runtime
    }

    static private registerDefault(Runtime.ID rtId) {
        if ( rtId == JRUBY ) {
            return registerRuntime(new JRubyRuntime(
                    new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.PERSISTENT)))
        } else if (rtId == RHINO) {
            Context ctx = null
            def scriptableObject = null
            try {
                ctx = Context.enter()
                scriptableObject = new ImporterTopLevel(ctx)
                scriptableObject.put('out', scriptableObject, System.out)
                ctx.evaluateString(scriptableObject, '''
                    function println(str) {
                        out.println(str);
                    }
                ''', "init.js", 1, null)
            } finally {ctx.exit()}
            return new RhinoRuntime(scriptableObject)
        } else if (rtId == GROOVY) return new GroovyRuntime()
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
}
