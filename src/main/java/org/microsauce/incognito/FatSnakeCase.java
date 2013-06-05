package org.microsauce.incognito;

/**
 * Created with IntelliJ IDEA.
 * User: jboone
 * Date: 6/5/13
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class FatSnakeCase extends SnakeCase {
    public String enforce(String str) {
        return to_snake_case(str, true);
    }
}
