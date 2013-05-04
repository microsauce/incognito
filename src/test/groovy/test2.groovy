import static org.microsauce.incognito.test.Polly.groovy
import static org.microsauce.incognito.test.Polly.jruby
import static org.microsauce.incognito.test.Polly.rhino

rhino [:], ''

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
rhino([name: 'jimmy'], '''
    println('hello ' + name);
''')

jruby([name: 'jimmy'], '''
    puts "hey dare #{name} !!!"
''')

groovy([age:7]) {
    println "groovy age  $age"
}

rhino [foo:'bar'], {'''
    println('foo');
'''}