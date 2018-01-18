# HTML Parser

You will build an options trading advisor as your class project. This (CC1) is the first part of your project. In this part you will write a BNF grammar (using yacc/bison) to parse the HTML data obtained from NASDAQ for your group and extract the option chain sheet. This sheet can be moved into a database for further processing and analysis.

Each group will submit a single file (cc1.y) that contains the grammar AND yylex function. I will run this as follows:

$ cat 148*-ibm.dat | ./cc1

That is, I will feed it all the daily data at once. (Your grammar should be able to recognize when one "snapshot" of data ends and another one begins.)

You will find your data in the /ta.dir/nyse/ directory organized by timestamp and company. The timestamp of the file represents the timestamp of the snapshot (and is not to be confused with the date of option expiration.)

Your program must produce a single CSV file with the entire option chain sheet, with the first column as the timestamp of the snapshot.

Note: This is an open-ended assignment and (as we've seen in class) there are many ways to go about this and many ways to organize the data. If you have questions then please ASK. There is a lot of learning, experimentation and organization to do so get started early!

Grading

This part of the project will carry 20% of the overall class grade. I will grade this in two parts: demo and actuals. I will schedule a demo (on 2/22) where I will review your group work and provide suggestions etc. When you submit your actual program I will run it to see how best it captures and organizes the data and how congruent it is to the discussion we had during your demo.

Good luck and happy investing!