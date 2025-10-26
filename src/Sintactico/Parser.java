//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "grammar.y"
    import java.io.*;

    import Lexico.AnalizadorLexico;

    import Tools.TablaSimbolos;
    import Tools.Pair;
    import Tools.TablaPalabrasReservadas;
    import Tools.Logger;
    import Tools.Cursor;
    import Tools.Info;
    import Tools.Terceto;
    import java.util.Scanner;



//#line 33 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short TWO_POINTS_ASSIGNATION=257;
public final static short STRING=258;
public final static short GREATER_OR_EQUAL=259;
public final static short LESS_OR_EQUAL=260;
public final static short EQUAL=261;
public final static short NOT_EQUAL=262;
public final static short CTE_INT=263;
public final static short CTE_FLOAT=264;
public final static short ID=265;
public final static short IF=266;
public final static short ELSE=267;
public final static short ENDIF=268;
public final static short PRINT=269;
public final static short RETURN=270;
public final static short VAR=271;
public final static short FOR=272;
public final static short FROM=273;
public final static short TO=274;
public final static short CR=275;
public final static short SE=276;
public final static short LE=277;
public final static short TOI=278;
public final static short INT=279;
public final static short FLOAT=280;
public final static short ARROW=281;
public final static short FUN=282;
public final static short CTE_INT_NEGATIVE=283;
public final static short SENTENCIA_ASIGNACION_PREC=284;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    2,    2,    2,    2,    2,    2,    1,    1,
    3,    3,    3,    4,    5,    5,    5,    5,   12,   12,
   13,   13,   13,   13,   16,   14,   20,    6,    8,    8,
    8,   22,    9,    9,    9,   23,   23,   15,   15,   25,
   25,   25,   17,   17,   26,   10,   19,   19,   29,   29,
   29,   28,   28,   30,   30,   30,   30,   30,   27,   27,
   31,   11,   11,   32,   32,   32,   35,   33,    7,    7,
   37,   37,   41,   18,   18,   18,   18,   18,   42,   42,
   43,   38,   44,   45,   45,   48,   46,   46,   46,   24,
   24,   51,   51,   51,   34,   34,   34,   34,   34,   54,
   54,   54,   54,   52,   52,   52,   52,   55,   55,   55,
   55,   50,   50,   50,   50,   56,   56,   59,   53,   53,
   60,   60,   60,   49,   49,   49,   49,   49,   49,   21,
   21,   21,   61,   39,   39,   39,   62,   40,   40,   36,
   36,   36,   64,   57,   57,   47,   47,   47,   63,   63,
   63,   66,   66,   66,   66,   58,   58,   65,   65,   65,
   68,   68,   68,   67,   67,   69,
};
final static short yylen[] = {                            2,
    4,    1,    3,    4,    2,    3,    3,    1,    2,    1,
    1,    2,    1,    1,    1,    1,    1,    1,    1,    1,
    6,    6,    6,    4,    3,    3,    3,    1,    4,    4,
    1,    3,    6,    4,    1,    5,    3,    3,    1,    2,
    2,    1,    3,    1,    2,    3,    3,    1,    2,    2,
    1,    5,    1,    4,    4,    4,    4,    4,    3,    1,
    2,    1,    1,    4,    3,    1,    3,    3,    4,    1,
    6,    1,    8,    2,    2,    3,    3,    1,    2,    1,
    5,    3,    4,    3,    1,    2,    3,    3,    3,    3,
    1,    2,    2,    2,    3,    3,    1,    1,    1,    3,
    3,    3,    3,    3,    3,    1,    1,    3,    3,    3,
    3,    1,    1,    1,    1,    5,    1,    4,    4,    1,
    4,    4,    4,    1,    1,    1,    1,    1,    1,    3,
    1,    1,    2,    3,    1,    1,    2,    3,    1,    3,
    1,    1,    2,    1,    3,    1,    1,    1,    3,    2,
    1,    2,    1,    2,    1,    3,    1,    2,    2,    1,
    1,    1,    1,    3,    1,    2,
};
final static short yydefred[] = {                         0,
    8,    0,    0,    0,    2,  148,    0,    0,    0,    0,
    0,  146,  147,    0,    0,    0,   10,   11,    0,   13,
   14,   15,   16,   17,   18,   31,   35,   62,   63,   66,
    0,    0,   70,    0,   72,    0,  135,  141,  136,  142,
    0,    0,    0,    0,    0,  124,  125,  126,  127,  112,
  113,    0,    0,    0,    0,  128,  129,    0,    0,   39,
    0,    0,  106,   91,    0,   97,   99,  107,  114,  115,
  117,  120,    0,    0,    0,    0,    0,    0,    0,   48,
   53,    0,    0,    0,    7,    9,   12,    0,    0,  143,
    0,    0,    0,  137,    0,    0,    0,   85,    0,    3,
    0,  145,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   44,   40,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   32,    0,    0,  132,    0,    0,
    0,    0,    0,    0,    0,   46,   60,   49,    1,    4,
    0,    0,  140,    0,    0,    0,    0,    0,   19,   20,
    0,    0,   80,    0,    0,  134,    0,   86,    0,   82,
    0,    0,  110,  111,    0,    0,    0,    0,    0,  157,
  165,   38,   45,    0,    0,   34,    0,    0,    0,    0,
   90,  108,  104,  109,  105,   29,   30,    0,    0,    0,
    0,    0,    0,    0,   47,   61,    0,   83,    0,    0,
    0,    0,   79,   69,    0,    0,  155,    0,  162,  163,
    0,    0,  139,    0,  151,  160,   84,    0,    0,    0,
  123,  121,  122,  119,    0,    0,  118,    0,   43,    0,
    0,   55,   57,    0,   56,   54,   59,    0,    0,    0,
    0,    0,   26,    0,   77,    0,  158,  159,    0,    0,
  150,  154,    0,   88,   89,   87,  116,  164,  156,   33,
   52,    0,    0,    0,   24,    0,    0,    0,    0,   71,
    0,  138,  149,   25,    0,    0,    0,   81,   27,    0,
   21,   22,   23,   73,
};
final static short yydgoto[] = {                          4,
  147,    5,   17,   18,   19,   20,   21,   22,   23,   24,
   25,  148,  149,  150,   58,  239,  230,  151,   78,  243,
  126,   26,   27,   59,   60,  114,  136,   79,   80,   81,
  137,   28,   29,   61,   30,   31,   32,   33,   34,  211,
   35,  152,  153,   36,   97,  160,   37,   98,   62,   63,
   64,   65,   66,   67,   68,   69,   70,  169,   71,   72,
  128,   39,  213,   40,  214,  215,  170,  216,  171,
};
final static short yysindex[] = {                      -111,
    0,  761, 1050,    0,    0,    0,  -28,  391,   -6, -227,
  -30,    0,    0, 1050, 1050,  834,    0,    0,   -2,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -39,  -76,    0,  -24,    0,  850,    0,    0,    0,    0,
 -228,  877,  -71, -195,   93,    0,    0,    0,    0,    0,
    0,   29,  -37,  -27, 1085,    0,    0,  -42,   50,    0,
 1117, -180,    0,    0,   17,    0,    0,    0,    0,    0,
    0,    0,  -33,  -66, -223, -150, -179,    2,   80,    0,
    0,  903,  915, -119,    0,    0,    0,  -71, -101,    0,
  931,  -23, -228,    0, 1050,  949,  134,    0, -119,    0,
  127,    0, -180, -180, -155, -155,  -71,  -71,  139,  -71,
  141,  972, -163,    0,    0,   65, -145,   40, -155,   17,
   17,  128,  163,  165,    0,  206,  127,    0,  -71,  127,
  -70, -249,  -64,  172,  984,    0,    0,    0,    0,    0,
  183,  329,    0,  391,  193,  -30,  931,  189,    0,    0,
  126, 1050,    0,  214,  227,    0, 1002,    0,  -96,    0,
   17,   17,    0,    0,  -12,   13,  -71,  -41,   -9,    0,
    0,    0,    0, 1014,  -42,    0,   65,   17,   65,   17,
    0,    0,    0,    0,    0,    0,    0,  -71,  127,  127,
   15, -247,   22,   32,    0,    0, 1038,    0,  153,  -71,
  178, 1050,    0,    0,  931,  227,    0,  -81,    0,    0,
   33,   47,    0,  216,    0,    0,    0,  282,  288,  289,
    0,    0,    0,    0,   98,   67,    0,  -71,    0,   71,
  127,    0,    0,   77,    0,    0,    0,  696,  -67,  -59,
  287,  735,    0, 1050,    0,  103,    0,    0,  219,  227,
    0,    0,   76,    0,    0,    0,    0,    0,    0,    0,
    0,  797,  221,  153,    0,  153,  284,  815,  231,    0,
  931,    0,    0,    0,   94,   95,   97,    0,    0,  241,
    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,  -38,    0,    0,    0,
    0,    0,    0,    0,    0,  344,    0,    0,  617,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    1,    0,    0,    0,    0,    0,    0,  260,    0,
    0,    0,    0,    0,   28,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  266,    0,
    0,  390,    0,   -5,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  523,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  277,    0,  539,    0,    0,    0,    0,    0,  -26,  -22,
  -20,    0,    0,    0,    0,    0,  450,    0,    0,  562,
    0,    0,    0,  294,    0,    0,    0,    0,    0,    0,
    0,  634,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  286,    0,    0,    0,    0,    0,    0,    0,    0,
   55,   89,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  116,  143,  177,  420,
    0,    0,    0,    0,    0,    0,    0,    0,  475,  578,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  293,    0,    0,  304,    0,    0,  218,    0,    0,
    0,  111,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  121,    0,    0,    0,  601,
  500,    0,    0,  -17,    0,    0,    0,    0,    0,  665,
    0,    0,    0,  305,    0,    0,    0,    0,    0,    0,
    0,    0,  122,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  510,    0,  -16,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  290,  -93,  -49, -189,  298,    0,
  -77,    0,    0,  377,    0,    0,    0,  375,    0,    0,
    0,    0,    0, 1226,    0,    0,    0,    0,    0,  248,
    0, -140,    0,    0,    0,    0,  323,    0,  405,  448,
    0,  -25,    0,    0,    0,    0,  896,  300,    0,    0,
    0,    0,  222,    0,    0,    0,  240,    0,    0,
};
final static int YYTABLESIZE=1467;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         86,
  144,  117,  108,  118,   89,  144,  202,  125,  113,   77,
  142,    3,  110,  192,   93,  233,  155,   44,   92,   93,
   94,   88,  144,   58,  193,   86,  234,   98,  222,    6,
  117,  227,  118,   73,  228,  120,  121,   74,  135,  131,
  144,  144,  144,  144,  144,  144,   91,  144,  263,  132,
   12,   13,  269,  224,  100,  117,   87,  118,  122,  144,
  144,  144,  144,  123,  245,   86,   86,   98,   98,  102,
   98,   98,   98,  249,   44,  116,  250,  161,  162,   86,
  112,  280,   50,   51,   52,   75,   98,   98,  101,   98,
  115,  178,  180,   76,  100,  100,   93,  100,  100,  100,
   92,   54,   94,  175,  176,   58,  105,   50,   51,   52,
  177,  106,  133,  100,  100,  102,  100,   50,   51,   52,
  138,  202,  241,  144,  135,  144,   54,  202,  101,  101,
   86,  101,  101,  101,  105,  103,   54,  104,  257,  106,
   86,  228,   95,  270,    1,  141,  250,  101,  101,  240,
  101,  153,   98,    2,  153,  102,  102,   86,  102,  102,
  102,  166,  152,   52,  166,  152,  218,  219,  220,  117,
  275,  118,  277,  159,  102,  102,  103,  102,  167,  100,
   86,  172,   95,   95,   45,   95,   95,   95,   86,   45,
  129,   50,   51,   52,  247,  248,   50,   51,   52,  264,
  265,   95,   95,  191,   95,  186,   53,  266,  176,  194,
   54,   53,  195,  101,  276,   54,  103,  103,  107,  103,
  103,  103,   45,  198,  124,   52,  144,   86,   43,   50,
   51,   52,  200,    6,   75,  103,  103,  109,  103,  226,
  102,  154,   76,  221,   53,   86,  187,  203,   54,  188,
  204,   86,  135,  206,   12,   13,  144,   92,  144,  144,
  144,  144,  144,  144,  144,  144,  144,   95,  223,  144,
  144,  144,  144,  135,  135,  238,  135,  232,  144,  144,
  144,  144,  144,   98,  235,   98,   98,   98,   98,   98,
   98,   98,   98,   98,  236,  179,   98,   98,   98,   98,
  242,  103,   50,   51,   52,   98,   98,   98,   98,   98,
  100,  251,  100,  100,  100,  100,  100,  100,  100,  100,
  100,   54,  254,  100,  100,  100,  100,  267,  255,  256,
  188,  258,  100,  100,  100,  100,  100,   84,  260,  261,
  273,  271,  278,    5,  101,  274,  101,  101,  101,  101,
  101,  101,  101,  101,  101,  279,   94,  101,  101,  101,
  101,  281,  282,   99,  283,  284,  101,  101,  101,  101,
  101,  102,  188,  102,  102,  102,  102,  102,  102,  102,
  102,  102,   42,  182,  102,  102,  102,  102,   51,    6,
   50,   51,   52,  102,  102,  102,  102,  102,   95,   41,
   95,   95,   95,   95,   95,   95,   95,   95,   95,   54,
   78,   95,   95,   95,   95,  156,   50,   74,  184,   96,
   95,   95,   95,   95,   95,   50,   51,   52,   75,   76,
   55,  111,  103,  199,  103,  103,  103,  103,  103,  103,
  103,  103,  103,  201,   54,  103,  103,  103,  103,  131,
   57,  134,   56,  246,  103,  103,  103,  103,  103,   96,
   96,   45,   96,   96,   96,  119,  225,  259,   50,   51,
   52,  272,    0,    6,  133,  161,    0,  212,   96,   96,
  252,   96,  161,   53,    6,    0,    0,   54,    0,  131,
  131,  207,    0,  131,   12,   13,  161,  161,    0,  130,
    0,  208,  209,  210,    0,   12,   13,    0,  131,    0,
    0,   16,   42,    0,  133,  133,    0,    0,  133,    0,
    0,    0,   65,   82,   83,    0,    0,    0,  212,    0,
    0,    0,    0,  133,    0,    0,  253,    0,   37,  130,
  130,    0,   45,  130,   96,   96,    0,    0,    0,   50,
   51,   52,  163,  164,    0,    0,    0,    0,  130,    0,
    0,   67,   65,   65,   53,    0,  181,    0,   54,  183,
  185,    0,  212,    0,  131,    0,    0,   64,   37,   37,
    0,   65,    0,    0,   45,    0,    0,    0,    0,    0,
    0,   50,   51,   52,    0,    0,    0,   37,    0,  133,
   36,   67,   67,    0,  157,    0,   53,    0,    0,    0,
   54,    0,    0,    0,    0,    0,   28,   64,   64,    0,
   67,  174,    0,    0,  130,    0,    0,    0,    0,    0,
    0,    0,    0,   68,    0,    0,   64,    0,    0,    0,
   36,   36,    0,    0,  197,    0,   45,   65,    0,   46,
   47,   48,   49,   50,   51,   52,   28,   28,    0,   36,
    0,  205,    0,   37,    0,    0,    0,    0,   53,    0,
    0,    0,   54,   68,   68,   96,    0,   96,   96,   96,
   96,   96,   96,   96,   96,   96,   67,    0,   96,   96,
   96,   96,   68,    0,    0,    0,    0,   96,   96,   96,
   96,   96,   64,    0,   37,  131,    0,  131,    0,    0,
    0,  244,  131,  131,  131,  131,    0,    0,  131,  131,
  131,  131,    0,   37,    0,   36,    0,  131,  131,  131,
  133,  131,  133,    0,    0,   41,    0,  133,  133,  133,
  133,   28,    0,  133,  133,  133,  133,  262,    0,    0,
    0,  268,  133,  133,  133,  130,  133,  130,   68,    0,
    0,    0,  130,  130,  130,  130,    0,    0,  130,  130,
  130,  130,    0,    0,   41,    0,    0,  130,  130,  130,
   65,  130,    0,    0,    0,    0,    0,   65,   65,   37,
    0,   65,   65,   65,   65,    0,   37,    0,    0,    0,
   15,   65,   65,   37,   37,    0,    0,   37,   37,   37,
   37,    0,    0,    0,    0,    0,    0,   37,   37,   67,
  173,    0,    0,    0,    0,    0,   67,   67,    0,    0,
   67,   67,   67,   67,    0,   64,   41,    0,    0,    0,
   67,   67,   64,   64,    0,    0,   64,   64,   64,   64,
    0,    0,    0,    0,   41,    0,   64,   64,   36,  196,
    0,    0,    0,    0,    0,   36,   36,    0,    0,   36,
   36,   36,   36,   41,   28,    0,    0,    0,    0,   36,
   36,   28,   28,   14,    0,   28,   28,   28,   28,   41,
    0,   68,    0,    0,    0,   28,   28,   38,   38,   68,
    0,    0,   68,   68,   68,   68,    0,    0,    0,   38,
   38,   38,   68,   68,    0,    0,   41,    0,    0,    0,
    0,  229,   37,    0,    0,    0,   90,    0,    0,   37,
   37,   38,    0,   37,   37,   37,   37,   38,    0,  237,
    0,    0,   41,   37,   37,    0,    0,    0,    0,    0,
    0,    0,    0,    6,   41,  140,    0,    0,   85,    0,
    7,  144,    0,    0,    9,  145,   10,  146,    0,    0,
   41,    0,   95,    0,   12,   13,    0,   38,   38,    0,
    0,    0,    0,    0,  143,    0,   38,    0,   41,    0,
   38,   38,    6,    0,    0,    0,    0,    0,    0,    7,
  144,  100,    0,    9,  145,   10,  146,   38,    0,    0,
    0,   41,    0,   12,   13,    0,    0,    0,    6,    0,
    0,    0,    0,   41,    0,    7,    8,  139,    0,    9,
   38,   10,   11,    0,    0,    0,    0,    0,    0,   12,
   13,   41,   38,    0,    0,    0,    0,   38,    0,    0,
    0,    0,   38,   41,    6,    0,    0,    0,    0,    0,
    0,    7,  144,    0,    0,    9,  145,   10,  146,   38,
    0,    0,    6,  158,    0,   12,   13,   41,    0,    7,
  144,    0,    0,    9,  145,   10,  146,    0,    0,   41,
    0,    6,   38,   12,   13,    0,  173,   38,    7,    8,
   38,    0,    9,    0,   10,   11,    0,    6,  196,    0,
    0,    0,   12,   13,    7,    8,    0,    0,    9,    0,
   10,   11,    0,    0,    0,    0,  217,    0,   12,   13,
    0,    0,    0,   38,    6,    0,    0,   38,  229,   38,
    0,    7,    8,    0,   57,    9,   56,   10,   11,    0,
    0,    0,    0,    0,    0,   12,   13,   38,    0,  117,
    6,  118,  237,   38,    0,    0,   38,    7,    8,    0,
    0,    9,    6,   10,   11,    0,   57,    0,   56,    7,
    8,   12,   13,    9,    0,   10,   11,    0,    6,    0,
    0,    0,    0,   12,   13,    7,  144,    0,    0,    9,
  145,   10,  146,    0,    0,    0,    6,    0,    0,   12,
   13,    0,    0,    7,    8,    0,    0,    9,    0,   10,
   11,    0,    0,    0,    0,    0,    0,   12,   13,    6,
    0,    0,    0,    0,    0,    0,    7,    8,    0,    0,
    9,    6,   10,   11,    0,    0,    0,    0,    7,    8,
   12,   13,    9,    0,   10,   11,    0,    0,    0,    6,
    0,    0,   12,   13,    0,    0,    7,    8,  101,    0,
    9,    6,   10,   11,    0,    0,    0,    0,    7,    8,
   12,   13,    9,    0,   10,   11,    0,    0,    0,    0,
    0,    0,   12,   13,    0,    6,    0,    0,  127,  130,
    0,    0,    7,    8,    0,    0,    9,    6,   10,   11,
    0,    0,    0,  127,    7,    8,   12,   13,    9,    0,
   10,   11,    0,    0,    0,    0,    0,    0,   12,   13,
    0,    0,  165,  166,    0,  168,    0,    0,    0,    0,
   45,    0,    0,   46,   47,   48,   49,   50,   51,   52,
    0,  189,    0,    0,  190,    0,    0,    0,    0,    0,
    0,    0,   53,    0,    0,    0,   54,  189,    0,    0,
    0,    0,  116,    0,    0,   46,   47,   48,   49,   50,
   51,   52,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  168,    0,    0,    0,    0,    0,   54,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  231,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  127,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  168,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  189,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         16,
    0,   43,   40,   45,   44,   44,  147,   41,   58,   40,
   88,  123,   40,  263,   41,  263,   40,   46,   41,   44,
   41,   61,   61,   41,  274,   42,  274,    0,   41,  258,
   43,   41,   45,   40,   44,   61,   62,  265,   44,  263,
   40,   41,   42,   43,   44,   45,  123,   47,  238,  273,
  279,  280,  242,   41,    0,   43,   59,   45,   42,   59,
   60,   61,   62,   47,  205,   82,   83,   40,   41,  265,
   43,   44,   45,   41,   46,  256,   44,  103,  104,   96,
  123,  271,  263,  264,  265,  265,   59,   60,    0,   62,
   41,  117,  118,  273,   40,   41,  123,   43,   44,   45,
  123,  282,  123,  267,  268,  123,   42,  263,  264,  265,
  256,   47,  263,   59,   60,    0,   62,  263,  264,  265,
   41,  262,  200,  123,  123,  125,  282,  268,   40,   41,
  147,   43,   44,   45,   42,   43,  282,   45,   41,   47,
  157,   44,    0,   41,  256,  265,   44,   59,   60,  199,
   62,   41,  125,  265,   44,   40,   41,  174,   43,   44,
   45,   41,   41,  265,   44,   44,  263,  264,  265,   43,
  264,   45,  266,   40,   59,   60,    0,   62,   40,  125,
  197,   41,   40,   41,  256,   43,   44,   45,  205,  256,
  257,  263,  264,  265,  276,  277,  263,  264,  265,  267,
  268,   59,   60,  274,   62,   41,  278,  267,  268,  274,
  282,  278,   41,  125,  264,  282,   40,   41,  256,   43,
   44,   45,  256,   41,  258,  265,  265,  244,  257,  263,
  264,  265,   40,  258,  265,   59,   60,  265,   62,  281,
  125,  265,  273,  256,  278,  262,   41,   59,  282,   44,
  125,  268,  258,   40,  279,  280,  256,  282,  258,  259,
  260,  261,  262,  263,  264,  265,  266,  125,  256,  269,
  270,  271,  272,  279,  280,  123,  282,  263,  278,  279,
  280,  281,  282,  256,  263,  258,  259,  260,  261,  262,
  263,  264,  265,  266,  263,  256,  269,  270,  271,  272,
  123,  125,  263,  264,  265,  278,  279,  280,  281,  282,
  256,  265,  258,  259,  260,  261,  262,  263,  264,  265,
  266,  282,   41,  269,  270,  271,  272,   41,   41,   41,
   44,  265,  278,  279,  280,  281,  282,   15,  268,  263,
  265,  123,   59,    0,  256,  125,  258,  259,  260,  261,
  262,  263,  264,  265,  266,  125,   34,  269,  270,  271,
  272,  268,  268,   41,  268,  125,  278,  279,  280,  281,
  282,  256,   44,  258,  259,  260,  261,  262,  263,  264,
  265,  266,  123,  256,  269,  270,  271,  272,  123,    0,
  263,  264,  265,  278,  279,  280,  281,  282,  256,  123,
  258,  259,  260,  261,  262,  263,  264,  265,  266,  282,
  125,  269,  270,  271,  272,   93,  123,  125,  256,    0,
  278,  279,  280,  281,  282,  263,  264,  265,  125,  125,
   40,   55,  256,  144,  258,  259,  260,  261,  262,  263,
  264,  265,  266,  146,  282,  269,  270,  271,  272,    0,
   60,   77,   62,  206,  278,  279,  280,  281,  282,   40,
   41,  256,   43,   44,   45,   61,  167,  228,  263,  264,
  265,  250,   -1,  258,    0,  258,   -1,  155,   59,   60,
  265,   62,  265,  278,  258,   -1,   -1,  282,   -1,   40,
   41,  265,   -1,   44,  279,  280,  279,  280,   -1,    0,
   -1,  275,  276,  277,   -1,  279,  280,   -1,   59,   -1,
   -1,    2,    3,   -1,   40,   41,   -1,   -1,   44,   -1,
   -1,   -1,    0,   14,   15,   -1,   -1,   -1,  206,   -1,
   -1,   -1,   -1,   59,   -1,   -1,  214,   -1,    0,   40,
   41,   -1,  256,   44,  125,   36,   -1,   -1,   -1,  263,
  264,  265,  105,  106,   -1,   -1,   -1,   -1,   59,   -1,
   -1,    0,   40,   41,  278,   -1,  119,   -1,  282,  122,
  123,   -1,  250,   -1,  125,   -1,   -1,    0,   40,   41,
   -1,   59,   -1,   -1,  256,   -1,   -1,   -1,   -1,   -1,
   -1,  263,  264,  265,   -1,   -1,   -1,   59,   -1,  125,
    0,   40,   41,   -1,   95,   -1,  278,   -1,   -1,   -1,
  282,   -1,   -1,   -1,   -1,   -1,    0,   40,   41,   -1,
   59,  112,   -1,   -1,  125,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,    0,   -1,   -1,   59,   -1,   -1,   -1,
   40,   41,   -1,   -1,  135,   -1,  256,  125,   -1,  259,
  260,  261,  262,  263,  264,  265,   40,   41,   -1,   59,
   -1,  152,   -1,  125,   -1,   -1,   -1,   -1,  278,   -1,
   -1,   -1,  282,   40,   41,  256,   -1,  258,  259,  260,
  261,  262,  263,  264,  265,  266,  125,   -1,  269,  270,
  271,  272,   59,   -1,   -1,   -1,   -1,  278,  279,  280,
  281,  282,  125,   -1,   40,  256,   -1,  258,   -1,   -1,
   -1,  202,  263,  264,  265,  266,   -1,   -1,  269,  270,
  271,  272,   -1,   59,   -1,  125,   -1,  278,  279,  280,
  256,  282,  258,   -1,   -1,   40,   -1,  263,  264,  265,
  266,  125,   -1,  269,  270,  271,  272,  238,   -1,   -1,
   -1,  242,  278,  279,  280,  256,  282,  258,  125,   -1,
   -1,   -1,  263,  264,  265,  266,   -1,   -1,  269,  270,
  271,  272,   -1,   -1,   40,   -1,   -1,  278,  279,  280,
  258,  282,   -1,   -1,   -1,   -1,   -1,  265,  266,  125,
   -1,  269,  270,  271,  272,   -1,  258,   -1,   -1,   -1,
   40,  279,  280,  265,  266,   -1,   -1,  269,  270,  271,
  272,   -1,   -1,   -1,   -1,   -1,   -1,  279,  280,  258,
  125,   -1,   -1,   -1,   -1,   -1,  265,  266,   -1,   -1,
  269,  270,  271,  272,   -1,  258,   40,   -1,   -1,   -1,
  279,  280,  265,  266,   -1,   -1,  269,  270,  271,  272,
   -1,   -1,   -1,   -1,   40,   -1,  279,  280,  258,  125,
   -1,   -1,   -1,   -1,   -1,  265,  266,   -1,   -1,  269,
  270,  271,  272,   40,  258,   -1,   -1,   -1,   -1,  279,
  280,  265,  266,  123,   -1,  269,  270,  271,  272,   40,
   -1,  258,   -1,   -1,   -1,  279,  280,    2,    3,  266,
   -1,   -1,  269,  270,  271,  272,   -1,   -1,   -1,   14,
   15,   16,  279,  280,   -1,   -1,   40,   -1,   -1,   -1,
   -1,  125,  258,   -1,   -1,   -1,   31,   -1,   -1,  265,
  266,   36,   -1,  269,  270,  271,  272,   42,   -1,  125,
   -1,   -1,   40,  279,  280,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  258,   40,   41,   -1,   -1,  125,   -1,
  265,  266,   -1,   -1,  269,  270,  271,  272,   -1,   -1,
   40,   -1,  123,   -1,  279,  280,   -1,   82,   83,   -1,
   -1,   -1,   -1,   -1,   89,   -1,   91,   -1,   40,   -1,
   95,   96,  258,   -1,   -1,   -1,   -1,   -1,   -1,  265,
  266,  125,   -1,  269,  270,  271,  272,  112,   -1,   -1,
   -1,   40,   -1,  279,  280,   -1,   -1,   -1,  258,   -1,
   -1,   -1,   -1,   40,   -1,  265,  266,  125,   -1,  269,
  135,  271,  272,   -1,   -1,   -1,   -1,   -1,   -1,  279,
  280,   40,  147,   -1,   -1,   -1,   -1,  152,   -1,   -1,
   -1,   -1,  157,   40,  258,   -1,   -1,   -1,   -1,   -1,
   -1,  265,  266,   -1,   -1,  269,  270,  271,  272,  174,
   -1,   -1,  258,  125,   -1,  279,  280,   40,   -1,  265,
  266,   -1,   -1,  269,  270,  271,  272,   -1,   -1,   40,
   -1,  258,  197,  279,  280,   -1,  125,  202,  265,  266,
  205,   -1,  269,   -1,  271,  272,   -1,  258,  125,   -1,
   -1,   -1,  279,  280,  265,  266,   -1,   -1,  269,   -1,
  271,  272,   -1,   -1,   -1,   -1,  125,   -1,  279,  280,
   -1,   -1,   -1,  238,  258,   -1,   -1,  242,  125,  244,
   -1,  265,  266,   -1,   60,  269,   62,  271,  272,   -1,
   -1,   -1,   -1,   -1,   -1,  279,  280,  262,   -1,   43,
  258,   45,  125,  268,   -1,   -1,  271,  265,  266,   -1,
   -1,  269,  258,  271,  272,   -1,   60,   -1,   62,  265,
  266,  279,  280,  269,   -1,  271,  272,   -1,  258,   -1,
   -1,   -1,   -1,  279,  280,  265,  266,   -1,   -1,  269,
  270,  271,  272,   -1,   -1,   -1,  258,   -1,   -1,  279,
  280,   -1,   -1,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   -1,   -1,   -1,   -1,   -1,  279,  280,  258,
   -1,   -1,   -1,   -1,   -1,   -1,  265,  266,   -1,   -1,
  269,  258,  271,  272,   -1,   -1,   -1,   -1,  265,  266,
  279,  280,  269,   -1,  271,  272,   -1,   -1,   -1,  258,
   -1,   -1,  279,  280,   -1,   -1,  265,  266,   43,   -1,
  269,  258,  271,  272,   -1,   -1,   -1,   -1,  265,  266,
  279,  280,  269,   -1,  271,  272,   -1,   -1,   -1,   -1,
   -1,   -1,  279,  280,   -1,  258,   -1,   -1,   73,   74,
   -1,   -1,  265,  266,   -1,   -1,  269,  258,  271,  272,
   -1,   -1,   -1,   88,  265,  266,  279,  280,  269,   -1,
  271,  272,   -1,   -1,   -1,   -1,   -1,   -1,  279,  280,
   -1,   -1,  107,  108,   -1,  110,   -1,   -1,   -1,   -1,
  256,   -1,   -1,  259,  260,  261,  262,  263,  264,  265,
   -1,  126,   -1,   -1,  129,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  278,   -1,   -1,   -1,  282,  142,   -1,   -1,
   -1,   -1,  256,   -1,   -1,  259,  260,  261,  262,  263,
  264,  265,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  167,   -1,   -1,   -1,   -1,   -1,  282,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  188,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  200,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  228,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  241,
};
}
final static short YYFINAL=4;
final static short YYMAXTOKEN=284;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"TWO_POINTS_ASSIGNATION","STRING",
"GREATER_OR_EQUAL","LESS_OR_EQUAL","EQUAL","NOT_EQUAL","CTE_INT","CTE_FLOAT",
"ID","IF","ELSE","ENDIF","PRINT","RETURN","VAR","FOR","FROM","TO","CR","SE",
"LE","TOI","INT","FLOAT","ARROW","FUN","CTE_INT_NEGATIVE",
"SENTENCIA_ASIGNACION_PREC",
};
final static String yyrule[] = {
"$accept : prog",
"prog : ID '{' cuerpo '}'",
"prog : prog_error",
"prog_error : '{' cuerpo '}'",
"prog_error : ID '(' cuerpo ')'",
"prog_error : ID cuerpo",
"prog_error : ID '{' cuerpo",
"prog_error : ID cuerpo '}'",
"prog_error : error",
"cuerpo : cuerpo sentencia",
"cuerpo : sentencia",
"sentencia : sentencia_declarativa",
"sentencia : sentencia_ejecucion ';'",
"sentencia : sentencia_ejecucion_sin_coma",
"sentencia_declarativa : funcion",
"sentencia_ejecucion : sentencia_print",
"sentencia_ejecucion : sentencia_seleccion",
"sentencia_ejecucion : sentencia_iteracion",
"sentencia_ejecucion : sentencia_asignacion",
"sentencia_ejecucion_retorno : sentencia_seleccion_retorno",
"sentencia_ejecucion_retorno : sentencia_iteracion_retorno",
"sentencia_seleccion_retorno : IF parametros_seleccion cuerpo_seleccion_retorno ELSE cuerpo_seleccion_retorno ENDIF",
"sentencia_seleccion_retorno : IF parametros_seleccion cuerpo_seleccion_retorno ELSE cuerpo_seleccion ENDIF",
"sentencia_seleccion_retorno : IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion_retorno ENDIF",
"sentencia_seleccion_retorno : IF parametros_seleccion cuerpo_seleccion_retorno ENDIF",
"cuerpo_seleccion_retorno : '{' cuerpo_funcion '}'",
"sentencia_iteracion_retorno : FOR parametros_iteracion cuerpo_iteracion_retorno",
"cuerpo_iteracion_retorno : '{' cuerpo_funcion '}'",
"sentencia_ejecucion_sin_coma : sentencia_ejecucion",
"sentencia_print : PRINT '(' STRING ')'",
"sentencia_print : PRINT '(' lista_exp_aritmeticas ')'",
"sentencia_print : sentencia_print_error",
"sentencia_print_error : PRINT '(' ')'",
"sentencia_seleccion : IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion ENDIF",
"sentencia_seleccion : IF parametros_seleccion cuerpo_seleccion ENDIF",
"sentencia_seleccion : sentencia_seleccion_sin_endif",
"sentencia_seleccion_sin_endif : IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion",
"sentencia_seleccion_sin_endif : IF parametros_seleccion cuerpo_seleccion",
"parametros_seleccion : '(' condicion ')'",
"parametros_seleccion : parametros_seleccion_error",
"parametros_seleccion_error : condicion ')'",
"parametros_seleccion_error : '(' condicion",
"parametros_seleccion_error : condicion",
"cuerpo_seleccion : '{' cuerpo '}'",
"cuerpo_seleccion : cuerpo_seleccion_error",
"cuerpo_seleccion_error : '{' '}'",
"sentencia_iteracion : FOR parametros_iteracion cuerpo_iteracion",
"parametros_iteracion : '(' encabezado_iteracion ')'",
"parametros_iteracion : parametros_iteracion_error",
"parametros_iteracion_error : encabezado_iteracion ')'",
"parametros_iteracion_error : '(' encabezado_iteracion",
"parametros_iteracion_error : encabezado_iteracion",
"encabezado_iteracion : ID FROM CTE_INT TO CTE_INT",
"encabezado_iteracion : encabezado_iteracion_error",
"encabezado_iteracion_error : FROM CTE_INT TO CTE_INT",
"encabezado_iteracion_error : ID CTE_INT TO CTE_INT",
"encabezado_iteracion_error : ID FROM TO CTE_INT",
"encabezado_iteracion_error : ID FROM CTE_INT CTE_INT",
"encabezado_iteracion_error : ID FROM CTE_INT TO",
"cuerpo_iteracion : '{' cuerpo '}'",
"cuerpo_iteracion : cuerpo_iteracion_error",
"cuerpo_iteracion_error : '{' '}'",
"sentencia_asignacion : sentencia_asignacion_unaria",
"sentencia_asignacion : sentencia_asignacion_multiple",
"sentencia_asignacion_unaria : VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica",
"sentencia_asignacion_unaria : ID TWO_POINTS_ASSIGNATION expresion_aritmetica",
"sentencia_asignacion_unaria : sentencia_asignacion_unaria_error",
"sentencia_asignacion_unaria_error : VAR ID expresion_aritmetica",
"sentencia_asignacion_multiple : lista_variables '=' lista_exp_aritmeticas",
"funcion : encabezado_funcion '{' cuerpo_funcion '}'",
"funcion : sentencia_lambda",
"encabezado_funcion : lista_tipos FUN ID '(' lista_param_formales ')'",
"encabezado_funcion : encabezado_funcion_error",
"encabezado_funcion_error : lista_tipos FUN '(' lista_param_formales ')' '{' cuerpo_funcion '}'",
"cuerpo_funcion : cuerpo sentencia_funcion",
"cuerpo_funcion : sentencia_funcion cuerpo",
"cuerpo_funcion : cuerpo sentencia_funcion cuerpo",
"cuerpo_funcion : sentencia_funcion cuerpo sentencia_funcion",
"cuerpo_funcion : sentencia_funcion",
"sentencia_funcion : sentencia_ejecucion_retorno ';'",
"sentencia_funcion : sentencia_retorno",
"sentencia_retorno : RETURN '(' lista_exp_aritmeticas ')' ';'",
"sentencia_lambda : parametro_lambda cuerpo_lambda argumento_lambda",
"parametro_lambda : '(' tipo ID ')'",
"cuerpo_lambda : '{' cuerpo '}'",
"cuerpo_lambda : cuerpo_lambda_error",
"cuerpo_lambda_error : cuerpo '}'",
"argumento_lambda : '(' ID ')'",
"argumento_lambda : '(' CTE_INT ')'",
"argumento_lambda : '(' CTE_FLOAT ')'",
"condicion : expresion_aritmetica simbolo_comparador factor",
"condicion : condicion_error",
"condicion_error : expresion_aritmetica termino",
"condicion_error : expresion_aritmetica simbolo_comparador",
"condicion_error : simbolo_comparador termino",
"expresion_aritmetica : expresion_aritmetica '+' termino",
"expresion_aritmetica : expresion_aritmetica '-' termino",
"expresion_aritmetica : expresion_aritmetica_toi",
"expresion_aritmetica : termino",
"expresion_aritmetica : expresion_aritmetica_error",
"expresion_aritmetica_error : error '+' termino",
"expresion_aritmetica_error : error '-' termino",
"expresion_aritmetica_error : expresion_aritmetica '+' error",
"expresion_aritmetica_error : expresion_aritmetica '-' error",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : factor",
"termino : termino_error",
"termino_error : termino '*' error",
"termino_error : termino '/' error",
"termino_error : error '*' factor",
"termino_error : error '/' factor",
"factor : CTE_INT",
"factor : CTE_FLOAT",
"factor : invocacion_funcion",
"factor : variable",
"invocacion_funcion : FUN ID '(' lista_param_reales ')'",
"invocacion_funcion : invocacion_funcion_error",
"invocacion_funcion_error : FUN '(' lista_param_reales ')'",
"expresion_aritmetica_toi : TOI '(' expresion_aritmetica ')'",
"expresion_aritmetica_toi : expresion_aritmetica_toi_error",
"expresion_aritmetica_toi_error : TOI error expresion_aritmetica ')'",
"expresion_aritmetica_toi_error : TOI '(' expresion_aritmetica error",
"expresion_aritmetica_toi_error : TOI error expresion_aritmetica error",
"simbolo_comparador : GREATER_OR_EQUAL",
"simbolo_comparador : LESS_OR_EQUAL",
"simbolo_comparador : EQUAL",
"simbolo_comparador : NOT_EQUAL",
"simbolo_comparador : '>'",
"simbolo_comparador : '<'",
"lista_exp_aritmeticas : lista_exp_aritmeticas ',' expresion_aritmetica",
"lista_exp_aritmeticas : expresion_aritmetica",
"lista_exp_aritmeticas : lista_exp_aritmeticas_error",
"lista_exp_aritmeticas_error : lista_exp_aritmeticas expresion_aritmetica",
"lista_tipos : lista_tipos ',' tipo",
"lista_tipos : tipo",
"lista_tipos : lista_tipos_error",
"lista_tipos_error : lista_tipos tipo",
"lista_param_formales : lista_param_formales ',' parametro_formal",
"lista_param_formales : parametro_formal",
"lista_variables : lista_variables ',' variable",
"lista_variables : variable",
"lista_variables : lista_variables_error",
"lista_variables_error : lista_variables variable",
"variable : ID",
"variable : ID '.' ID",
"tipo : INT",
"tipo : FLOAT",
"tipo : STRING",
"parametro_formal : semantica_pasaje tipo ID",
"parametro_formal : tipo ID",
"parametro_formal : parametro_formal_error",
"parametro_formal_error : semantica_pasaje tipo",
"parametro_formal_error : tipo",
"parametro_formal_error : semantica_pasaje ID",
"parametro_formal_error : ID",
"lista_param_reales : lista_param_reales ',' parametro_real",
"lista_param_reales : parametro_real",
"semantica_pasaje : CR SE",
"semantica_pasaje : CR LE",
"semantica_pasaje : semantica_pasaje_error",
"semantica_pasaje_error : CR",
"semantica_pasaje_error : SE",
"semantica_pasaje_error : LE",
"parametro_real : expresion_aritmetica ARROW ID",
"parametro_real : parametro_real_error",
"parametro_real_error : expresion_aritmetica ARROW",
};

//#line 461 "grammar.y"

private static int yylval_recognition = 0;

static AnalizadorLexico lex = null;
static Parser par = null;
static Cursor cursor = null;
static Integer numTerceto = 0;
static String ambito = null;

private String getReferencia(ParserVal val) {
    if (val.obj != null) {
        // Es un Terceto, usamos su número de referencia
        return ((Terceto) val.obj).getNumTerceto();
    } else {
        // Es un valor simple (ID, CTE_INT, etc.), usamos su lexema
        return val.sval;
    }
}

private ParserVal crearTerceto(ParserVal operando1, ParserVal operador, ParserVal operando2) {

    String opIzquierdo = getReferencia(operando1);
    String opDerecho = getReferencia(operando2);

    //System.out.println("op1 " + operando1.obj);
    //System.out.println("op2 " + operando2.obj);
    String op = operador.sval;
    numTerceto += 1;
    Terceto nuevoTerceto = new Terceto(numTerceto,op, opIzquierdo, opDerecho);
    Logger.logTerceto(cursor.getCurrentLine(), nuevoTerceto);


    ParserVal resultado = new ParserVal();
    resultado.obj = nuevoTerceto;
    resultado.sval = null;
    return resultado;
}

public static void main (String [] args) {

    System.out.println("Iniciando compilación ... ");
    Scanner lector = new Scanner(System.in);
    System.out.println("Usted se encuentra en: " + System.getProperty("user.dir"));
    System.out.println("Ingrese el archivo deseado, este debe estar dentro de data");
    String programa = lector.nextLine();
    lector.close();

    TablaPalabrasReservadas tablaPalabrasReservadas = new TablaPalabrasReservadas();
    tablaPalabrasReservadas.cargarTabla();

    lex = new AnalizadorLexico (programa) ;
    cursor = lex.getCursor();
    par = new Parser (false);

    par.run () ;

    System.out.println("TablaSimbolos: " + TablaSimbolos.TABLA_SIMBOLOS);
    System.out.println(Logger.generateLog());

    System.out.println("\nFin compilación");
}


int yylex() {
    Pair<String, Integer> t = lex.generarToken();
    String lexema = t.getFirst();
    Integer token = t.getSecond();
    if (lexema != null){
        yylval = new ParserVal(lexema);
        yylval_recognition += 1;
    }
    return token;
}

void yyerror (String s) {
    System.out.println(s);
}
//#line 860 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 33 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia PROG");
            ambito = val_peek(3).sval;}
break;
case 3:
//#line 40 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre del programa");}
break;
case 4:
//#line 41 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Debe indicar el programa entre {}");}
break;
case 5:
//#line 42 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Faltan los delimitadores de programa");}
break;
case 6:
//#line 43 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '}'");}
break;
case 7:
//#line 44 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '{'");}
break;
case 8:
//#line 45 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Hay errores lexicos o sintaticos no identificados");}
break;
case 14:
//#line 60 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");}
break;
case 15:
//#line 64 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia PRINT");}
break;
case 21:
//#line 76 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
break;
case 22:
//#line 77 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
break;
case 23:
//#line 78 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
break;
case 24:
//#line 79 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");}
break;
case 26:
//#line 89 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia ITERACION");}
break;
case 28:
//#line 98 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
break;
case 32:
//#line 108 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
break;
case 33:
//#line 112 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
break;
case 34:
//#line 113 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");}
break;
case 35:
//#line 114 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
break;
case 38:
//#line 123 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia CONDICION");}
break;
case 40:
//#line 128 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
break;
case 41:
//#line 129 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
break;
case 42:
//#line 130 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
break;
case 45:
//#line 139 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}
break;
case 46:
//#line 142 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia ITERACION");}
break;
case 49:
//#line 151 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de iteracion");}
break;
case 50:
//#line 152 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de iteracion");}
break;
case 51:
//#line 153 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de iteracion");}
break;
case 54:
//#line 162 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado ID del for");}
break;
case 55:
//#line 163 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado FROM del for");}
break;
case 56:
//#line 164 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_1 del for");}
break;
case 57:
//#line 165 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado TO del for");}
break;
case 58:
//#line 166 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_2 del for");}
break;
case 61:
//#line 175 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
break;
case 62:
//#line 179 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION UNARIA");}
break;
case 63:
//#line 180 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION MULTIPLE");}
break;
case 64:
//#line 184 "grammar.y"
{
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(val_peek(2).sval)) {
                Logger.logError(cursor.getCurrentLine(), "Redeclaracion de variable");}
            else {
                val_peek(2).sval=val_peek(2).sval + ".INT." + ambito;
                TablaSimbolos.TABLA_SIMBOLOS.put(val_peek(2).sval,new Info(val_peek(2).sval, "CTE_INT", "INT", "Variable", ambito));
                /*System.out.println(TablaSimbolos.TABLA_SIMBOLOS.get($2.sval));*/
            };
        yyval = crearTerceto(val_peek(2), val_peek(1), val_peek(0));
        }
break;
case 65:
//#line 194 "grammar.y"
{
            if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey(val_peek(2).sval))) {
                Logger.logError(cursor.getCurrentLine(), "Variable sin declarar");
            };
            yyval = crearTerceto(val_peek(2), val_peek(1), val_peek(0));

        }
break;
case 67:
//#line 205 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de asignacion luego de var");}
break;
case 68:
//#line 209 "grammar.y"
{if (val_peek(2).ival != val_peek(0).ival) {Logger.logError(cursor.getCurrentLine(), "La cantidad de variables (" + val_peek(2).ival + ") no coincide con la cantidad de expresiones (" + val_peek(0).ival + ") en la asignación múltiple.");}}
break;
case 69:
//#line 213 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");}
break;
case 70:
//#line 214 "grammar.y"
{Logger.logRule(cursor.getCurrentLine(), "Sentencia LAMBDA");}
break;
case 71:
//#line 220 "grammar.y"
{if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(val_peek(3).sval)) {
            Logger.logError(cursor.getCurrentLine(), "Redeclaracion de funcion");
        } else {
            System.out.println(val_peek(5).sval);
            /*TablaSimbolos.TABLA_SIMBOLOS.put($3.sval,new Info($3.sval,$1, ));*/
        };}
break;
case 73:
//#line 229 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de nombre en declaracion de funcion");}
break;
case 86:
//#line 263 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta delimitador izquierdo '{' del cuerpo lambda");}
break;
case 92:
//#line 279 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
break;
case 93:
//#line 280 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
break;
case 94:
//#line 281 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
break;
case 95:
//#line 285 "grammar.y"
{

            yyval = crearTerceto(val_peek(2), val_peek(1), val_peek(0));
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
break;
case 96:
//#line 290 "grammar.y"
{
            yyval = crearTerceto(val_peek(2), val_peek(1), val_peek(0));

            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
break;
case 97:
//#line 295 "grammar.y"
{
            yyval = val_peek(0);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia TOI");
        }
break;
case 98:
//#line 299 "grammar.y"
{
            yyval=val_peek(0);
        }
break;
case 100:
//#line 306 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '+''");}
break;
case 101:
//#line 307 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '-'");}
break;
case 102:
//#line 308 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '+'");}
break;
case 103:
//#line 309 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 104:
//#line 313 "grammar.y"
{
            yyval = crearTerceto(val_peek(2), val_peek(1), val_peek(0));
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
break;
case 105:
//#line 317 "grammar.y"
{

            yyval = crearTerceto(val_peek(2), val_peek(1), val_peek(0));
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
break;
case 106:
//#line 322 "grammar.y"
{
            yyval = val_peek(0);
        }
break;
case 108:
//#line 329 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '*'");}
break;
case 109:
//#line 330 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
break;
case 110:
//#line 331 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
break;
case 111:
//#line 332 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 112:
//#line 336 "grammar.y"
{
            yyval=val_peek(0);
        }
break;
case 113:
//#line 339 "grammar.y"
{
            yyval=val_peek(0);
        }
break;
case 114:
//#line 342 "grammar.y"
{
            Logger.logRule(cursor.getCurrentLine(), "Sentencia INVOCACION FUNCION");
        }
break;
case 115:
//#line 345 "grammar.y"
{
            yyval=val_peek(0);
        }
break;
case 116:
//#line 352 "grammar.y"
{if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey(val_peek(3).sval))) {
                Logger.logError(cursor.getCurrentLine(), "Funcion sin declarar");};}
break;
case 118:
//#line 357 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion en invocacion de funcion");}
break;
case 121:
//#line 365 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en expresion_aritmetica TOI");}
break;
case 122:
//#line 366 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en expresion_aritmetica TOI");}
break;
case 123:
//#line 367 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en expresion_aritmetica TOI");}
break;
case 130:
//#line 380 "grammar.y"
{yyval.ival = val_peek(2).ival + 1;}
break;
case 131:
//#line 381 "grammar.y"
{yyval.ival = 1;}
break;
case 133:
//#line 386 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de expresiones aritmeticas (lado derecho)");}
break;
case 134:
//#line 390 "grammar.y"
{yyval.sval = val_peek(2).sval + (",") + val_peek(0).sval;}
break;
case 135:
//#line 391 "grammar.y"
{yyval.sval = val_peek(0).sval;}
break;
case 137:
//#line 395 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de tipos");}
break;
case 140:
//#line 403 "grammar.y"
{yyval.ival = val_peek(2).ival + 1;}
break;
case 141:
//#line 404 "grammar.y"
{yyval.ival = 1; }
break;
case 143:
//#line 409 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
break;
case 144:
//#line 413 "grammar.y"
{yyval.sval=val_peek(0).sval;}
break;
case 146:
//#line 418 "grammar.y"
{yyval.sval = "INT";}
break;
case 147:
//#line 419 "grammar.y"
{yyval.sval = "FLOAT";}
break;
case 152:
//#line 430 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 153:
//#line 431 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 154:
//#line 432 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 155:
//#line 433 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 161:
//#line 448 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de LE o SE despues de CR");}
break;
case 162:
//#line 449 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de SE");}
break;
case 163:
//#line 450 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de LE");}
break;
case 166:
//#line 458 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta especificacion del parametro formal");}
break;
//#line 1429 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
