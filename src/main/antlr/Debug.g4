grammar Debug;

command             : load
                    | breakpoint
                    | condition
                    | list
                    | remove
                    | run
                    | evaluate
                    | cmdStop
                    | cmdContinue;

load                : 'load' filename;
breakpoint          : 'breakpoint' line;
condition           : 'condition' line expr;
list                : 'list';
remove              : 'remove' line;
run                 : 'run';
evaluate            : 'evaluate' expr;
cmdStop             : 'stop';
cmdContinue         : 'continue';

filename            : ANY;
expr                : ANY+;
line                : ANY;

Whitespace          : (' ' | '\t' | '\r' | '\n') -> skip;
ANY                 : ~(' ' | '\t' | '\r' | '\n')+;
