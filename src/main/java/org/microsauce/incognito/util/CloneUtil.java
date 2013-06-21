package org.microsauce.incognito.util;

import org.microsauce.incognito.MetaObject;
import org.microsauce.incognito.Type;
import org.microsauce.incognito.groovy.GroovyProxy;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: microsauce
 * Date: 6/21/13
 * Time: 3:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloneUtil {

    public static Object doClone(MetaObject obj) {
System.out.println("\t CloneUtil.doClone !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
System.out.println("\t CloneUtil.doClone !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        if ( obj.getType() == Type.PRIMITIVE || obj.getType() == Type.DATE )
            return obj.getTargetObject();
        else if ( obj.getType() == Type.OBJECT )
            return new GroovyProxy(obj, obj.getOriginRuntime()).clone();
        else if ( obj.getType() == Type.ARRAY || obj.getType() == Type.SET ) {
            List array = new ArrayList();
            for ( Object element : (List) obj.getTargetObject() ) {
                array.add(doClone((MetaObject) element));
            }
            return array;
        }
        else if ( obj.getType() == Type.HASH ) {
            Map hash = new HashMap();
            for ( Map.Entry entry : (Set<Map.Entry>)((Map) obj.getTargetObject()).entrySet() ) {
                hash.put(entry.getKey(), doClone((MetaObject)entry.getValue()));
            }
            return hash;
        }
        return null;
    }

}
