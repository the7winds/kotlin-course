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

function            : Fun funName '(' parameterNames? ')' blockWithBraces;
funName             : Identifier;

variable            : Var varName ('=' expr)?;
varName             : Identifier;

parameterNames      : Identifier (',' Identifier)*;
whileStatement      : While '(' expr ')' blockWithBraces;
ifStatement          : If '(' expr ')' blockWithBraces (Else blockWithBraces)?;
assignment          : Identifier '=' expr;
returnStatement     : Return expr?;

println             : Println '(' arguments? ')';

functionCall        : funName '(' arguments? ')';
arguments           : expr (',' expr)*;
varLoad             : Identifier;

atom                : constant | functionCall | varLoad | '(' expr ')';
level0              : atom (Op0 atom)*;
level1              : level0 (Op1 level0)*;
level2              : level1 (Op2 level1)*;
level3              : level2 (Op3 level2)*;

expr                : level3;

constant returns [int value]
                    : n=Literal {$value = Integer.parseInt($n.text);};


// keywords
Println             : 'println';
While               : 'while';
Var                 : 'var';
Fun                 : 'fun';
If                  : 'if';
Else                : 'else';
Return              : 'return';


fragment Letter     : [a-zA-Z];
fragment Digit      : [0-9] ;

Identifier          : Letter (Letter | Digit)*;
Literal             : '-'? Digit+;
Whitespace          : (' ' | '\t' | '\r'| '\n') -> skip;


Op0                 : '*' | '/' | '%';
Op1                 : '+' | '-';
Op2                 : '>' | '<' | '>=' | '<=' | '==' | '!=';
Op3                 : '||' | '&&';