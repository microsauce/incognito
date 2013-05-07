import static org.microsauce.incognito.Polly.*
import static org.microsauce.incognito.Runtime.ID.*

rhino [:], ''

def value = rhino([name: 'jimmy'], '''
    println('hello ' + name);
    var obj = {
        name: 'Jimmy',
        age: 7
    };
    obj;
''', GROOVY)
println "rhino000000 value: ${value.name} - ${value.age}"

def jrubyValue = jruby([name: 'jimmy'], '''
    puts "hey dare #{name} !!!"
    1+2
''')
println "jruby value: $jrubyValue"


rhino([name: 'jimmy'], '''
    println('hello ' + name);
''')

jruby([name: 'jimmy'], '''
    puts "hey dare #{name} !!!"
''')
rhino([name: 'jimmy'], '''
    println('hello ' + name);
''')

jruby([name: 'jimmy'], '''
    puts "hey dare #{name} !!!"
''')

groovy([age:7]) {
    println "groovy age  $age"
}

