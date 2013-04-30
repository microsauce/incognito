
/*
    define these in the global scope for now
*/

global.incongnitoRhinoExecutableProxy = function(metaObject, runtime) {
    var originRuntime = metaObject.originRuntime;
    return function(args) {
        return originRuntime.exec(
            metaObject,
            originRuntime.scope,
            incognitoPrepareArguments(args, originRuntime, runtime));
    }
}

global.incongnitoRhinoDate = function(millis) {
    var d = new Date();
    d.setMilliseconds(millis);
    return d;
}

global.incognitoConvertRhinoDate() = function(date) {
    return date.getMilliseconds();
}

global.incognitoPrepareArguments = function(args, originRuntime, destRuntime) {
    return args.map(function(arg) {
        return originRuntime.proxy(destRuntime.wrap(arg));
    });
}



