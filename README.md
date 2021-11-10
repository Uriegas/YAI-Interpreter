# YAI-Interpreter
A fork from the incredible [Lox interpreter](https://github.com/munificent/craftinginterpreters/tree/master/java/com/craftinginterpreters/lox) by [Bob Nystrom](https://github.com/munificent).  

## Grammar  
The following is the grammar for the YAI interpreter in Extended Backus-Naur form ([EBNF](https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_form)) notation:  
```md
#Declarations
program     = declaration* EOF
declaration = funcDecl | varDecl | statement
funcDecl    = "def" function
varDecl     = "yai" ID ("=" expression)? ";"

#Statements
statement   = exprStmnt | forStmnt | ifStmnt | returnStmnt | whileStmnt | block
exprStmnt   = expression ";"
forStmnt    = "for" "(" (varDecl | exprStmnt | ";")
                        expression? ";"
                        expression? ")"
                    statement
ifStmnt     = "if" "(" expression ")" statement
returnStmnt = "return" expression?
whileStmnt  = "while" "(" expression ")" statement
block       = "{" declaration* "}"

#Expressions
expression  = assignment
assignment  = ( call "." )? IDENTIFIER "=" assignment | logic_or
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
