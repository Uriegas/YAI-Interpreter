package com.uriegas.yai;

/**
 * Representation of a Token
 */
public class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line; 

    /**
     * Constructor
     * @param type TokenType
     * @param lexeme String
     * @param literal Object
     * @param line int
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
