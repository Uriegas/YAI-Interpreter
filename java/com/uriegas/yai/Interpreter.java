package com.uriegas.yai;

import java.util.*;
import static com.uriegas.yai.TokenType.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment environment = new Environment(); //Global environment

    void interpret(List<Stmt> statements) { 
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            Yai.runtimeError(error);
        }
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        System.out.println(stringify(evaluate(stmt.expression)));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.initializer != null)
            value = evaluate(stmt.initializer);

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }
        return null;
    }
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right); 
        switch (expr.operator.type) {
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;
                if (left instanceof String && right instanceof String) //Handle concatenation
                    return (String)left + (String)right;
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case MINUS:
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperand(expr.operator, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperand(expr.operator, right);
                return (double)left * (double)right;
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
        return null;
    }
    
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    /**
     * Evaluates if an expression is true or false
     * @param object
     * @return
     */
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }
    /**
     * Evaluates if two objects are equal
     * @param left
     * @param right
     * @return true if equal, false otherwise
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
    /**
     * Checks if an object is a number
     * @param operator
     * @param operand
     */
    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }
    /**
     * Checks if two objects are numbers
     * @param operator
     * @param left
     * @param right
     * @throw RuntimeError if not a number
     */
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    /**
     * Converts an object to a string
     * @param object
     * @return string representation of object
     */
    private String stringify(Object object) {
        if (object == null) return "null";
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0"))
                text = text.substring(0, text.length() - 2);
            return text;
        }
        return object.toString();
    }
}
