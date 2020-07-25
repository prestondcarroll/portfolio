% Author:
% Date: 5/9/2018

random_cell(W,H,X,Y) :-
    random(0,W,X),
    random(0,H,Y).
    
    
lastElement(X,L) :-
    last(X,L).