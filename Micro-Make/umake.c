/* CSCI 347 micro-make
* Assignment1 Fall 2017
*
* 09 AUG 2017, Professor: Aran Clauson - original author
* 22 OCT 2017, Student: Preston Carroll - finished version author
*
* purpose: parse a line and its arguments then execute that line if possible
*/


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <ctype.h>
#include <string.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <sys/types.h>
#include "arg_parse.h"
#include "target.h"

/* CONSTANTS */

/* PROTOTYPES */

/* Process Line
* line   The command line to execute.
* This function interprets line as a command line.  It creates a new child
* process to execute the line and waits for that process to complete.
*/
void processline(char* line);
void processTarget(const char* target);
void processDeps(const char* target);
int proc_targ_check(const char* target);
int expand(char* orig, char* new, int newsize);
int brace_match(char* orig, int i);
char* get_variable_name();
void do_line_logic(char* line, char** name, int* add_Targ_Flag);
void io_redirect(char** args, int argc, int* old_io);

alist* targlist = NULL;
strList* depList = NULL;
strList* ruleList = NULL;

/* Main entry point.
* argc    A count of command-line arguments
* argv    The command-line argument valus
*
* Micro-make (umake) reads from the uMakefile in the current working
* directory.  The file is read one line at a time.  Lines with a leading tab
* character ('\t') are interpreted as a command and passed to processline minus
* the leading tab.
*
* put all the targets, dependencies, and rules into linked lists and store
* them accordingly. Then process each target that was supplied to the command
* line
*/
int main(int argc, const char* argv[]) {

    FILE* makefile = fopen("./uMakefile", "r");
    if(makefile == NULL)
        return EXIT_FAILURE;

    size_t  bufsize = 0;
    char*   line    = NULL;
    ssize_t linelen = getline(&line, &bufsize, makefile);

    int add_Targ_Flag = 0; //prevents adding a target in the first iteration
    char* name = "";

    while(-1 != linelen) {

        if(line[linelen-1]=='\n') {
            linelen -= 1;
            line[linelen] = '\0';
        }
        do_line_logic(line, &name, &add_Targ_Flag);
        linelen = getline(&line, &bufsize, makefile);
    }

    targlist = append_info(targlist, name, depList, ruleList);

    for(int i = 1; i <= argc - 1; i++) {
        if(find_name(argv[i]) != NULL) {
            processDeps(argv[i]);
            if (proc_targ_check(argv[i]) == 1)
                processTarget(argv[i]);
        }
    }

    free(line);
    return EXIT_SUCCESS;
}

/* Do Line Logic
 *  handles all the logic for a line from the umake file
 *  will either add a target, or add an environment variable
 *  or will build the rules/dependencies
 */
void do_line_logic(char* line, char** name, int* add_Targ_Flag) {

    char* rule = strdup(line);
    int argc = 0;
    char** args = arg_parse(line, &argc);
    char* test_val = strchr(line,'=');
    char* comment_test = strchr(line, '#');
    if(comment_test != NULL)
        *comment_test = '\0';


    if(line[0] == '\t'){
        ruleList = append_str(ruleList, rule);
    }
    else if (line[0] == '\0'){
        //do nothing
    }
    else if (test_val != NULL){
        *test_val = '\0';
        setenv(line, test_val + 1, 1);
    }
    else {
        if (*add_Targ_Flag == 1){ //does not add a target in the first iteration
            targlist = append_info(targlist, *name, depList, ruleList);
            depList = NULL;    //reset for next target
            ruleList = NULL;
        }
        *add_Targ_Flag = 1;
        char* tempString = strdup(args[0]);
        tempString[strlen(args[0]) -1] = '\0';
        *name = tempString;

        for(int i = 1; i < argc; i++){
            char* dep = strdup(args[i]);
            depList = append_str(depList, dep);
        }
    }
    free(args);

}

/*Process Dependencies
 *  Recursively find all the dependencies for a target
 *  and process those first.
 *  Checks if dependencies are newer or not and checks if it already exists
 *  before processing any target
 */
void processDeps(const char* target){
    strList* depList = find_depend(target);
    while(depList != NULL){
        if (find_name(depList->str) != NULL && proc_targ_check(target) == 1)
            processTarget(depList->str);
        depList = depList->next;
    }
}

/* Process Target
* Will process a target by going through the rule linked list and
* executing each rule
*/
void processTarget(const char* target){

    strList* ruleList = find_rules(target);
    while(ruleList != NULL){
        processline(ruleList->str);
        ruleList = ruleList->next;
    }
}

/* Process Target Check
* Does a check to see if Process target should return
* if either the target does not exist, or any dependency is newer than the
*   target it will return 1(True) indicating a Target will be processed
* if that is not the case then return 0(False) and no targets will be processed
*/
int proc_targ_check(const char* target){
    struct stat targ_stat;
    struct stat dep_stat;
    int success = stat (target, &targ_stat);
    if (success != 0){
        return 1;
    }
    else{
        strList* depList = find_depend(target);
        while(depList != NULL){
            stat(depList->str , &dep_stat);
            if(dep_stat.st_mtime > targ_stat.st_mtime){
                return 1;
            }
            depList = depList->next;
        }
    }
    return 0;
}


/* Process Line
* will either parse a line and execute that line's commands
* or fail to do so and print and error
*/
void processline (char* line) {

    int argc = 0;
    int old_io[2] = {-1,-1};
    int size = sizeof(line) * 2;
    char new[size];
    memset(new, 0, size);
    int exit_val = expand(line, &new[0], sizeof(new));
    if (exit_val == 0) {
        printf("Error: brace mismatch! Now exiting\n");
        exit(3);
    }
    char** args = arg_parse(new, &argc);
    io_redirect(args, argc, old_io);
    const pid_t cpid = fork();
    switch(cpid) {

        //I still am not sure on how there should be strdup here
        case -1: {
            perror("fork");
            free(args);
            break;
        }

        case 0: {
            execvp(args[0], args);
            perror("execvp");
            exit(EXIT_FAILURE);
            break;
        }

        default: {
            if(argc == 0){
                //do nothing
                break;
            }
            int   status;
            const pid_t pid = wait(&status);

            if (old_io[0] == 0 && old_io[1] != -1) //reset old stdin/stdout
                dup2(old_io[1], fileno(stdin));
            else if (old_io[0] == 1 && old_io[1] != -1)
                dup2(old_io[1], fileno(stdout));

            free(args);
            if(-1 == pid) {
                perror("wait");
            }
            else if (pid != cpid) {
                fprintf(stderr, "wait: expected process %d, but waited for process %d",
                cpid, pid);
            }
            break;
        }
    }
}

/*IO redirect
* Redirects the input or output depending on the flags that were found
*   either append, redirect input, or redirect output
* args: the line broken down into args
* argc: the count of arguments
* old_io[0]: contains the flag of what stdio was changed
* old_io[1]: contains the fileno of the old stdio
*/
void io_redirect(char** args, int argc, int* old_io){

    for (int i = 0; i < argc; i++){

        if ((strcmp(args[i], ">>")) == 0){
            FILE* fp = fopen(args[i+1], "a+"); //creates file if not there
            old_io[0] = 1;
            old_io[1] = dup(fileno(stdout));
            dup2(fileno(fp), fileno(stdout));
            fclose(fp);

            args[i] = '\0';
        }

        else if (args[i][0] == '>'){
            FILE* fp = fopen(args[i+1], "w+"); //creats file if not there
            old_io[0] = 1;
            old_io[1] = dup(fileno(stdout));
            dup2(fileno(fp), fileno(stdout));
            fclose(fp);

            args[i] = '\0';
        }

        else if (args[i][0] == '<'){
            FILE* fp = fopen(args[i+1], "r");
            if (fp == NULL){
                perror("Error opening file.");
                exit(3);
            }
            old_io[0] = 0;
            old_io[1] = dup(fileno(stdin));
            dup2(fileno(fp), fileno(stdin));
            fclose(fp);

            args[i] = '\0';
        }
    }
}

/* Expand
 * orig    The input string that may contain variables to be expanded
 * new     An output buffer that will contain a copy of orig with all
 *         variables expanded
 * newsize The size of the buffer pointed to by new.
 * returns 1 upon success or 0 upon failure.
 *
 * Example: "Hello, ${PLACE}" will expand to "Hello, World" when the environment
 * variable PLACE="World".
 */
int expand(char* orig, char* new, int newsize){

    int idx = 0;
    for(int i = 0; i < strlen(orig); i++, idx++){
        if (orig[i] == '$'){
            int r_brace_idx = brace_match(orig, i+1);
            if (r_brace_idx != -1) {
                char* variable = strndup(orig + i + 2, r_brace_idx - i -2);
                char* new_name = getenv(variable);
                for(int j = 0; j <  strlen(new_name); j++, idx++){
                    new[idx] = new_name[j];
                }
                i += strlen(variable) + 2;
                idx--;
            }
            else
                return 0; //no matching braces
        }
        else
            new[idx] = orig[i];
    }
    new[idx] = 0;
    return 1;
}


int brace_match(char* orig, int i){
    if (orig[i] == '{')
        i++;
    else
        return -1;

    for(;orig[i] != '\0'; i++){     //right bracket search
        if (orig[i] == '}')
            return i;
        else if (orig[i] == '{')
            return -1;
    }
    return -1;
}








//
