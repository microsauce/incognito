incognito
=========

Facilitating idiomatic interoperability of dynamic languages on the JVM.

### the problem
Many languages target the JVM.  Most of these languages have excellent interoperability with Java.  The problem is
(outside of their disparate runtime API's) they don't interoperate well with each other.  Incognito is an attempt to
address this problem.

### overview
Incognito is a library that enables objects created in supported JVM runtimes to enter other JVM runtimes 'incognito'
-- assuming an identity inconspicuous to that runtime.  The library defines adaptors to standardize access to objects in each
supported runtime and it defines proxies to enable idiomatic usage of these objects in receiving runtimes.

### goals

Support interoperablity of objects, primitives (strings, integers, floating point, etc), and literal data-structure types
(arrays, sets, hashes, etc).  Provide support for basic OO features: property access, method invocation, and duck-type
reflection (respond_to?, hasProperty,respondsTo, undefined, etc).  Support for executable/callback types (functions,
closures, lambdas, etc).

### strategy

Leverage, as much as possible, existing support for Java interoperability (particularly with collections and primitives) in proxy/adaptor
implementations.

### status and roadmap
Incognito is currently alpha software.  Proposed roadmap:
* 0.1 - jruby, groovy, rhino, runtime adaptors and proxies, Polly (Groovy)
* 0.2 - jython, Polly (Rhino, JRuby, Jython)
* 1.0 - performance optimizations / polishing
* 1.1 - guice-style polyglot IOC

## usage

Incognito provides a simple and intuitive API which can be called upon directly via the Incognito Java class or indirectly
via Polly (a poly-glot script utility).

### polly usage:
```groovy
    import static org.microsauce.incognito.Polly.*

    def kid_proxies = jruby([arg1: 'Jimmy', arg2: 7], '''
      require 'date'

      class Kid
        attr_accessor :name, :age, :dob, :callback

        def initialize(name, age, dob, &callback)
          @name = name; @age = age; @dob = dob; @callback = callback
        end

        def foobify(prefix)
          return prefix + '- foo - ' + @name + '-ibity '
        end
      end

      Kid.new(arg1,arg2, DateTime.now) do |name|
        puts "hey #{name} I'm a ruby callback"
        'ruby'
      end
    ''', RHINO, GROOVY)

    rhino([kid: kid_proxies[RHINO]], '''

        // print ruby object properties
        println("kid.name: " + kid.name);
        println("kid.age: " + kid.age);
        println("kid.dob: " + kid.dob);
        println("kid.foobify: " + kid.foobify('hey', 8));
        println("kid.callback: " + kid.callback('Steve'));
        println("items:");
        if (kid.set!==undefined) {
            kid.set.map(function(item) {println("\t"+item);})
            println("kid.set[0]: "+kid.set[0]);
            kid.set.push(100);
        }

        // assign a ruby method to a JS variable
        var myfoobifier = kid.foobify

        // execute ruby method
        println(myfoobifier('yo', 9))
        println(kid['foobify'])
        println("send: ");
        // retrieve the ruby send method as an JS function
        println("\t"+kid['send']);

        // iterate through the 'properties' of a ruby object
        for ( prop in kid ) {
            println(prop)
            println("\t=> " + kid[prop])
        }
    ''')
```

### incognito usage:
```groovy
    import org.microsauce.incognito.Incognito

    def incognito = new Incognito()
    incognito.registerRuntime(new RhinoRuntime(rhinoGlobalScope))
    incognito.registerRuntime(new JRubyRuntime(scriptingContainer))
    incognito.registerRuntime(new GroovyRuntime())

    def rhino_proxy = incognito.assumeIdentity(Runtime.ID.RHINO, jruby_object)

    // pass the proxy into
    def ctx = Context.enter()
    try {
        rhinoGlobalScope.put('jruby_object', rhinoGlobalScope, rhino_proxy)
        ctx.evaluateString(rhinoGlobalScope, '''
           console.log("Hello " + jruby_object.name)
        ''', "scriptlet_0.js", 1, null)
    } finally {if (ctx) ctx.exit()}

```
