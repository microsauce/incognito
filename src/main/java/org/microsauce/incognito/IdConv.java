package org.microsauce.incognito;

/**
 * Created with IntelliJ IDEA.
 * User: jboone
 * Date: 6/5/13
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public enum IdConv {

    CAMEL_CASE("camelCase"),
    SNAKE_CASE("camel_case"),
    FAT_SNAKE_CASE("fat_snake");

    String name;

    IdConv(String name) {
        this.name = name;
    }
}
