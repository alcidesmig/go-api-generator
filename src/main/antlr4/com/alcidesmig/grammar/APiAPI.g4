grammar APiAPI;

RESERVED
    : 'Models'
    | 'int'
    | 'string'
    | 'float'
    | 'Routes'
    | 'GET'
    | 'POST'
    | 'PUT'
    | 'DELETE'
    | 'Method'
    | 'Path'
    ;

OPERATORS
    : '{'
    | '}'
    | ':'
    ;

WRONG_COMMENT
    : '#' ( ~( '#' | '#' ) )* '\n'
    ;

COMMENT
    : ('#' ( ~( '#' | '#' | '\n' ) )* '#' '\n') -> skip
    ;

IDENT
    : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

PATH_IDENT
    : ('a'..'z'|'A'..'Z'|'_'|'/') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')*
    ;

WHITE
    : (' '
    | '\t') -> skip
    ;

BREAK_LINE
    : '\n'
    ;

WRONG_SYMBOL
    : .
    ;

main
    :   'Models' '{' '\n' 
            (model)+
        '}' ('\n')*
        'Routes' '{' '\n'
            (routes)+ 
        '}'
    ;


model 
    :    IDENT '{' '\n' fields '}' '\n'
    ;

fields
    :   (field)+
    ;

field
    :   IDENT ':' type  '\n'
    ;

type
    :   IDENT
    |   'int'
    |   'string'
    |   'float'
    ;


routes
    :   'Models.' IDENT '{' '\n'
             (route)+
         '}' '\n'
    ;

route
    :    IDENT '{' '\n'
            routeSpecs
         '}' '\n'
    ;

routeSpecs
    :   'Method' ':' method '\n' 
        'Path' ':' path '\n'
    ;

path
    :  PATH_IDENT (param)?
    ;

param
    :   ':' IDENT
    ;

method
    :   'GET'
    |   'POST'
    |   'PUT'
    |   'DELETE'
    ;