package com.uriegas.yai;

import java.util.*;

public class Environment {
    private final Map<String, Object> values = new HashMap<>();

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public Object get(Token name) {
        if(values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }
        throw new RuntimeException("Variable " + name.lexeme + " not found");
    }
}
