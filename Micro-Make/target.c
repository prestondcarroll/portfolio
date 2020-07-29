/* CSCI 347 target.c
 * Assignment1 Fall 2017
 *
 * 16 OCT 2017, Student: Preston Carroll - finished version author
 *
 * purpose: declares the functions for two types of linked lists
 *      alist type linked list and a strList type linked list
 */


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <string.h>
#include "arg_parse.h"
#include "target.h"

/* CONSTANTS */


/* Find Dependency
* calls the auxillary function
*/
strList* find_depend(const char* name) {
    return find_depend_aux(name, targlist);
}

/* Find Dependency Aux
* recursively find the dependency linked list and return it
* otherwise return NULL
*/
strList* find_depend_aux(const char* name, alist* l){
    if(l == NULL){
        return NULL;
    }
    else if (strcmp(name, l->name) == 0){
        return l->dependencies;
    }
    else {
        return find_depend_aux(name, l->next);
    }
}

/* Find Rules
* calls the auxillary function
*/
strList* find_rules(const const char* name) {
    return find_rules_aux(name, targlist);
}

/* Find Rules Aux
* recursively find the Rules linked list and return it
* otherwise return NULL
*/
strList* find_rules_aux(const char* name, alist* l){
    if(l == NULL){
        return NULL;
    }
    else if (strcmp(name, l->name) == 0){
        return l->rules;
    }
    else {
        return find_rules_aux(name, l->next);
    }
}

/* Find Name
* calls the auxillary function
*/
alist* find_name(const char* name) {
    return find_name_aux(name, targlist);
}

/* Find Name Aux
* recursively find the target Node matching the supplied name and return it
* otherwise return NULL
*/
alist* find_name_aux(const char* name, alist* l){
    if(l == NULL){
        return NULL;
    }
    else if (strcmp(name, l->name) == 0){
        return l;
    }
    else {
        return find_name_aux(name, l->next);
    }
}

/* Add Information
* add the name and the dependency list to the node and then return the node
*/
alist* add_info(char* name, void* depList, void* ruleList, alist* next){
    alist* n = malloc(sizeof(alist));
    if(!n){
        perror("malloc");
        exit(1);
    }
    n->name = strdup(name);
    n->dependencies = depList;
    n->rules = ruleList;
    n->next = next;

    return n;
}

/* Append information
* add a node containing the name and the dependency list
* to the main target linked list
*/
alist* append_info(alist* head, char* name, void* depList, void* ruleList) {
    if(head == NULL){
        alist* new_node = add_info(name, depList, ruleList, NULL);
        head = new_node;
        return head;
    }

    alist *cursor = head;
    while(cursor->next != NULL)
        cursor = cursor->next;

    alist* new_node = add_info(name, depList, ruleList, NULL);
    cursor->next = new_node;

    return head;
}

/* Add string
* create a new node, add the string to it and then return the node
*/
strList* add_str(char* str, strList* next){

    strList* n = malloc(sizeof(strList));
    if(!n){
        perror("malloc");
        exit(1);
    }
    n->str = str;
    n->next = next;

    return n;
}

/* Append String
* append a node containing a string to a strList linked list
*/
strList* append_str(strList* head, char* str) {
    if(head == NULL){
        strList* new_node = add_str(str, head);
        head = new_node;
        return head;
    }

    strList *cursor = head;
    while(cursor->next != NULL)
        cursor = cursor->next;

    strList* new_node = add_str(str, NULL);
    cursor->next = new_node;

    return head;
}


























//
