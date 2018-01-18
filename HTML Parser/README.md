# HTML Parser

I built an options trading advisor as class project. This (CC1) is the first part of your project. In this, I wrote a BNF grammar (using yacc/bison) to parse the HTML data obtained from NASDAQ and extracted the option chain sheet. This sheet can be moved into a database for further processing and analysis.

# To execute the program: 

  1. first need to download .dat files (html pages from NASDAQ webiste). Let's consider particular stock (ibm), download the        ibm option sheet for 1 week to 6 months of data. 
  2. run the following command
      $ cat 148*-ibm.dat | ./cc1

That is, I will feed it all the daily data at once. (My grammar is able to recognize when one "snapshot" of data ends and another one begins.)

This program produces a single CSV file with the entire option chain sheet, with the first column as the timestamp of the snapshot.

Note: This is an open-ended assignment and there are many ways to go about this and many ways to organize the data. If you have questions then please ASK. There is a lot of learning, experimentation and organization to do so get started early!
