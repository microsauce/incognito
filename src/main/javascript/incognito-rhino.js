
importPackage(org.microsauce.incognito)

var rhinoIncognito = {

    executableProxy : function(metaObject, runtime) {
        if ( metaObject.runtime.id.equals(Runtime.ID.RHINO) ) {
            return metaObject.targetObject;
        } else {
            var originRuntime = metaObject.originRuntime;
            return function(args) {
                return originRuntime.exec(
                    metaObject,
                    originRuntime.scope,
                    incognitoPrepareArguments(args, originRuntime, runtime));
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

    prepareArguments : function(args, originRuntime, destRuntime) {
        return args.map(function(arg) {
            return originRuntime.proxy(destRuntime.wrap(arg));
        });
    }
}
