package com.uriegas.yai;

import java.util.*;

public class Environment {
    private final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        this.enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if(values.containsKey(name.lexeme))
            return values.get(name.lexeme);
        if(enclosing != null)
            return enclosing.get(name);
        throw new RuntimeException("Variable " + name.lexeme + " not found");
    }

    public void assign(Token name, Object value) {
        if(values.containsKey(name.lexeme))
            values.put(name.lexeme, value);
        else //Variable not found
            throw new RuntimeException("Variable " + name.lexeme + " not found");
    }
}
