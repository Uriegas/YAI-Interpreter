package com.uriegas.yai;

import java.util.*;
import static com.uriegas.yai.TokenType.*;

/**
 * Recursive Descendent Parser: top down parser.
 * Takes a list of tokens and applies the production rules to it. <br>
 * Terminal = a token.
 * Non-terminal = a production rule.
 * | = if statement.
 * * / + = while / for loop.
 * ? = if statement.
 */
public class Parser {
    private static class ParseError extends RuntimeException {}
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses the tokens and returns the root node.
     * @return a list of statements.
     */
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd())
            statements.add(declaration());
        return statements; 
    }
    // Expr parse() {
    //     try{
    //         return expression();
    //     }catch(ParseError e){
    //         return null;
    //     }
    // }

    // ==> Production Rules
    private Stmt declaration() { // declaration -> varDecl
        try {
            if (match(VAR))
                return varDeclaration();
            // } else if (match(FUN)) {
            //     return funDeclaration();
            // } else {
            //     return statement();
            // }
            return statement();
        } catch (ParseError e) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() { // varDecl -> "var" IDENTIFIER ("=" expression)? ";"
        Token name = consume(IDENTIFIER, "Expect variable name.");
        // consume(EQUAL, "Expect '=' after variable name.");
        Expr initializer = null;
        if(match(EQUAL)){
            initializer = expression();
        }
        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt statement() { // stmt -> printStmnt | ifStmt | block | exprStmnt
        if (match(PRINT)) return printStatement();
        if(match(IF)) return ifStatement();
        if (match(LEFT_BRACE)) return block();
        return expressionStatement();
    }
    
    private Stmt ifStatement() { // ifStmnt -> "if" "(" expression ")" stmt ("else" stmt)?
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE))
            elseBranch = statement();
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() { // printStmt -> "print" expression ";"
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt block() { // block -> "{" declaration* "}"
        List<Stmt> statements = new ArrayList<>();
        while (!check(RIGHT_BRACE) && !isAtEnd())
            statements.add(declaration());
        consume(RIGHT_BRACE, "Expect '}' after block.");
        return new Stmt.Block(statements);
    }

    private Stmt expressionStatement() { // exprStmnt -> expression ";"
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private Expr expression() { // expression -> assignment
        return assignment();
    }

    private Expr assignment() { // assignment -> IDENTIFIER "=" assignment | equality
        Expr expr = equality();
        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }
            error(equals, "Invalid assignment target.");
        }
        return expr;
    }
    
    private Expr equality() { // equality -> comparison ( ( "!=" | "==" ) comparison )*
        Expr expr = comparison();
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr comparison() { // comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*
        Expr expr = term();
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr term() { // term -> factor ( ( "+" | "-" ) factor )*
        Expr expr = factor();
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr factor() { // factor -> ( "/" | "*" ) factor | primary
        Expr expr = unary();
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    private Expr unary() { // unary -> ( "!" | "-" ) unary | primary
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() { // primary -> NUMBER | STRING | IDENTIFIER | "false" | "true" | "null" | "(" expression ")"
        if (match(NUMBER, STRING)) return new Expr.Literal(previous().literal);
        if (match(IDENTIFIER)) return new Expr.Variable(previous());
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NULL)) return new Expr.Literal(null);
        if (match(LEFT_PAREN)) {//"(" Expression ")"
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }
        throw error(peek(), "Expect expression.");
    }
    // <== Production Rules

    // ==> Helper Methods
    /**
     * Check if the next token is of the expected type and return it, otherwise throw an error.
     * @param type
     * @param message
     * @return the token.
     * @throws ParseError
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Yai.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                return;
            }

            advance();
        }
    }

    /**
     * Check if the current token is of the given type(s).
     * @param types
     * @return
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the current token is of the given type.
     * @param type
     * @return
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    /**
     * Check if the current token is of the given type and advance.
     * @param type
     * @return
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /**
     * Check if we are out of tokens.
     * @return true if we are at the end of the tokens.
     */
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    /**
     * Get the current token.
     * @return
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Get the previous token.
     * @return the previous token.
     */
    private Token previous() {
        return tokens.get(current - 1);
    }
    // <== Helper Methods
}
