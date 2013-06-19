
import static org.microsauce.incognito.Polly.*

def loadScript(uri) {
    this.getClass().getResource(uri).text
}

def testers = [
    rhino(loadScript('rhino/test.js'), GROOVY),
    jruby(loadScript('jruby/test.rb'), GROOVY),
    groovy(loadScript('groovy/Test.groovy'))
]

testers.each { test ->
    test.addExercises(testers)
    test.run()
}