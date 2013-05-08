
import static org.microsauce.incognito.Polly.*


def kid_proxies = jruby([arg1: 'Hello', arg2: 7], '''
  require 'date'

  class Kid
    attr_accessor :name, :age, :dob, :callback

    def initialize(name, age, dob, &callback)
      @name = name; @age = age, @dob = dob, @callback = callback
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

def groovy_kid = groovy('''
  import org.joda.time.DateTime
  class Kid {
    def name
    def age
    def dob
    def callback

    Kid(name,age,dob,callback) {
        this.name = name
        this.age = age
        this.dob = dob
        this.callback = callback
    }

    def foobify(prefix, num) {
      return prefix + '- foo - ' + name + '-ibity ' + num
    }
  }
  def callback = { name ->
    println "hey $name I'm a groovy callback"
    return 'groovy'
  }
  new Kid('Steve',9, new DateTime(), callback)
''', RHINO)
println "kid_proxies: $kid_proxies - groovy_kid: $groovy_kid"
[kid_proxies[RHINO], groovy_kid].each { kid ->
    rhino([kid: kid], '''
      println("kid.name: " + kid.name);
      println("kid.age: " + kid.age);
      println("kid.dob: " + kid.dob);
      println("kid.foobify: " + kid.foobify('hey', 8));
      println("kid.callback: " + kid.callback('Steve'))
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
//  class Kid {
//      def name
//      def age
//  }
//
//
//
//  rhino([kid: new ScriptableMap(nativeRt(RHINO), [name: 'Steve', age: 7])], '''
//      for ( prop in kid ) {
//        println(prop + " => " + kid[prop])
//      }
//  ''')
