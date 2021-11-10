package com.uriegas.yai;

import java.util.*;
import static com.uriegas.yai.TokenType.*;

/**
 * Scanner for the YAIL language.<br>
 * It takes a string and returns a list of tokens.
 */
public class Scanner {
    private final String source; //The source code to evaluate
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("null",    NULL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    private int start = 0;
    private int current = 0;
    private int line = 1;

    /**
     * Constructor to load source code
     * @param source
     */
    Scanner(String source) {
        this.source = source;
    }

    List<Token> scan() {
        while(!isAtEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    /**
     * Checks if the current character is at the end of the source code
     * @return
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Scans a single token and adds it to the list of tokens as a Token type
     */
    public void scanToken(){
        char c = advance();
        switch (c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            // ==> Handle two character tokens
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            // <== Handle two character tokens
            // ==> Handle comments and division
            case '/':
                if(match('/')){
                    while(peek() != '\n' && !isAtEnd()){
                        advance();
                    }
                } else {
                    addToken(SLASH);
                }
                break;
            // <== Handle comments and division
            
            // ==> Handle white spaces
            case ' ': case '\r': case '\t': break;
            case '\n': line++; break; //Move to next line
            // <== Handle white spaces
            case '"': string(); break;
            default:
                if(isDigit(c))
                    number();
                else if(isAlpha(c))
                    identifier();
                else
                    Yai.error(line, "Unexpected character.");
        }
    }

    /**
     * Add an identifier token to the list of tokens
     */
    private void identifier() {
        while(isAlphaNumeric(peek()))
            advance();
        String text = source.substring(start, current); //Get the identifier text
        TokenType type = keywords.get(text); //Get the token type
        if(type != null)
            addToken(type);
        else
            addToken(IDENTIFIER);
    }

    /**
     * Returns if the given character is alphanumeric
     * @param c
     * @return true if the character is alphanumeric
     */
    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Returns if the given character is a alpha character
     * @param c
     * @return true if the character is a alpha character
     */
    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    /**
     * Returns if the given character is a digit
     * @param c
     * @return true if the given character is a digit
     */
    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Scans a number and adds it to the list of tokens
     */
    private void number() {
        while(isDigit(peek())) advance();
        if(peek() == '.' && isDigit(peekNext())){
            advance();
            while(isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Scan a string literal and adds it to the list of tokens
     */
    private void string() {
        while(peek() != '"' && !isAtEnd()){
            if(peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()){
            Yai.error(line, "Unterminated string.");
            return;
        }
        advance();//Close string
        addToken(STRING, source.substring(start + 1, current - 1));//Remove quotes and add to tokens
    }

    /**
     * Consume current character only if it matches the expected character
     * @param expected character
     * @return true if the character matches the expected character
     */
    private boolean match(char expected) {
        if(isAtEnd()) return false; // We reached end of source code
        if(source.charAt(current) != expected) return false; // The character doesn't match
        current++;
        return true;
    }

    /**
     * Consumes the current character and returns it
     * @return the next character
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Returns the next character without consuming it
     */
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * Returns the next character without consuming it
     */
    private char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * Adds a token to the list of tokens
     * @param type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Add a new {@link Token} to the list of tokens with the given type and 
     * @param type The {@link TokenType}
     * @param literal The literal value of the token
     */
    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
