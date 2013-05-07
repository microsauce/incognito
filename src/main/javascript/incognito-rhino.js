
importPackage(org.microsauce.incognito)
importPackage(java.util)

var incognitoPrepareArguments = function(args, originRuntime, destRuntime) {
    var pArgs = new ArrayList();
    for (var i = 0; i < args.length; i++) {
        pArgs.add(originRuntime.proxy(destRuntime.wrap(args[i])));
    }
    return pArgs;
}


var rhinoIncognito = {

    executableProxy : function(metaObject, runtime) {
        if ( metaObject.originRuntime.id.equals(Runtime.ID.RHINO) ) {
            return metaObject.targetObject;
        } else {
            var originRuntime = metaObject.originRuntime;
            return function() {
                return originRuntime.exec(
                    metaObject,
                    originRuntime.scope,
                    incognitoPrepareArguments(arguments, originRuntime, runtime));
            }
        }
    },

    dateProxy : function(metaObject) {
        if ( metaObject.runtime.id.equals(Runtime.RT.RHINO) ) {
            return metaObject.targetRaw
        } else {
            var cd = metaObject.targetObject
            return new Date(cd.year, cd.month, cd.dayOfMonth, cd.hour, cd.minute, cd.second, cd.millis);
        }
    },

    convertDate : function(date) {
        return new CommonDate(
            date.getYear(),
            date.getMonth()+1,
            date.getDate(),
            date.getHours()+1,
            date.getMinutes(),
            date.getSeconds(),
            date.getMilliseconds());
    },

}
