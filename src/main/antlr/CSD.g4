/*
 * CSD - Command Script Discriptor
 * _______________________________
 *
 * A simple script to define structres for the various syntaxes used by all
 * the discord bot commands.
 *
 */

/*
 * Code is **COPIED** from
 * https://github.com/antlr/grammars-v4/blob/master/json/JSON.g4
 * and is slightly edited to meet specific requirements.
 *
 */

/** Taken from "The Definitive ANTLR 4 Reference" by Terence Parr */

// Derived from http://json.org
grammar CSD;

@header {
    package in.mcxiv.antlr;
}



csd
   : obj
   ;



obj
   : '{' entry (',' entry)* '}'
   | '{' '}'
   ;

entry
   : word
   | pair
   ;

pair
   : word ':' type
   ;



arr
   : primitive_type ARRAY_IDENTIFIER+
   | obj ARRAY_IDENTIFIER+
   ;



type
   : primitive_type
   | complex_type
   ;

complex_type
   : obj
   | arr
   ;

primitive_type
   : 'int'
   | 'float'
   | 'boolean'
   | 'String'
   ;



word
   : VARIABLE_NAME
   ;



ARRAY_IDENTIFIER
   : '[]'
   ;

VARIABLE_NAME
   : VARIABLE_NAME_START VARIABLE_NAME_CONTENT*
   ;

fragment VARIABLE_NAME_START
   : [a-zA-Z_$]
   ;

fragment VARIABLE_NAME_CONTENT
   : [0-9a-zA-Z_$]
   ;



// \- since - means "range" inside [...]

WS
   : [ \t\n\r] + -> skip
   ;