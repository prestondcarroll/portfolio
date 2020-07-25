% Author: Jon Spitzer
% Date: 5/30/2018

%-------------------- Object Definition --------------------%

%Containers
container(fridge).
container(dishwasher).
container(oven).
container(sink).
container(bbq).

%Walls
wall(wall_1).
wall(wall_2).
wall(wall_3).
wall(wall_4).

%Supports
support(counter).
support(fridge).

%Utensils
utensil(knife).
utensil(spoon).
utensil(fork).

%Coolers
cooler(fridge).

%Cookers
cooker(bbq).
cooker(oven).

%Dispensers
dispenser(beer_tap).

%Perishables
perishable(chicken).
perishable(beer).

%Non-Perishables
nonperishable(noodles).

%Appliances
appliance(fridge).
appliance(dishwasher).

%Drinks
drink(beer).


%-------------------- Object Attributes --------------------%

%Sittable Objects
sittable(stool).

%Movable Objects
movable(stool).
movable(chicken).
movable(knife).
movable(beer).

%Consumables
consumable(beer).
consumable(chicken).

%Interactable Objects
interactable(beer_tap).
interactable(dishwasher).
interactable(fridge).
interactable(bbq).

%Grabbable Objects
grabbable(chicken).
grabbable(knife).
grabbable(beer).


%-------------------- Hierarchy Definitions --------------------%

%Item Definition
is_item(I) :-
  item(I).
  
is_item(I) :-
  is_food(I).
  
is_item(I) :-
  is_drink(I).

is_item(I) :-
  utensil(I).
  
%Food Definition
is_food(F) :-
  food(F).
  
is_food(F) :-
  perishable(F).

is_food(F) :-
  nonperishable(F).
  
is_drink(D) :-
  drink(D).

%Room Definition
is_wall(X) :-
  wall(X).
  
%Appliance Definition
is_appliance(X) :-
  appliance(X).
  
is_appliance(X) :-
  cooler(X).
  
is_appliance(X) :-
  cooker(X).

%Support Definition
is_support(S) :-
  support(S).
  
  
%-------------------- Capability Definitions --------------------%

%Movable
is_movable(I) :-
  movable(I).

%Interactable
is_interactable(I) :-
  interactable(I).

%Grabbable
is_grabbable(I) :-
  grabbable(I).

%Consumable
is_consumable(I) :-
  consumable(I).
  
  
%-------------------- Relationship Definitions --------------------%

%Relation Existence Definitions
is_adjacent_to(X,Y) :-
  in_front_of(X,Y).

is_adjacent_to(X,Y) :-
  is_left_of(X,Y).

is_adjacent_to(X,Y) :-
  in_right_of(X,Y).

is_adjacent_to(X,Y) :-
  in_behind(X,Y).

%Placement Feasibility Definitions
can_be_placed_adjacent_to(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_item(X),
  is_item(Y).

can_be_placed_adjacent_to(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_appliance(X),
  is_appliance(Y).

can_be_placed_adjacent_to(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_appliance(X),
  is_support(Y).

can_be_placed_adjacent_to(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_support(X),
  is_appliance(Y).

can_be_placed_adjacent_to(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_support(X),
  is_spport(Y).

can_be_in_front_of(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_appliance(X),
  is_wall(Y).

can_be_placed_inside(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_item(X),
  is_container(Y).

can_be_placed_on(X,Y) :-
  room_contains(X),
  room_contains(Y),
  is_item(X),
  is_support(Y).
  
  