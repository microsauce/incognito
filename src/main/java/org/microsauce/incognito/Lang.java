package org.microsauce.incognito;

public enum Lang {
    JAVA("java"), JAVASCRIPT("javascript"), RUBY("ruby"), PYTHON("python");
    
    String name;
    
    Lang(String name) {
        this.name = name;
    }

    String getName() {return name;}
}
