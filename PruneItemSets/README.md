# Pruning Item sets using Apriori Algorithm

You will implement the A-priori algorithm (here). Today's class slides are here (PPT).
You can use Java or C or C++. You will submit a single file (AP.java or AP.C) which
can be run in the following three modes.

1. java AP -g < $F > 1col.txt { this file should be: "UID","your data" }
This mode ("generate") will take an input file and generate 1 column (the additional
column) of augmented input e.g. "11pm+" as described in class.

2. java AP -v 0.09 0.31 < small.txt ## show the pruned itemsets
This mode ("verbose") will show your implementation of A-priori on a small data set
with the same structure as the main file. In this mode you will use the existing
fields in the file to generate 3 and 4 itemsets.

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

3. java AP -m minsup minconf $F 1col.txt  ## the real thing
This mode ("master") should discover and write the 3-itemset rules and 4-itemset rules
to standard output.
e.g.
"Heat","11pm+" => "Electric Heater" (sup = 0.03 conf = 0.40)

Important Notes

You will define minsup and minconf (subject to my approval)
Your rules should have high confidence AND be meaningful i.e. should help the city take specific steps to save money. Simple descriptive statistics (e.g. Brooklyn has the most water problems) are NOT enough!
Intermediate deliverables: On Fri 3/31 you will meet with me to discuss the rules you think you will go after e.g. "Heat" and the support and confidence measures which you think are appropriate. I will assign a grade (A, B, C) with deflators like before based on the correctness, value and depth of your approach. On Tue 4/4 you should ideally have "verbose" part working. This way you will get a couple more weeks to fine tune your approach and play with the data.
Your A-priori program will be eventually run on a larger 311 data set to see how well your support and confidence measurements hold up.
Your A-priori program should not use more than 100MB of memory (even for the larger final data set which will probably be 600MB or more in size) and should complete in under 100 seconds.
Your final submission will be your Java or C or C++ program. Your program should contain a comment block at the beginning that explains any specific settings e.g. JVM heap or limitations on running the program. Note that if your program does not finish in 120 seconds the autograder will kill it and you will get a grade deduction. The usual rules apply i.e. should not crash, hang or swallow other system resources like memory or file system.