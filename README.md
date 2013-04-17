incognito
=========

jruby, rhino, jython, groovy

property access
invocation
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

