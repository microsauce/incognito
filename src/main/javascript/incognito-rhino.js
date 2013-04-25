
function newProxyObject(adaptor) {
    return new ScriptableMap(new RhinoAdaptorBackedMap(adaptor));
}

function newProxyArray(adaptor) {
    return new ScriptableList(new RhinoAdaptorBackedList(adaptor));
}