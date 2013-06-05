/*

 */

class Test {

    static constants = [:]

    static stringModifier = { str ->
        str + '-mod'
    }

    static numberModifier = { num ->
        num / 2
    }

    static dateModifer = { date ->
        // TODO add one year, month, day, hour, minute, and second
        date
    }

    static hashModifier = { hash ->
        // TODO add, remove, get, size
    }

    static setModifier = { set ->
        // TODO
    }

    static arrayModifier = { array ->

    }

    class TestSubject {

        def object //
        def string //
        def integer // mod
        def floatingPoint // mod
        def date // mod - via date arithmetic
        def hash // add, remove, size, get, mod entry value
        def array  // append, prepend, remove, size
        def set // add, remove, size, contains -- cannot support this for custom types
        def executable = {arg, modifier ->
            modifier(arg)
        }// an executable returning hash of values

        // Type.METHOD
        def method(arg,modifier) {
            modifier(arg)
        }

    }

    Test() {
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
    }

    def execute() {

    }

}
