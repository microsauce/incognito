incognito
=========

Facilitating idiomatic interoperability of dynamic languages on the JVM.

### the problem
Many languages target the JVM.  Most of these languages have excellent interoperability with Java.  The problem is
(outside of their disparate runtime API's) they don't interoperate well with each other.  Incognito is an attempt to
solve this problem.

### overview
Incognito is a library that enables objects created in supported JVM runtimes to enter other JVM runtimes 'incognito'
-- assuming an identity native to that runtime.  The library defines adaptors to standardize access to objects in each
supported runtime.  It defines proxies to enable idiomatic usage of these objects in receiving runtimes. Support will
include primitives (strings/integers/floating point), objects, executable types (functions/lambdas/etc), common data structures
(arrays, hashes, sets), dates, and method/function/lambda/etc invocation.

### strategy
Leverage, as much as possible, existing support for Java interoperability (particularly with collections) in proxy
implementations.

### status and roadmap
Incognito is currently pre-alpha software.  Proposed roadmap:
* 0.1 - jruby, groovy, rhino, runtime adaptors and proxies
* 0.2 - jython
* 1.0 - performance optimizations / polishing
* 1.1 - guice style polyglot IOC

### proposed syntax:
```ruby
incognito = Incognito.new
incognito.registerRuntime(RhinoRuntime.new(scope))
incognito.registerRuntime(JRubyRuntime.new(scriptingContainer))

# . . .

js_array_proxy = incognito.assume_identity js_array, JRUBY
# -- and/or --
js_array_proxy = js_array.assume_identity JRUBY

js_array_proxy.each do |x|
    puts x
end
```
