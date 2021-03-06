package com.uriegas.yai;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;

public class Yai{
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    /**
     * Main entry point.
     * @param args Command line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length > 1 ){
            System.out.println("Usage: yai [script]");
            System.exit(1);
        }
        else if(args.length == 1){
            runFile(args[0]);
        }
        else{
            runPrompt();
        }
    }

    /**
     * Execute a file.
     * @param path
     * @throws IOException
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(1);
    }
    /**
     * Execute the REPL.
     * @throws IOException
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        System.out.println("==YET ANOTHER INTERPTER==");
        String yai;
        yai =
"**      **     **      **" + "  Usage:" + "\n" +
" //**  **     ****    /**" + "\t var x = 10;" + "\n" +
"  //****     **//**   /**" + "\t for(var i = 0; i < 10; i = i+1){print (i+x);}" + "\n" +
"   //**     **  //**  /**" + "\t if(nill == nill) print \"OMG\" + \"!!\";" + "\n" +
"    /**    ********** /**" + "\t def add(x, y){return x + y;}" + "\n" +
"    /**   /**//////** /**" + "\t print add(x, 3);" + "\n" +
"    /**  /**      /***/**" + "\t print \"Pura UPV Compa\";" + "\n" +
"    //   //      ///  // ";
        System.out.println(yai);
        while(true) { 
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }
    /**
     * Execute a line of code.
     * @param source
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scan();
        Parser parser = new Parser(tokens);
        // Expr expression = parser.parse();
        List<Stmt> statements = parser.parse();
        if (hadError) return;
        // System.out.println(new ASTPrinter().print(expression));
        interpreter.interpret(statements);
    }

    /**
     * Reports an error.
     * @param line
     * @param message
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /**
     * Reports an error with token information.
     * @param token
     * @param message
     */
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF)
            report(token.line, " at end", message);
        else
            report(token.line, " at '" + token.lexeme + "'", message);
    }

    /**
     * Reports an runtime error.
     * @param error
     */
    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

    /**
     * Print an error message.
     * @param line
     * @param where
     * @param message
     */
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
