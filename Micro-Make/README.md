# Micro Make

![Video Demo](https://raw.githubusercontent.com/prestondcarroll/projects/master/Micro-Make/demo.mp4)

This program is a simplified implementation of the Unix "make" functionality. Make is useful in automatically 
compiling parts of a project and recompiling parts that are out of date. For large projects, this tool is very valuable for simplifying
the compiling process.

This project was made in C and will store all the different targets as well as their rules for compiling. The dependencies
are stored in a linked list so when a target needs to be built it can iterate over the linked list and find new targets to build.
Completed for Computer Systems II, November 2017


Features:
* Command-line parsing - instructions that are specified will be parsed and executed
* Dependency Checking - Any component that is needed for compiling will be assembled first, and then the original file will be compiled.
* Variable processing - variables in the makeFile will be substituted with their actual values.
* I/O redirection - Input and output can be redirected to and from a file. 
* Pipes - stdout and stdin can be modified between commands
* Comment-Support - the makeFile supports adding comments
