/* CSCI 347 target.h
 * Assignment2 Fall 2017
 *
 * 16 OCT 2017, Student: Preston Carroll
 *
 * purpose: declare the prototypes for target.c
 */

 /* PROTOTYPES */


 typedef
 struct strList {
     struct strList* next;
     char* str;

 } strList;

 typedef
 struct Alist_st {
     struct Alist_st* next;
     char* name;
     strList* dependencies;
     strList* rules;
 } alist;



 alist* targlist;
 strList* depList;
 strList* ruleList;

 strList* find_depend(const char* name);
 strList* find_depend_aux(const char* name, alist* l);
 strList* find_rules(const char* name);
 strList* find_rules_aux(const char* name, alist* l);
 alist* find_name(const char* name);
 alist* find_name_aux(const char* name, alist* l);
 alist* add_info(char* name, void* depList, void* ruleList, alist* next);
 alist* append_info(alist* head, char* name, void* depList, void* ruleList);
 strList* append_str(strList* head, char* str);
 strList* add_str(char* str, strList* next);
