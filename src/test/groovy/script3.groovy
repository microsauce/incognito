import org.microsauce.incognito.rhino.ScriptableMapProxy
import org.ringojs.wrappers.ScriptableMap

import static org.microsauce.incognito.Polly.*
  import static org.microsauce.incognito.Runtime.ID.*



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

  rhino([kid: kid_rhinoProxy], '''
      println("kid.name: " + kid.name);
      println("kid.age: " + kid.age);
      println("kid.foobify: " + kid.foobify('hey', 8));
      var myfoobifier = kid.foobify

      println(myfoobifier('yo', 9))
//      for ( prop in kid ) {
//        println(prop)
//        println("\t=> " + kid[prop])
//      }
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
