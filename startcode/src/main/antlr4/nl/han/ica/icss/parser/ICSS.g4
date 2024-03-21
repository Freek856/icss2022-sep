grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
//het hele bestand
stylesheet: (variableAssignment | stylerule)* EOF;
//Losse variabele boven aan
variableAssignment: variableReference ASSIGNMENT_OPERATOR expression+ SEMICOLON;
//methodes
stylerule: tagSelector OPEN_BRACE (declaration|ifClause|elseClause)+ CLOSE_BRACE;
tagSelector: ID_IDENT | CLASS_IDENT | LOWER_IDENT;
declaration: property COLON literal (operation literal)* SEMICOLON;
ifClause: IF BOX_BRACKET_OPEN variableReference BOX_BRACKET_CLOSE OPEN_BRACE (declaration|ifClause|elseClause)+ CLOSE_BRACE;
elseClause: ELSE OPEN_BRACE (declaration|ifClause|elseClause)+ CLOSE_BRACE;
operation: PLUS | MIN | MUL;
property: LOWER_IDENT;

//literals
boolLiteral: TRUE | FALSE;
colorLiteral: COLOR;
percentageLiteral: PERCENTAGE;
pixelLiteral: PIXELSIZE;
scalarLiteral: SCALAR;
variableReference: CAPITAL_IDENT;
literal: boolLiteral
    | colorLiteral
    | percentageLiteral
    | pixelLiteral
    | scalarLiteral
    | variableReference;

expression:
    literal
    | expression (MUL) expression
    | expression (PLUS | MIN ) expression;


