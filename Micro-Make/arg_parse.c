/* CSCI 347 arg_parse.c
 * Assignment2 Fall 2017
 *
 * 16 OCT 2017, Student: Preston Carroll
 *
 * purpose: parse a line and its arguments
 */


 #include <stdio.h>
 #include <stdlib.h>
 #include <unistd.h>
 #include <sys/wait.h>
 #include <ctype.h>
 #include "arg_parse.h"
 #include "target.h"

 /* PROTOTYPES */


#define BETWEEN 0
#define ARGUMENT 1

/* Delta[state][class] = new_state
 * space = 0;
 * !space = 1;
 */
 const int Delta[2][2] = {
     {BETWEEN, ARGUMENT}, /*BETWEEN */
     {BETWEEN, ARGUMENT}, /*ARGUMENT */
 };

 const int AtStart[2][2] = {
     {0, 1},
     {0, 0},
 };

 const int AtEnd[2][2] = {
     {0, 0},
     {1, 0},
 };

 /* Arg Count
  * Returns the number of space seperated arguments in line.
  */
  static int arg_count (char* line) {
      int count = 0;;
      int state = BETWEEN;

      while(*line != '\0') {
          int class = !isspace(*line);
          count += AtStart[state][class];
          state = Delta[state][class];
          ++line;
      }
      return count;
  }

  /* Arg Parse Aux
   *
   * Actually creates the null-terminated array of argument pointers. Modifies
   * the string line by inserting nulls at the end of arguments.
   */
  static char** arg_parse_aux(char* line, int count) {
      char** args = malloc((count+1)*sizeof(char*));
      int argc = 0;
      int state = BETWEEN;
      args[argc] = NULL;

      while(*line != '\0') {
          int class = !isspace(*line);

          if(AtStart[state][class]) {
              args[argc] = line;
              ++argc;
              args[argc] = NULL;
          }

          if(AtEnd[state][class])
              *line = '\0';

          state = Delta[state][class];
          ++line;
      }
      return args;
  }

  /* Arg Parse\
   * All of the real work is done above in arg_count and arg_parse_aux.
   */
   char** arg_parse(char* line, int* argcp) {
       *argcp = arg_count(line);
       return arg_parse_aux(line, arg_count(line));
   }





//
