incognito
=========

Facilitate idiomatic interoperability of dynamic languages on the JVM.

Goals:
Develop a framework that enables objects created in supported JVM runtimes to enter other JVM runtimes 'incognito' -- assuming
an identity natural to the receiving runtime.  Define proxies/adaptors to enable idiomatic usage of objects defined in
each supported runtime in any of the other supported runtimes.  This support will include primitives (strings/integers/floating
point), executable types (functions/lambdas/etc), commonly used data structures (arrays, hashes, ~~sets~~), dates, and
method invocation.

Intended Use:
Polyglot frameworks.

Implementation strategy:
1. define adaptor interfaces to wrap target objects
2. define proxies in host RT's which forward method calls to adaptor (via 'methodMissing' or the like)
3. leverage existing host RT primitive type and Java collection interoperability (JRuby, Rhino, and Groovy are all
   excellent in this regard)

Implementation Notes:
- JRuby and Rhino hashes/arrays extend Java Map/List (RubyHash-RubyArray/NativeArray-NativeObject) - adaptor implementations
  will extend these types (via dynamic Proxy) overriding get/put/add methods ()

Initial target runtimes: JRuby, Rhino/RingoJS, Groovy
Potential target runtimes: Jython (collection interop not as complete as JRuby/Rhino)

Syntax:
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

Rhino Proxy Stack:

object: ScriptableMap => CustomMapImpl => ObjectAdaptor => target object/target object runtime
array:  ScriptableList => CustomListImpl => ArrayAdaptor => target array/target object runtime

Jruby Proxy Stack:

object: Ruby Proxy => ObjectAdaptor => target object
array:  CustomListImpl => ArrayAdaptor => target array
hash:   CustomMapImpl => MapAdaptor => target array

Groovy Proxy Stack:

object: Groovy Proxy => ObjectAdaptor => target object
array:  CustomListImpl => ArrayAdaptor => target array
hash:   CustomMapImpl => MapAdaptor => target array

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

Notes:

property access
method/function/closure invocation
basic collection support
    - arrays
    - hashes
basic type conversions
    - date
    - most other types are auto-converted (string, int, float, etc)


can I use a single adaptor for each of these types ???
object
    rhino - use a scriptablemap (ringojs)
    jruby - use a proxy class (method_missing)
    groovy - use a proxy class (methodMissing/propertyMissing)
    jython - use a proxy class (__getattr__)
hash
    ruby - extend java map
        - override all methods that manipulate the map (put, remove, etc)
        - in addition to calling the adaptor call the super method
    java - extend java map
        - ditto above
    jython - extend java map as above, extend dict to defer to the java map
array
    ruby, js, java - ditto above
executable

    can I use the same map / list implementation for each ??? yes (reference the ObjectAdaptor)

for each runtime
proxy object
proxy hash
proxy array
proxy list
proxy set