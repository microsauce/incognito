
import static org.microsauce.incognito.Polly.*


def kid_rhinoProxy = jruby([arg1: 'Hello', arg2: 7], '''
  class Kid
    attr_accessor :name, :age

    def initialize(name, age)
      @name = name; @age = age
    end

    def foobify(prefix, num)
      return prefix + '- foo - ' + @name + '-ibity ' + num.to_s
    end
  end

  Kid.new(arg1,arg2)
''', RHINO)

def groovy_kid = groovy([:], '''
  class Kid {
    def name
    def age

    Kid(name,age) {
        this.name = name
        this.age = age
    }

    def foobify(prefix, num) {
      return prefix + '- foo - ' + name + '-ibity ' + num
    }
  }

  new Kid('Steve',9)
''', RHINO)

rhino([kid: groovy_kid], '''
  println("kid.name: " + kid.name);
  println("kid.age: " + kid.age);
  println("kid.foobify: " + kid.foobify('hey', 8));
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
