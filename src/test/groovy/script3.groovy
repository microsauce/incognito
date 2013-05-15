
import static org.microsauce.incognito.Polly.*
println "ruby setup"
def kid_proxies = jruby([arg1: 'Hello', arg2: 7], '''
  require 'date'
  puts "jruby arg1: #{arg1} - arg2: #{arg2}"
  class Kid
    attr_accessor :name, :age, :dob, :callback

    def initialize(name, age, dob, &callback)
      @name = name; @age = age; @dob = dob; @callback = callback
    end

    def foobify(prefix, num)
      return prefix + '- foo - ' + @name + '-ibity ' + num.to_s + ' rubityyyyyyyyyyyyyyy'
    end
  end

  Kid.new(arg1,arg2, DateTime.now) do |name|
    puts "hey #{name} I'm a ruby callback"
    'ruby'
  end
''', RHINO, GROOVY)
println "kid_proxies: ${kid_proxies}"

println "groovy setup"

def groovy_kid = groovy('''
  import org.joda.time.DateTime
  class Kid {
    def name
    def age
    def dob
    def callback
    def sett
    def hash

    Kid(name,age,dob,callback, set) {
      this.name = name
      this.age = age
      this.dob = dob
      this.callback = callback
      this.sett = set
    }

    def foobify(prefix, num) {
      return prefix + '- foo - ' + name + '-ibity ' + num + ' groovityyyyyyyyyyyyyyyyyyyyy'
    }
  }
  def callback = { name ->
    println "hey $name I'm a groovy callback"
    return 'groovy'
  }
  new Kid('Steve',9, new DateTime(), callback, [1,2,3,4] as Set)
''', RHINO,GROOVY,JRUBY)
println "groovy_kid: ${groovy_kid}"

println "rhino playground"
[kid_proxies[RHINO], groovy_kid[RHINO]].each { kid ->
//[groovy_kid].each { kid ->
    println "\tkid $kid hitting the rhino playground"

    rhino([kid: kid], '''
      println("kid.name: " + kid.name);
      println("kid.age: " + kid.age);
      println("kid.dob: " + kid.dob);
      println("kid.foobify: " + kid.foobify('hey', 8));
      println("kid.callback: " + kid.callback('Steve'));
      println("items:");
      if (kid.sett===undefined) {}
      else {
        kid.sett.map(function(item) {println("\t"+item);})
        println("kid.sett[0]: "+kid.sett[0]);
        kid.sett.push(100);
      }
      var myfoobifier = kid.foobify

      println(myfoobifier('yo', 9))
      println(kid['foobify'])
      println("send: ");
      println("\t"+kid['send']);
      for ( prop in kid ) {
        println(prop)
        println("\t=> " + kid[prop])
      }
    ''')
}
println "groovy_kid post rhino: ${groovy_kid[GROOVY].sett}"
println "groovy_kid.toSTring: ${groovy_kid[GROOVY].toString()}"

println "groovy playground"

groovy([kid:kid_proxies[GROOVY]], '''
    println "in groovy . . . "
//    println "kid.resondsTo: ${kid.respondsTo('foobify')}"
    kid.respondsTo('foobify').each {
        print "${it}: "
        println "${it.invoke(kid, 'pre', 8)}"
        println "${kid."$it"('preee',9)}"
    }
''')

println "\nruby playground: ${groovy_kid[JRUBY]}"
jruby([kid: groovy_kid[JRUBY]], '''
    # puts "kid: #{kid.getClass()} . . ."
    puts "kid.respond_to?('foobify') => #{kid.respond_to?('foobify')}"
    puts "kid.respond_to?('barbify') ??? "
    puts "kid.respond_to?('barbify') => #{kid.respond_to?('barbify')}"
    puts "kid.foobify() => #{kid.foobify('proovity', 10)}"

    puts "dump the sett:"
    puts "set: #{kid.sett}"
    puts "set: #{kid.sett.class}"
    kid.sett.each { |i|
      puts "\t#{i}"
    }
    kid.sett << { "Jane Doe" => 10, "Jim Doe" => 6 }
    kid.hash = { "Jane Doe" => 10, "Jim Doe" => 6 }
''')

println "da set after jruby playground: " + groovy_kid[GROOVY].sett
println "how old is Jane Doe: " + groovy_kid[GROOVY].hash['Jane Doe']
