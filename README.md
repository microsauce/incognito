incognito
=========

Facilitate idiomatic interoperability of dynamic languages on the JVM.

the problem
===========
Many languages target the JVM.  Most of these languages have excellent interoperability with Java.  The problem is
(outside of their disparate runtime API's) they don't interoperate well with each other.  Incognito is an attempt to
solve this problem.

overview
========
Incongnito is a framework that enables objects created in supported JVM runtimes to enter other JVM runtimes 'incognito'
-- assuming an identity natural to the receiving runtime.  The framework defines adaptors to standardize access to objects in each
supported runtime.  It defines proxies to enable idiomatic usage of these objects in receiving runtimes. Support will
include primitives (strings/integers/floating point), executable types (functions/lambdas/etc), commonly used data structures
(arrays, hashes, sets), dates, and method/function/lambda/etc invocation.

status and roadmap
==================
Incognito is currently in pre-alpha software.
* 0.1 - ruby, groovy, rhino | objects (property access, method invocation), primitives, built-in data structions (arrays,hashes,sets), dates, executable types (functions/closures/procs/lambdas)
* 0.2 - jython
* 0.3 - guice style polyglot IOC


Many of these features are already available for Java objects (and Groovy to some extent) inside JRuby and Rhino runtimes,
But the reverse isn't true, neither can JRuby utilize Rhino objects in an idiomatic way (and vice-versa).

proposed syntax:
```ruby
incognito = Incognito.new
incognito.registerRuntime(RhinoRuntime.new(scope))
incognito.registerRuntime(JRubyRuntime.new(scriptingContainer))

# . . .

js_array_proxy = incognito.assume_identity js_array, RUBY
# -- and/or --
js_array_proxy = js_array.assume_identity RUBY

js_array_proxy.each do |x|
    puts x
end
```
