# Pruning Item sets using Apriori Algorithm

I implemented the A-priori algorithm to determine the cost saving strategies for the city complaints. 

# The program can be executed in following 3 modes: 

1. java AP -g < $F > 1col.txt { this file should be: "UID","your data" }
This mode ("generate") will take an input file and generate 1 column (the additional
column) of augmented input e.g. "11pm+" as described in class.

2. java AP -v 0.09 0.31 < small.txt ## show the pruned itemsets
This mode ("verbose") will show my implementation of A-priori on a small data set
with the same structure as the main file. In this mode I used the existing
fields in the file to generate 3 and 4 itemsets.

3. java AP -m minsup minconf $F 1col.txt  ## the real thing
This mode ("master") should discover and write the 3-itemset rules and 4-itemset rules
to standard output.

# Assumptions | Output should be as folllows: 

for mode 2: 

minsup=0.09
minconf=0.31 (to prune if lower than this)

A c=0.200 pruned
B c=0.200 pruned
C c=0.322 not pruned
D c=0.400 not pruned
E c=0.500 not pruned
C->D c=0.200 pruned
C->E c=0.400 not pruned

minsup  (0.0 to 1.0)
minconf (0.0 to 1.0)

for mode 3: 
e.g.
"Heat","11pm+" => "Electric Heater" (sup = 0.03 conf = 0.40)

# Important Notes

1. Self define minsup and minconf (subject to Professor's approval)
2. Rules should have high confidence AND be meaningful i.e. should help the city take specific steps to save money. Simple descriptive statistics (e.g. Brooklyn has the most water problems) are NOT enough!
3. This A-priori program will run on a larger 311 data set to see how well support and confidence measurements (in step 1) hold up.
4. This A-priori program should not use more than 100MB of memory (even for the larger final data set which will probably be 600MB or more in size) and should complete in under 100 seconds.
5. Final submission will be Java or C or C++ program. Program should contain a comment block at the beginning that explains any specific settings e.g. JVM heap or limitations on running the program. Note that if program does not finish in 120 seconds the autograder will kill it and you will get a grade deduction. The usual rules apply i.e. should not crash, hang or swallow other system resources like memory or file system.
