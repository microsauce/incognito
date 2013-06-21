
import static org.microsauce.incognito.Polly.*

def rhino_data = rhino( '''
    var counter = 0;
    var mydata = {
        name: 'Jimb0o0o0o0',
        address: {
            street: {
                number: 10,
                name: 'Minny',
                type: 'Street'
            },
            city: 'MadTown'
        },
        age: 6
    };
    mydata;
''', GROOVY)
println "rhino data: ${rhino_data}"

println "== round 1 =="
println "rhino_data.name: ${rhino_data.name}"
println "rhino_data.address.street.number: ${rhino_data.address.street.number}"
println "rhino_data.address.street.name: ${rhino_data.address.street.name}"
println "rhino_data.entrySet: ${rhino_data.entrySet()}"

for (i in 2..10) {
    rhino( '''
        counter += 2;
        mydata.address.street.number = counter;
        mydata.address.street.name = mydata.address.street.name + 'y';
    ''', GROOVY)

    println "\n== round $i =="
    println "rhino_data.name: ${rhino_data.name}"
    println "rhino_data.address.street.number: ${rhino_data.address.street.number}"
    println "rhino_data.address.street.name: ${rhino_data.address.street.name}"
    println "rhino_data.entrySet: ${rhino_data.entrySet()}"
}
