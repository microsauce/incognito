package org.microsauce.incognito.test

import org.jruby.embed.LocalContextScope
import org.jruby.embed.LocalVariableBehavior
import org.jruby.embed.ScriptingContainer
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject

/**
 * Polly is a utility class for writing polyglot test scripts.
 */
public class Polly {

    static ScriptingContainer jruby
    static ScriptableObject rhino
    static rbNdx = 0
    static jsNdx = 0

    static {
        jruby = new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.PERSISTENT);
        Context ctx = null
        try {
            ctx = Context.enter()
            rhino = new ImporterTopLevel(ctx)
            rhino.put('out', rhino, System.out)
            ctx.evaluateString(rhino, '''
                function println(str) {
                    out.println(str);
                }
            ''', "init.js", 1, null)
        } finally {ctx.exit()}

    }

    static groovy(Map args, Closure scriptlet) {
        scriptlet.delegate = args as Binding
        scriptlet.resolveStrategy = Closure.DELEGATE_FIRST
        scriptlet.call()
    }

    static jruby(Map args, String scriptlet) {
        args.each {key,value ->
            jruby.put key, value
        }
        def stream = new ByteArrayInputStream(scriptlet.bytes)
        jruby.runScriptlet(stream, "scriptlet_${rbNdx++}.rb");
    }

    static rhino(Map args, String scriptlet) {
        Context ctx = Context.enter()
        try {
            args.each { key, value ->
                rhino.put(key, rhino, value)
            }
            ctx.evaluateString(rhino, scriptlet, "scriptlet_${jsNdx++}.js", 1, null)
        }
        finally { ctx.exit() }

    }
}
