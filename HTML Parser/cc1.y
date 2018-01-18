%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <signal.h>
#include <setjmp.h>

int yylex(void);
%}

%union {
   char c;
   char s[2048];
}
%token <s> TAG
%token <s> TEXT
%token <s> BAAPTAG
%token <s> BAAPTAG1
%token <s> TD
%token <s> TABLEEND
%token <s> STARTHTML
%token <s> ENDHTML
%token <s> EOF1
%%

FILES:
 | FILES FILE
 
FILE: STARTHTML list ENDHTML {printf("end of html\n"); 
		fclose(f);
 		 file1++;
		 printf("current file: %d\n\n",file1);
		f = fopen("Output.csv","a");
		//fprintf(f,"\nfile is %d\n",file1);
}
 | list
;


list: TAG  
 | TEXT
 | list TAG { if(tBody==1 && IS($2,"<tr>")){
	    fprintf(f,"\n");
	    fprintf(f,"\"%s\"",price);
	    fprintf(f,",\"%s\"",time);
	    tdFlag=1; }} 
 | list TEXT 
 | list BAAPTAG TEXT { printf(" %s\n",$3);
 			if(bf==1){strcpy(price,$3);bf=0; }
			if(bf==2){strcpy(time,$3);bf=0; }
			}
 | list TDS
;

TDS: TD TD {  if(tdFlag==0){
	  fprintf(f,"NULL");
	  tdFlag=1;}
	  else{ fprintf(f,",NULL"); }
	}
 | TD TEXT TD { if(tdFlag==0){
	  fprintf(f,"\"%s\"",$2);
	  printf("writing in file");
	  tdFlag=1;
	  }else{ fprintf(f,",\"%s\"",$2);
	  }
	}
 | TD TAG TEXT TAG TD { if(tdFlag==0){
	  fprintf(f,"\"%s\"",$3);
	  tdFlag=1;
	  }else{
	  fprintf(f,",\"%s\"",$3);
	  }
	}
 | TD TAG TAG TD { if(tdFlag==0){
	  fprintf(f,"NULL");
	  tdFlag=1;
	  }else{
	  fprintf(f,",NULL");
	  }
        }
;
%%
#include <ctype.h>
int indexNum=0,lineNum=0,tBody=0, trindex=0,tdindex=0,tdMaxindex=0,fileNum=0;
int f1=0,tdFlag=0,file1=0,eh1=0i,bf=0;
char price[20]; char time[50];
FILE *f;
#define IS(a,b) (strcmp((a),(b))==0)
#define SUBSTRING(a,b) (strstr((a),(b))!=NULL)
int yylex(void)
{
  int c;
  char *tv=yylval.s;
  int p=0;
  while(( c=getc(stdin))!= EOF){
  
  if(tBody==1){
  	  if(c=='<'){
	do{ tv[p++]=c; c=getc(stdin); indexNum++;
        if(indexNum>900 || p>900){indexNum=0; p=0;}
        if(c=='\n'){lineNum++;indexNum=0;}}
        while(c!='>');
       tv[p++]=c;
       tv[p]='\0';
       if(SUBSTRING(tv,"td")) {return TD;}
       if(SUBSTRING(tv,"/div")){
		tBody=0; return TAG;}
       return TAG;
        }
  else{
        do{ tv[p++]=c; c=getc(stdin); indexNum++;
        if(p>900 || indexNum>900){indexNum=0;p=0;}
        if(c=='\n') {lineNum++; indexNum=0;}}
        while(c!='<');
        ungetc(c,stdin);
        tv[p]='\0';
        return TEXT;
 }
  }

  if(c=='<'){
        do{ tv[p++]=c; c=getc(stdin); indexNum++; 
	if(indexNum>900 || p>900){indexNum=0; p=0;}
	if(c=='\n'){lineNum++;indexNum=0;}}
        while(c!='>');
       tv[p++]=c;
       tv[p]='\0';
       if(SUBSTRING(tv,"!doctype")){printf("start\n"); return STARTHTML;}
       if(SUBSTRING(tv,"div class=\"qwidget-symbol\"")){return BAAPTAG;}
       if(SUBSTRING(tv,"div id=\"qwidget_lastsale\" class=\"qwidget-dollar\"")){bf=1;return BAAPTAG;}
       if(SUBSTRING(tv,"span id=\"qwidget_markettime\">")){bf=2;return BAAPTAG;}
       if(SUBSTRING(tv,"div class=\"OptionsChain-chart borderAll thin\"")){ 
		tBody=1; return BAAPTAG;}
       if(SUBSTRING(tv,"/html"))  { lineNum=0; fileNum++;eh1=1;	return ENDHTML;}
       return TAG;
        }
  else{ 
	if(eh1==1){
	  do{ c=getc(stdin);}
	  while(c==' ' || c=='\n');
	  if(c!='<')
	  return 0;
	  else
	  ungetc(c,stdin);
	  eh1=0;
	}
	else{
	do{ tv[p++]=c; c=getc(stdin); indexNum++;
        if(p>900 || indexNum>900){indexNum=0;p=0;}
	if(c=='\n') {lineNum++; indexNum=0;}}
        while(c!='<');
	ungetc(c,stdin);
	tv[p]='\0';
	return TEXT;
	}
 }}
 return c;
}

int yyerror(char *s) { printf("yyerror:%s\n at line: %d, fileNum: %d, index:%d\n",s,lineNum,indexNum,fileNum,yylval.s);}

int main(int argc, char **argv)
{
  f = fopen("Output.csv","w");
  yyparse();
  fclose(f);
  printf("end of program\n");
  printf("total files: "+file1); 
}
