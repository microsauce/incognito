package org.microsauce.incognito;

/**
 * Created with IntelliJ IDEA.
 * Date: 5/15/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public enum IdentifierConvention {
    CAMEL_CASE("camelCase"), SNAKE_CASE("snake_case");

    private String name;

    IdentifierConvention(String name) {
        this.name = name;
    }

    public String getName() {
        return name();
    }
}
