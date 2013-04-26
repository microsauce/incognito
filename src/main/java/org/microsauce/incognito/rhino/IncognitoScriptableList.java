package org.microsauce.incognito.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.ringojs.util.ScriptUtils;
import org.ringojs.wrappers.ScriptableList;

import java.util.Collection;
import java.util.List;

// TODO remove

public class IncognitoScriptableList extends ScriptableList {

    private Collection originCollection;

    public IncognitoScriptableList(Scriptable scope, List list) {
        super(scope, list);
    }

    public void setOriginCollection(Collection originCollection) {
        this.originCollection = originCollection;
    }

    public void put(int index, Scriptable start, Object value) {
        if (getList() != null) {
            try {
                if (index == getList().size()) {
                    Object jValue = ScriptUtils.jsToJava(value);
                    getList().add(jValue);
                    if ( this.originCollection != null ) {
                        this.originCollection.add(jValue);
                    }
                } else {
                    getList().set(index, ScriptUtils.jsToJava(value));
                }
            } catch (RuntimeException e) {
                Context.throwAsScriptRuntimeEx(e);
            }
        } else {
            super.put(index, start, value);
        }
    }

}
