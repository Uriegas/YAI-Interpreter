[![Run on Repl.it](https://repl.it/badge/github/Uriegas/YAI-Interpreter)](https://repl.it/@Uriegas/YAI-Interpreter)
YAI-Interpreter
==================

A fork from the incredible [Lox interpreter](https://github.com/munificent/craftinginterpreters/tree/master/java/com/craftinginterpreters/lox) by [Bob Nystrom](https://github.com/munificent).  

## Turing Complete  
Because this programming language implements the basic parts of a complete Turing machine:
* arithmetic
* control flow
* memory allocation

---
**NOTE**

For control flow a programming language only needs while loops and if statements, even though other kind of loops are commonly implemented

---

## Grammar  
The following is the grammar for the YAI interpreter in Extended Backus-Naur form ([EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form)) notation:  
```md
#Declarations
program     = declaration* EOF
declaration = funcDecl | varDecl | statement
funcDecl    = "def" function
varDecl     = "var" IDENTIFIER ("=" expression)? ";"

#Statements
statement   = printStmnt | exprStmnt | forStmnt | ifStmnt | returnStmnt | whileStmnt | block
printStmnt  = "print" expression ";"
exprStmnt   = expression ";"
forStmnt    = "for" "(" (varDecl | exprStmnt | ";")
                        expression? ";"
                        expression? ")"
                    statement
ifStmnt     = "if" "(" expression ")" statement
returnStmnt = "return" expression? ";"
whileStmnt  = "while" "(" expression ")" statement
block       = "{" declaration* "}"

#Expressions
expression  = assignment
assignment  = IDENTIFIER "=" assignment | logic_or
logic_or    = logic_and ( "or" logic_and )*
logic_and   = equality ( "and" equality )*
equality    = comparison ( ( "!=" | "==" ) comparison )*
comparison  = term ( ( ">" | ">=" | "<" | "<=" ) term )*
term        = factor ( ( "-" | "+" ) factor )*
factor      = unary ( ( "/" | "*" ) unary )*
unary       = ( "!" | "-" ) unary | call
call        = primary ( "(" arguments? ")" )*
primary     = "true" | "false" | "null" | NUMBER | STRING | IDENTIFIER | "(" expression ")"

#Functions
function    = IDENTIFIER "(" parameters? ")" block
parameters  = IDENTIFIER ( "," IDENTIFIER )*
arguments   = expression ( "," expression )*

#Terminals
NUMBER      = DIGIT+ ( "." DIGIT+ )?
STRING      = "\"" CHAR* "\""
IDENTIFIER  = ALPHA ( ALPHA | DIGIT )*
ALPHA       = "a" ... "z" | "A" ... "Z" | "_"
DIGIT       = "0" ... "9"
CHAR        = UNICODE chars except "\""
```

## TODO  
Problem with recursive function call stack