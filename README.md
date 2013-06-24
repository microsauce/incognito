incognito - 0.1
=========

Facilitate idiomatic interoperability of dynamic languages on the JVM.

### the problem
Many languages target the JVM.  Most of these languages have excellent interoperability with Java.  The problem is
(outside of their disparate runtime API's) they don't operate in a natural/idiomatic way with each other.  Incognito is
an attempt to address this problem.

### overview
Incognito is a library that enables objects defined in supported JVM languages/runtimes to assume the identity of objects
defined by other JVM languages.  For example, a Rhino object can assume the identity of a JRuby object (via a JRuby
proxy) and be utilized in the JRuby runtime as a native JRuby object.

### goals
Support interoperablity of objects, primitives (strings, integers, floating point, etc), and literal data-structure types
(arrays, sets, hashes, etc).  Provide support for basic object utilization: property access, method invocation, and duck-type
reflection (respond_to?, hasProperty, undefined, etc).  Support for executable/callback types (functions,
closures, lambdas, etc).

#### out of scope

* Equality of user defined objects.
* Comparison of user defined objects.

### strategy

Leverage, as much as possible, existing support for Java interoperability (particularly with collections and primitives) in proxy/adaptor
implementations.

### status and roadmap
Incognito is currently alpha software.  Proposed roadmap:
* 0.1 - jruby, groovy, rhino, runtime adaptors and proxies, Polly (Groovy)
* 0.2 - jython, Polly (Rhino, JRuby, Jython), unit/functional test suite
* 1.0 - performance optimizations / polishing
* 1.1 - guice-style polyglot IOC

## usage

Incognito provides a simple and intuitive API which can be called upon directly via the Incognito Java class or indirectly
via Polly (a polyglot script utility).

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

    //
    // instantiate incognito
    //
    def incognito = new Incognito()

    //
    // register language runtimes
    //
    incognito.registerRuntime(new RhinoRuntime(rhinoGlobalScope))
    incognito.registerRuntime(new JRubyRuntime(scriptingContainer))
    incognito.registerRuntime(new GroovyRuntime())

    //
    // create Rhino proxy for JRuby object
    //
    def rhino_proxy = incognito.assumeIdentity(Runtime.ID.RHINO, jruby_object)

    //
    // pass the proxy into Rhino
    //
    def ctx = Context.enter()
    try {
        rhinoGlobalScope.put('jruby_object', rhinoGlobalScope, rhino_proxy)
        ctx.evaluateString(rhinoGlobalScope, '''

           console.log("Hello " + jruby_object.name)

        ''', "scriptlet_0.js", 1, null)
    } finally {if (ctx) ctx.exit()}

```

=================================================================================
=================================================================================
Notes:
=================================================================================
=================================================================================

Test.[js|groovy|ruby]:

	define Test class:
		define TestSubject class /* each RT TestSubject impls must all define the same property names/methods */
		define beforeConstantsHash
		defind afterConstantsHash

		def constructor TestClass()
			instantiate testSubject
			instantiate beforeConstantsHash
			instantiate afterConstantsHash

		def addExercises([exercising])

		def run method:
			for each exercise
				def testObject = new TestSubject()
				thisExercise.run(testSubject, constants)
				assertions(testSubject, afterConstantsHash)

		define assertions(testSubject,constantsHash) /* all impls must make uniform assertions */
			. . .

		define exercise(testSubject) /* all impls must manipulate testSubjects in the same way */
			- assertions(testObject,beforeConstantsHash)

			- exercises:
				- object
					- duck-type reflection
						- respondsTo, hasProperty, undefined, etc.
					- properties (modify)
						- primitives
						- objects
						- callbacks
						- dates
					- methods / executables
						- parameters
							- primitives
							- objects
							- callbacks
							- dates
						- return
							- primitives
							- objects
							- callbacks
							- dates

			- assertions(testObject,afterConstantsHash) -- (reuse - same as setup script after assertions)

	new Test()

Polly Script:

	def testers = []
	testers << rhino(/*load script from cp*/, GROOVY)
	testers << jruby(/*load script from cp*/, GROOVY)
	testers << groovy(/*load script from cp*/)

	testers.each { test -> // TODO
		test.addExercises(testers)
		tester.run
	}


===================================================================================
===================================================================================
===================================================================================

setup class:

	- define setup class
		define beforeConstantsHash
		defind afterConstantsHash

		define 'exercise' method:
			for each exercise in exercises
				def testObject = new TestClass()
				thisExercise.run(testObject, constants)
				assertions(testObject, afterConstantsHash)

exercise class:

	- define exercise class
		- define 'run' method
			- assertions(testObject,beforeConstantsHash)

			- exercises:
				- object
					- duck-type reflection
						- respondsTo, hasProperty, undefined, etc.
					- properties (modify)
						- primitives
						- objects
						- callbacks
						- dates
					- methods / executables
						- parameters
							- primitives
							- objects
							- callbacks
							- dates
						- return
							- primitives
							- objects
							- callbacks
							- dates

			- assertions(testObject,afterConstantsHash) -- (reuse - same as setup script after assertions)

	- instantiate/return exercise object

