grammar Lang;

file                : block EOF;
block               : statement*;
blockWithBraces     : '{' block '}';
statement           : println
                    | function
                    | variable
                    | expr
                    | whileStatement
                    | ifStatement
                    | assignment
                    | returnStatement;

function            : Fun funName '(' parameterNames ')' blockWithBraces;
funName             : identifier;

variable            : Var varName ('=' expr)?;
varName             : identifier;

parameterNames      : (identifier (',' identifier)*)?;
whileStatement      : While '(' expr ')' blockWithBraces;
ifStatement         : If '(' expr ')' blockWithBraces (Else blockWithBraces)?;
assignment          : identifier '=' expr;
returnStatement     : Return expr?;

println             : Println '(' arguments ')';

functionCall        : funName '(' arguments ')';
arguments           : (expr (',' expr)*)?;
varLoad             : identifier;

constant returns [int value]
                    : n=number {$value = Integer.parseInt($n.text);}
                    | '-' n=number {$value = -Integer.parseInt($n.text);};

atom                : constant | functionCall | varLoad | '(' expr ')';
level0              : atom (op0 atom)*;
level1              : level0 (op1 level0)*;
level2              : level1 (op2 level1)*;
level3              : level2 (op3 level2)*;

expr                : level3;

number              : {_input.LT(1).getText().matches("0|[1-9]\\d*")}? AlnumToken;
identifier          : {_input.LT(1).getText().matches("[a-zA-Z][a-zA-Z0-9]*")}? AlnumToken;

Comment             : '//' ~[\n]* -> skip;
Whitespace          : (' ' | '\t' | '\r'| '\n') -> skip;

// keywords
Println             : 'println';
While               : 'while';
Var                 : 'var';
Fun                 : 'fun';
If                  : 'if';
Else                : 'else';
Return              : 'return';

// operations
op0                 : '*' | '/' | '%';
op1                 : '+' | '-';
op2                 : '>' | '<' | '>=' | '<=' | '==' | '!=';
op3                 : '||' | '&&';

fragment Letter     : [a-zA-Z];
fragment Digit      : [0-9] ;

AlnumToken          : (Digit | Letter)+;