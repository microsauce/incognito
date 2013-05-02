import org.jruby.RubyObject
import org.mozilla.javascript.Context
import org.mozilla.javascript.ImporterTopLevel
import org.mozilla.javascript.ScriptableObject
import org.mozilla.javascript.NativeFunction

import org.jruby.embed.LocalContextScope
import org.jruby.embed.LocalVariableBehavior
import org.jruby.embed.ScriptingContainer
import org.microsauce.incognito.*
import org.microsauce.incognito.rhino.*
import org.microsauce.incognito.jruby.*

//
// begin rhino
//

org.mozilla.javascript.Context ctx = org.mozilla.javascript.Context.enter()
ScriptableObject jsContext = null
try {
    jsContext = new ImporterTopLevel(ctx)

    String applicationScript = '''

        var someObject = {
            name : 'Jimbo',
            age : 7.775,
            when : new Date(),
            dog : {
                name : 'foo fee'
            },
            foobify : function(a) {
                return 'foo-'+this.name+'-ify - ' + a.name;
            },
            array : ['one', 2, [1, 'two', {id:123}]]
        }

    '''
    ctx.evaluateString(jsContext, applicationScript, 'test.js', 1, null)
}
finally {
    ctx.exit()
}

def jsObject = jsContext.get('someObject', jsContext)
println "jsObject: ${jsObject}"

println "My jsObject:"
jsObject.each { key, value ->
    if ( value instanceof NativeFunction ) {
        ctx = org.mozilla.javascript.Context.enter()
        def retValue = value.call( ctx, jsContext, jsObject, 'a JS func argument' )
        ctx.exit()
        println "\t$key => $value - retValue: $retValue"
    } else
        println "\t$key => $value - type - ${value.getClass()}"
}

//
// begin JRuby
//

def container = new ScriptingContainer(LocalContextScope.SINGLETHREAD, LocalVariableBehavior.PERSISTENT);
def stream = new ByteArrayInputStream('''
    require 'date'
    require 'set'
    class Dog
        attr_accessor :name
        def initialize()
            @name = 'foo fee'
        end
    end
    class MyClass
        attr_accessor :name, :age, :whn, :dog, :foobify, :hashy, :array, :set, :l
        def initialize()
            @name = 'Jimbo'
            @age = 7.775
            @whn = DateTime.now
            @dog = Dog.new
            @foobify = Proc.new { |a|
                return "foo-#{@name}-ify #{a}"
            }
            @hashy = {:foo => 'bar'}
            @array = ['one', 2, 3.0]
            @set = ['a', 'b'].to_set
            @l = lambda {return "Freddy"}
        end

        def meth_with_args(arg1,arg2)
            return "#{arg1} is #{arg2} years old."
        end
    end
    my_instance = MyClass.new
'''.bytes)
container.runScriptlet(stream, "script.rb")
def rbObject = container.get('my_instance')
println "\nMy rbObject:"
['name', 'age', 'whn', 'dog', 'foobify', 'hashy', 'array', 'set', 'l'].each {
    def val = container.callMethod(rbObject, it, [] as Object[]);
    if ( val.getClass().equals(RubyObject) )
        println "\t$it => $val - type - ${val.getClass()} - ruby type - ${val.getType().getName()}"
    else
        println "\t$it => $val - type - ${val.getClass()}"
}
def val = container.callMethod(rbObject, 'meth_with_args', ['Jimmy', 8] as Object[])
println "\tmeth_with_args => ${val} - type - ${val.getClass()}"

//
// incognito - js -> jruby
//
println "initialize incognito . . ."
def incognito = new Incognito()
println "\tinit rhino . . ."
incognito.registerRuntime(new RhinoRuntime(null, jsContext))
println "\tinit jruby . . ."
incognito.registerRuntime(new JRubyRuntime(container, null))

println "create ruby proxy for js . . ."
def js_rubyProxy = incognito.assumeIdentity(Lang.RUBY, jsObject)

container.put('js_rubyProxy', js_rubyProxy)
container.runScriptlet(new ByteArrayInputStream('''
    puts "lets try the proxy in ruby . . ."
    class Kid
        attr_accessor :name
        def initialize(name)
            @name = name
        end
    end

    js_rubyProxy.array.each do |x|
        if x.respond_to?('each')
            x.each do |y|
                if y.kind_of? JRubyIncognito::JRubyObjectProxy
                    puts "@!@!@!!@!!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@!@ id: #{y.id}"
                else
                    puts "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ #{y}"
                end
            end
        else
            puts "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! #{x}"
        end
    end
    puts "#{js_rubyProxy.foobify(Kid.new('RubySteve'))}"
'''.bytes), "script.rb")

//
// incongnito jruby -> rhino
//
println "rb assume identity rhino . . ."
def rb_jsProxy = incognito.assumeIdentity(Lang.JAVASCRIPT, rbObject)
try {
    ctx = Context.enter()
    jsContext.put('rb_jsProxy', jsContext, rb_jsProxy)
    jsContext.put('out', jsContext, System.out)
    ctx.evaluateString(jsContext, '''
       out.println("list the rb array elements in js:");
       rb_jsProxy.array.map(function(x) {
          out.println(x);
       });
    ''', 'test.js', 1, null)
} finally {
    ctx.exit()
}

//
// proxies
//

//
// groovy
//

// use one of these two approaches for the groovy proxy:

// 1 )
class Stuff {
    def invokeMe() { "foo" }
}

def stuff = new Stuff()
stuff.metaClass.invokeMethod = { String name, args ->
    System.out.println "invoke: ${delegate.class} - name - $name - $args"
    delegate.class.getMethod(name, args.collect{it.class} as Class[]).invoke(delegate, args) + '-bity'
}
println "\nstuff.invokeMe: ${stuff.invokeMe()}"

// 2 )
class MyClass {
    def hello() { 'invoked hello directly' }
    def invokeMethod(String name, Object args){
        return "unknown method $name(${args.join(', ')})"
    }
}
def mine= new MyClass()
println mine.hello()
println mine.foo("Mark", 19)

//
// ruby
//
// define the proxy class in the init scripts

//
// rhino/ringojs
//
// use ScriptableMap/List

