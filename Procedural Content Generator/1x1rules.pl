% Author: Jon Spitzer
% Date: 4/4/2018

 % Item Classification
   % is_item :-
   % is_support :-
   % is_container :-
   % is_environment :-

 % Position Classification
   % is_on :-
   % is_inside :-
   % has_no_relation :- not(has_relation(X, item))
   % has_relation(support, item)

 % Defining Adjacency of Grid Tiles
 adjacent(X,Y,NX,Y) :- NX is X+1.
 adjacent(X,Y,NX,Y) :- NX is X-1.
 adjacent(X,Y,X,NY) :- NY is Y+1.
 adjacent(X,Y,X,NY) :- NY is Y-1.
 
 random_cell(W,H,X,Y) :-
    random(0,W,X),
    random(0,H,Y).
 

 % Defines cells of grid
 cell(X, Y, W, H) :-
    X >= 0,
    X < W,
    Y >= 0,
    Y < H.

 % Determines if a path between two nodes exists
 % X:  Current y-position
 % Y:  Current x-position
 % XF: Final x-position
 % YF: Final y-position
 % W:  Width of grid-space
 % H:  Height of grid-space
 % OC: List of occupied cells
 % VC: List of visiited cells
 does_path_exist(X,Y,XF,YF,W,H,OC,VC) :-
    path_exists(X,Y,XF,YF,W,H,OC,VC).

 path_exists(X,Y,XF,YF,W,H,OC,VC) :-
    adjacent(X,Y,XF,YF).
                           
 path_exists(X,Y,XF,YF,W,H,OC,VC) :-
    adjacent(X,Y,NX,NY),                        % Get an adjacent cell
    cell(NX,NY,W,H),                            % Ensure cell is within map
    \+memberchk([NX,NY], OC),                   % Ensure cell is not occupied
    \+memberchk([NX,NY], VC),                   % Ensure cell is not visited
    path_exists(NX,NY,XF,YF,W,H,OC,[[X,Y]|VC]).

 % Place 1v1 Objects on a grid of dimension HxW
 % Max: Total number of objects to be placed.
 % Width: Width of map.
 % Height: Height of map.
 generate_support_map(Max, Width, Height, Result) :-
    populate_map_supports(0, Max, Width, Height, [], Result).
 
 % Same as generate_map but takes in a list of already occupied locations
 append_support_map(Max, Width, Height, Occupied, Result) :-
    populate_map_supports(0, Max, Width, Height, Occupied, Result).

 % SupportsLoc is an array of support locations
 % ItemsLoc is an array of item locations
 generate_map(Supports, Items, Width, Height, SupportsLoc, ItemsLoc) :-
    populate_map_supports(0, Max, Width, Height, Occupied, SupportsLoc),
    populate_map_items(Items, SupportsLoc, ItemsLoc).
    
 append_items_map(Items, Locations, Result) :-
    populate_map_items(Items, Locations, Result).

 % Termination Case: Max number of objects have been placed
 populate_map_supports(Count, Max, Width, Height, Result, Result) :-
    Count >= Max.

 % Initial Case: Placing the first object
 populate_map_supports(Count,Max,Width,Height,Locations,Result) :-
    Count = 0,
    random_cell(Width,Height,X,Y),
    populate_map_supports(1,Max,Width,Height,[[X,Y]],Result).

 % Recursive Case
 populate_map_supports(Count,Max,Width,Height, Locations, Result) :-
    Count < Max,
    random_cell(Width, Height, X, Y),                         % Get a Cell
    \+memberchk([X,Y], Locations),                            % Ensure Location isn't Occupied
    last(Locations,C),                                        % Location of Previous Object
    reverse(C,P),                                             % Reverse Order of Previous Location
    last(P,XI),                                               % Get X-Coordinate of Previous Object
    last(C, YI),                                              % Get Y-Coordinate of previous object
    does_path_exist(X,Y,XI,YI, Width, Height,Locations,[]),
    populate_map_supports(Count + 1, Max, Width, Height, [[X,Y]|Locations], Result).
    

 populate_map_items(Items, Slocations, Result, Result) :-
    Items = 0;

 %Items = # of items to place
 %Locations = Cells with supports
 populate_map_items(Items, Slocations, Ilocations, Result) :-
    Items > 0,
    length(Slocation, L),
    random(0,L,R),
    nth(R, Slocations, E),
    subtract(Slocations, [E], SLocs),
    populate_map_items(Items - 1, SLocs, [E|Ilocations], Result).
 
