package org.microsauce.incognito;

/**
 * Created with IntelliJ IDEA.
 * User: jboone
 * Date: 6/5/13
 * Time: 3:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CamelCase implements IdentifierConvention {
    @Override
    public String enforce(String str) {
        return toCamelCase(str);
    }

    private String toCamelCase(String str) {
        StringBuilder buffer = new StringBuilder();
        int strLength = str.length();
        boolean nextCapital = false;
        for (int i = 0; i < strLength; i++) {
            char thisChar = str.charAt(i);
            if ( thisChar == '_' ) nextCapital = true;
            else {
                buffer.append(nextCapital ? Character.toUpperCase(thisChar) : thisChar);
                nextCapital = false;
            }
        }
        return buffer.toString();
    }

}
