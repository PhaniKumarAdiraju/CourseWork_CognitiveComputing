# Options Trader 

I will apply the A-priori algorithm on the spreadsheet data from CC2 to identify the critical factors affecting the "profit" from your option strategy.

I modified the A-priori program that was developed in CP1 to work as follows.

1. java Options -g < data.txt >items.txt

This is "-g" or generate mode. The input is the CSV data I generated before. (Note that this is the complete CSV data) You can also include up to 2 additional columns based on your analysis of your trading strategies in CC1 and CC2.

2. java Options -m 0.02 0.3 < items.txt

This is "-m" or master mode. The only input is the input file you generated in "-g" generate mode. My program will output the association rules that enjoy a min. sup. of 0.02 and min. conf. of 0.3 (these were supplied on the command line).

# Submission Requirements

1. Only one file either Options.java or Options.C/CPP. This means you can ONLY use C++, C or Java. No other languages are permitted. This code MUST be an extension of CP1 submission. (The autograder will run a diff.)

2. Java - you cannot use any other third-party frameworks or classes or libraries beyond what is available in the AFS Java JDK.

3. Program must run within limits e.g. 300 sec run time, 40MB memory and no additional files like temporary files. Program must ONLY produce output on standard output (both modes) and NOT in any file (esp not in any file you have specified.)

4. Program must have a comment block at the beginning that must outline the rules that you are chasing. This is required and if it is absent there will be a deduction.
