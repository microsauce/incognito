
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
      return prefix + '- foo - ' + @name + '-ibity ' + num.to_s
    end
  end

  Kid.new(arg1,arg2, DateTime.now) do |name|
    puts "hey #{name} I'm a ruby callback"
    'ruby'
  end
''', RHINO, GROOVY)
println "groovy setup"

def groovy_kid = groovy('''
  import org.joda.time.DateTime
  class Kid {
    def name
    def age
    def dob
    def callback
    def set

    Kid(name,age,dob,callback, set) {
      this.name = name
      this.age = age
      this.dob = dob
      this.callback = callback
      this.set = set
    }

    def foobify(prefix, num) {
      return prefix + '- foo - ' + name + '-ibity ' + num
    }
  }
  def callback = { name ->
    println "hey $name I'm a groovy callback"
    return 'groovy'
  }
  new Kid('Steve',9, new DateTime(), callback, [1,2,3,4] as Set)
''', RHINO,GROOVY)
println "rhino playground"
[kid_proxies[RHINO], groovy_kid[RHINO]].each { kid ->
//[groovy_kid].each { kid ->
    println "\tkid $kid hitting the rhino playground"

    // TODO why is jruby  kid.age a list proxy
    rhino([kid: kid], '''
      println("kid.name: " + kid.name);
      println("kid.age: " + kid.age);
      println("kid.dob: " + kid.dob);
      println("kid.foobify: " + kid.foobify('hey', 8));
      println("kid.callback: " + kid.callback('Steve'));
      println("items:");
      if (kid.set===undefined) {}
      else {
        kid.set.map(function(item) {println("\t"+item);})
        println("kid.set[0]: "+kid.set[0]);
        kid.set.push(100);
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
println "groovy_kid post rhino: ${groovy_kid[GROOVY].set}"
println "groovy_kid.toSTring: ${groovy_kid[GROOVY].toString()}"

