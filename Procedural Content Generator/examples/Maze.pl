%%%%A chromatic maze generator
#const t_max = 35.
#const min_solution = 20.
#const max_solution = 35.
#const size = 6.

%%A range of space and time
dim(0..size-1).
time(0..t_max).

%%Neighbor adjacency on grid
adjacent(X, Y, X + 1, Y):- dim(X), dim(Y).
adjacent(X, Y, X - 1, Y):- dim(X), dim(Y).
adjacent(X, Y, X, Y + 1):- dim(X), dim(Y).
adjacent(X, Y, X, Y - 1):- dim(X), dim(Y).

%%Cycling terminal-displayable colors
color(red;yellow;green;cyan;blue;magenta).
next(red,yellow).
next(yellow,green).
next(green,cyan).
next(cyan,blue).
next(blue,magenta).
next(magenta,red).

%%Allowable color transitions
ok(C, C):- color(C).
ok(C1, C2):- next(C1, C2).
ok(C1, C2):- next(C2, C1).

%%Allowable steps
passable(SX, SY, X, Y):-
adjacent(SX, SY, X, Y),
cell(C1, SX, SY),
cell(C2, X, Y),
ok(C1, C2).

%%Description of chromatic mazes
1 {cell(C, X, Y):color(C)} 1:- dim(X), dim(Y).
1 {start(X, Y):dim(X):dim(Y)} 1.
1 {finish(X, Y):dim(X):dim(Y)} 1.

%%A flood-fill style player exploration
player_at(0, X, Y):- start(X, Y).
player_at(T, X, Y):-
    time(T),
    player_at(T-1, SX, SY),
    passable(SX, SY, X, Y),
    0 {player_at(0..T-1, X, Y)} 0.

    
%%The time of earliest completion
victory_at(T):- finish(X, Y), player_at(T, X, Y).

%%That completion happened at all
victory:- victory_at(T).

%%Requirements on all generated puzzles:
:- not victory.
:- victory_at(T), T < min_solution.
:- victory_at(T), max_solution < T.

%%%%Visualization support logic
tile_grid(size,size).
tile_char(X, Y, T #mod10):-
    T > 0,
    player_at(T, X, Y),
    not start(X, Y),
    not finish(X, Y).
tile_char(X, Y, s):- start(X, Y).
tile_char(X, Y, f):- finish(X, Y).
tile_color(X, Y, C):- cell(C, X, Y).