/* CSCI 347 arg_parse.h
 * Assignment2 Fall 2017
 *
 * 16 OCT 2017, Student: Preston Carroll
 *
 * purpose: declare the prototypes for arg_parse
 */

 /* PROTOTYPES */

 /* Arg Parse
  * Returns a null-terminated array of strings (char*s). The strings are the
  * space separated arguments in line. Note that this function modifies line.
  */
  char** arg_parse(char* line, int *argcp);
