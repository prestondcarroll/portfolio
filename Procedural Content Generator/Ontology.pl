% Author:
% Date: 5/16/2018

item(I):-I is "knife".
item(I):-I is "chicken".
item(I):-I is "butter".
is_item(I) :- item(I).

food(F) :- "chicken".
is_food(F) :- food(F).


support(S):- S is "counter".
support(S):- S is "fridge".
is_support(S) :- support(S).


container(C) :- C is "fridge".
container(C) :- C is "drawer".
is_container(C) :- container(C).


cooler(C) :- "fridge".
cooler(C) :- "freezer".
is_cooler(C) :- cooler(C).


placable(I,S) :- is_food(I), is_cooler(S).
placable(I,S) :- is_food(I), is_support(S).
placable(I,S) :- is_item(I), is_support(S).
placable(I,S) :- is_item(I), is_container(S).
is_placable(I,S) :- placable(I,S).