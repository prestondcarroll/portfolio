# Phylogenetic Tree Builder

This program first builds a phylogenetic tree by reading in amino acid sequences for each species, and then comparing the sequences to infer the evolutionary relationships. 

The evolutionary distance between two species is given a weight, and then the hierarchal structure is inferred by using an agglomerative clustering algorithm on the weights.

example_input.txt is what the species info looks like when it is fed in. <br />
example_tree_output.txt is what the tree looks like when the program is run on the terminal, with different species being further apart. <br />
example_evo_distances_output.txt lists all the evolutionary weights between species after the tree has been built. <br />

This was an exercise in learning about trees in my Data Structures class. It received a perfect score.

