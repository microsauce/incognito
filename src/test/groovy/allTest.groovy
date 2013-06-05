/*
static constants = [:]

//
// before/after values for properties/executable products
//

constants['INIT_STRING']    = 'init string'
constants['MOD_STRING']     = 'mod string'
constants['INIT_INT']       = 7
constants['MOD_INT']        = 8
constants['INIT_FLOAT']     = 3.14159
constants['MOD_FLOAT']      = 2.71828

constants['INIT_YEAR']      = 1978
constants['MOD_YEAR']       = 1980
constants['INIT_MOY']       = 5
constants['MOD_MOY']        = 9
constants['INIT_DOM']       = 8
constants['MOD_DOM']        = 1
constants['INIT_HOD']       = 9
constants['MOD_HOD']        = 10
constants['INIT_MOH']       = 11
constants['MOD_MOH']        = 12
constants['INIT_SOM']       = 13
constants['MOD_SOM']        = 14

constants['INIT_C_SIZE']    = 5
constants['MOD_C_SIZE']     = 4
constants['NEW_MEMBER']     = 'new Member'
constants['REMOVED_MEMBER'] = 'removed Member'
*/

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
    test.run
}