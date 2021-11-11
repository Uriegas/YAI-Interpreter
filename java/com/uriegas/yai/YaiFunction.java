package com.uriegas.yai;

import java.util.*;

public class YaiFunction implements YaiCallable {
    private final Stmt.Function declaration;

    public YaiFunction(Stmt.Function declaration) {
        this.declaration = declaration;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        // List<Stmt> body = declaration.body;
        Environment environment = new Environment();

        for (int i = 0; i < declaration.params.size(); i++) {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        // try {
            interpreter.executeBlock(declaration.body, environment);
        // } catch (Return returnValue) {
        //     return returnValue.value;
        // }

        return null;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public String toString() {
        return "<function " + declaration.name.lexeme + ">";
    }
}
