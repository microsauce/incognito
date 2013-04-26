
// TODO uncertain that I need a script to init rhino
// I plan to use ScriptableMap/List

function newRhinoProxy(adaptor) {
    return new ScriptableMap(new RhinoAdaptorBackedMap(adaptor));
}
