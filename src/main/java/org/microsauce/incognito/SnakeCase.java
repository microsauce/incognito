package org.microsauce.incognito;

/**
 * Created with IntelliJ IDEA.
 * User: jboone
 * Date: 6/5/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class SnakeCase implements IdentifierConvention {

    @Override
    public String enforce(String str) {
        return to_snake_case(str);
    }

    private String to_snake_case(String str) {
        return to_snake_case(str, false);
    }

    protected String to_snake_case(String str, boolean capital) {
        StringBuilder buffer = new StringBuilder();
        int strLength = str.length();
        for (int i = 0; i < strLength; i++) {
            char thisChar = str.charAt(i);
            if (Character.isUpperCase(thisChar))
                buffer.append('_');

            buffer.append(capital ? Character.toUpperCase(thisChar) : Character.toLowerCase(thisChar));
        }
        return buffer.toString();
    }

}
