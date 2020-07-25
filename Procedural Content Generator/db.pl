%  Facts/Assertion
        /* Fact(Object1, Object2) */
        /* Classification of an object or a group of opjects*/
        loves(romeo, juliet). /*Assert that Romeo Loves Juliet*/

% Variables start with Uppercase Letters or underscore
%       loves(romeo, X). /*Who loves Romeo?*/

/* Rules */
        loves(juliet, romeo) :- loves(romeo, juliet). /*Juliet Loves Romeo if(':-') Romeo loves Juliet*/

/* Writing Output */
        /*write('text') */
        does_juliet_love_romeo :-
                loves(juliet, romeo),
                write('Juliet would kill herself for Romeo').


/* Defining facts */
   male(scott).
   male(denver).
   male(hunter).
   male(jacob).
   male(thomas).
   female(heather).
   female(juliet).
   female(amy).


/* Listing all things that are defined under a fact */
        /*Format: listing(fact)*/
        /*Example: listing(male)*/

/* Get All Permutations of a pair of facts */
        /* Format: Fact(Variable), Fact(Variable)*/
        /* Example: male(X), female(Y). */
        /* Use semicolon to cycle through entries */


/* Rules */
        /* Fact depends on a group of other facts */
        /* Fact :- Facts */

        /* Single Condition Rule */
        loves(phil, amy) :- female(amy). /* Phil loves amy if amy is a female */

        /*Multiple Condition Rule (AND)*/
        happy(phil) :- female(amy), female(juliet). /* Phil is happy if amy AND juliet are female */

        /* Multiple Condition Rule (OR) */
        inlove(phil) :- female(amy).
        inlove(phil) :- female(juliet). /* Phil is in love if either Amy OR juliet is female */

        parent(carl, phil).

/* Formatted String */
        /* format('....', ...) */
        /* ~w indicates a variable */
        /* ~s indicates a string */
        /* ~n indicates a new line */
        is_in_love :-
                loves(X, Y),
                format('~w ~s ~w ~n', [X, "is in love with", Y]).
        /* Example: is_in_love.*/

/* Case Statement */
        /* Define statements for specific result*/

        /* If 5 is passed in */
        what_grade(5) :-
                write('Go to kindergarden').

        /* Else if 6 is passed in*/
        what_grade(6) :-
                write('Go to 1st grade').

        /* Anything else is passed in */
        what_grade(Other) :-
                Grade is Other - 5,
                format('Go to grade Grade ~w', [Grade]).
                
                
/* Returning Sets of Data & Recursion */
   connected(bond_street,oxford_circus,central).
   connected(oxford_circus,tottenham_court_road,central).
   connected(bond_street,green_park,jubilee).
   connected(green_park,charing_cross,jubilee).
   connected(green_park,piccadilly_circus,piccadilly).
   connected(piccadilly_circus,leicester_square,piccadilly).
   connected(green_park,oxford_circus,victoria).
   connected(oxford_circus,piccadilly_circus,bakerloo).
   connected(piccadilly_circus,charing_cross,bakerloo).
   connected(tottenham_court_road,leicester_square,northern).
   connected(leicester_square,charing_cross,northern).

   /* Return path between two location */

      /* Base Case - The two locations are connected (adjacent) */
 %     path(X,Y,noroute):-connected(X,Y,L).
      
      /* Recursive Case */
 %     path(X,Y,route(Z,R)):- connected(X,Z,L),
 %                            path(Z,Y,R).
                                  
   /* Just return true/false if a path exists/doesn't exist */

      /* Base Case */
      reachable(X,Y) :- connected(X,Z,L).

      /* Recursive Case */
      reachable(X,Y) :- connected(X,Z,L)
                        reachable(Z,Y).
                        
/* Returning Lists */
   /* Base Case: X and Y are adjacent */
   path(X,Y,[]) :- connected(X,Y,L). /* [] is an empty list & list terminator */

   /* Recursive Case */
   path(X,Y,[Z|R]) :- connected(X,Z,L),
                      path(Z,Y,R).