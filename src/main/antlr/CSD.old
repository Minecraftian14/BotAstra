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



arr
   : primitive_type '[' ']'
   | arr '[' ']'
   ;



obj
   : '{' pair (',' pair)* '}'
   | '{' '}'
   ;

pair
   : VARIABLE_NAME ':' type
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