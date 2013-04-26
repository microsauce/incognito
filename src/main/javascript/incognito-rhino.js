
// TODO uncertain that I need a script to init rhino
// I plan to use ScriptableMap/List


(function() {

    function newRhinoExecutableProxy(metaObject, runtime) {
        var originRuntime = metaObject.originRuntime;
        return function(args) {
            return originRuntime.exec(
                metaObject,
                originRuntime.scope,
                prepareArguments(args, originRuntime, runtime));
        }
    }

    function prepareArguments(args, originRuntime, destRuntime) {
        return args.map(function(arg) {
            return destRuntime.proxy(originRuntime.wrap(arg));
        });
    }

}());

