grammar APiAPI;

// Words reserved by definition
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

// Ponctuators
OPERATORS
    : '{'
    | '}'
    | ':'
    ;

// Comment missing last #
WRONG_COMMENT
    : '#' ( ~( '#' | '#' ) )* '\n'
    ;

// Correct comment
COMMENT
    : ('#' ( ~( '#' | '#' | '\n' ) )* '#' '\n') -> skip
    ;

// Generic identificator
IDENT
    : ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

// Path identificator
PATH_IDENT
    : ('a'..'z'|'A'..'Z'|'_'|'/') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'/')*
    ;

// Any whitespace
WHITE
    : (' '
    | '\t') -> skip
    ;

// New line
BREAK_LINE
    : '\n'
    ;

// The rest
WRONG_SYMBOL
    : .
    ;

// The programs structure
main
    :   'Models' '{' '\n' 
            (model)+
        '}' ('\n')*
        'Routes' '{' '\n'
            (routes)+ 
        '}'
    ;

// The model structure
model 
    :    IDENT '{' '\n' fields '}' '\n'
    ;

// A model has at least one field
fields
    :   (field)+
    ;

// The field structure
field
    :   IDENT ':' type  '\n'
    ;

// Three supported types or user defined type
type
    :   IDENT
    |   'int'
    |   'string'
    |   'float'
    ;

// The routes structure
routes
    :   'Models.' IDENT '{' '\n'
             (route)+
         '}' '\n'
    ;

// A route structure
route
    :    IDENT '{' '\n'
            routeSpecs
         '}' '\n'
    ;

// Route parameters definition
routeSpecs
    :   'Method' ':' method '\n' 
        'Path' ':' path '\n'
    ;

// A route path
path
    :  PATH_IDENT (param)?
    ;

// Path parameter
param
    :   ':' IDENT
    ;

// HTTP Methods
method
    :   'GET'
    |   'POST'
    |   'PUT'
    |   'DELETE'
    ;