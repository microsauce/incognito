import org.joda.time.DateTime

class TestObject {

    def object
    def string
    def integer
    def floatingPoint
    def date
    def hash
    def array
    def set
    def executable // an executable returning hash of values

    // Type.METHOD
    def method(arg,modifier) {
        modifier(arg)
    }
}

def proxyExercise = { values ->

    def obj = values['object']

    // value assertions - property access
    assert obj.string == 'a string'
    assert obj.integer == 7
    assert obj.floatingPoint == 3.14159

    assert obj.date.year == 1978
    assert obj.date.monthOfYear.get() == 5
    assert obj.date.dayOfMonth.get() == 8
    assert obj.date.hourOfDay.get() == 9
    assert obj.date.minuteOfHour.get() == 10

    assert

}

def initialValues() {

    def values = [:]

    // Type.PRIMITIVE
    values['string'] = 'a string'
    values['integer'] = 7
    values['floatingPoint'] = 3.14159

    // Type.DATE
    values['date'] = new DateTime(1978,5,8,9,10,500)

    // Type.EXECUTABLE
    values['executable'] = { arg ->
        arg
    }

    // Type.HASH
    values['hash'] = new HashMap(values)

    // Type.ARRAY
    values['array'] = new ArrayList(values.values())

    // Type.SET
    values['set'] = new HashSet(values['array'])

    // Type.OBJECT
    values['object'] = new TestObject(values)
    values['object'].object = values['object']

    values
}

proxyExercise(values)

//
// follow-up assertions
//

// TODO

