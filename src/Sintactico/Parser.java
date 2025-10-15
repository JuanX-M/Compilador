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
    import java.util.Scanner;

    import Lexico.AnalizadorLexico;

    import Tools.TablaSimbolos;
    import Tools.Pair;
    import Tools.TablaPalabrasReservadas;
    import Tools.Logger;
    import Tools.Cursor;

//#line 28 "Parser.java"




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
   31,   11,   11,   32,   32,   35,   33,    7,    7,    7,
   37,   18,   18,   18,   18,   18,   42,   42,   43,   39,
   38,   44,   45,   45,   48,   46,   46,   46,   24,   24,
   51,   51,   51,   34,   34,   34,   34,   34,   54,   54,
   54,   54,   52,   52,   52,   52,   55,   55,   55,   55,
   50,   50,   50,   50,   53,   53,   58,   58,   58,   49,
   49,   49,   49,   49,   49,   21,   21,   21,   59,   40,
   40,   40,   60,   41,   41,   36,   36,   36,   62,   57,
   57,   47,   47,   47,   61,   61,   61,   64,   64,   64,
   64,   56,   56,   63,   63,   63,   66,   66,   66,   65,
   65,   67,
};
final static short yylen[] = {                            2,
    4,    1,    3,    4,    2,    3,    3,    1,    2,    1,
    1,    2,    1,    1,    1,    1,    1,    1,    1,    1,
    6,    6,    6,    4,    3,    3,    3,    1,    4,    4,
    1,    3,    6,    4,    1,    5,    3,    3,    1,    2,
    2,    1,    3,    1,    2,    3,    3,    1,    2,    2,
    1,    5,    1,    4,    4,    4,    4,    4,    3,    1,
    2,    1,    1,    4,    1,    3,    3,    4,    1,    1,
    6,    2,    2,    3,    3,    1,    1,    1,    5,   13,
    3,    4,    3,    1,    2,    3,    3,    3,    3,    1,
    2,    2,    2,    3,    3,    1,    1,    1,    3,    3,
    3,    3,    3,    3,    1,    1,    3,    3,    3,    3,
    1,    1,    5,    1,    4,    1,    4,    4,    4,    1,
    1,    1,    1,    1,    1,    3,    1,    1,    2,    3,
    1,    1,    2,    3,    1,    3,    1,    1,    2,    1,
    3,    1,    1,    1,    3,    2,    1,    2,    1,    2,
    1,    3,    1,    2,    2,    1,    1,    1,    1,    3,
    1,    2,
};
final static short yydefred[] = {                         0,
    8,    0,    0,    0,    2,  144,    0,    0,    0,    0,
    0,  142,  143,    0,    0,    0,   10,   11,    0,   13,
   14,   15,   16,   17,   18,   31,   35,   62,   63,   65,
    0,    0,   69,   70,    0,    0,  131,  137,  132,  138,
    0,    0,    0,    0,  120,  121,  122,  123,  111,  112,
    0,    0,    0,  124,  125,    0,    0,   39,    0,    0,
  105,   90,    0,   96,   98,  106,  114,  116,    0,    0,
    0,    0,    0,    0,    0,   48,   53,    0,    0,    0,
    7,    9,   12,    0,    0,  139,    0,    0,    0,  133,
    0,    0,    0,   84,    0,    3,  141,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   44,   40,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   32,    0,
    0,  128,    0,    0,    0,    0,    0,    0,    0,   46,
   60,   49,    1,    4,    0,    0,  136,    0,    0,    0,
    0,   77,   19,   20,    0,    0,   78,    0,    0,  130,
    0,   85,    0,   81,    0,    0,  109,  110,    0,    0,
    0,   38,   45,    0,    0,   34,    0,    0,    0,    0,
   89,  107,  103,  108,  104,   29,   30,    0,    0,    0,
    0,    0,    0,    0,   47,   61,    0,   82,    0,    0,
    0,    0,   68,    0,    0,  151,    0,  158,  159,    0,
    0,  135,    0,  147,  156,   83,    0,    0,    0,  119,
  117,  118,  115,    0,    0,  153,  161,   43,    0,    0,
   55,   57,    0,   56,   54,   59,    0,    0,    0,    0,
    0,   26,    0,   75,    0,  154,  155,    0,    0,  146,
  150,    0,   87,   88,   86,    0,  113,    0,   33,   52,
    0,    0,    0,   24,    0,    0,    0,    0,   71,    0,
  134,  145,  160,  152,   25,    0,    0,    0,   79,   27,
    0,   21,   22,   23,    0,    0,    0,    0,    0,   80,
};
final static short yydgoto[] = {                          4,
   16,    5,   17,   18,   19,   20,   21,   22,   23,   24,
   25,  142,  143,  144,   56,  228,  219,  145,   74,  232,
  120,   26,   27,   57,   58,  108,  130,   75,   76,   77,
  131,   28,   29,  121,   30,   31,   32,   33,   34,   35,
  200,  146,  147,   36,   93,  154,   37,   94,   60,   61,
   62,   63,   64,   65,   66,  215,   67,   68,  122,   39,
  202,   40,  203,  204,  216,  205,  217,
};
final static short yysindex[] = {                      -115,
    0,  389,  695,    0,    0,    0,  -28,  -33,   -4, -216,
  -25,    0,    0,  695,  695,  910,    0,    0,    8,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  -40,  -49,    0,    0,  -24,  743,    0,    0,    0,    0,
 -242,  929, -179,  123,    0,    0,    0,    0,    0,    0,
  -37, -163, 1124,    0,    0,    4,   90,    0, 1093, -144,
    0,    0,   23,    0,    0,    0,    0,    0,  264, -181,
 -223, -117, -188,   29,  117,    0,    0,  947,  325,  -98,
    0,    0,    0, -231,  -91,    0,  656,  -30, -242,    0,
  695,  963,  141,    0,  -98,    0,    0, -144, -144, -172,
 -172, -231, -231,  145,  150,  980,  -68,    0,    0,   81,
  -92,  213, -172,   23,   23,  242,  291,  154,    0,  287,
   99,    0, -231,   99,  -64, -257,  -22,  205,  992,    0,
    0,    0,    0,    0,  209,  189,    0,  -33,  238,  -25,
  656,    0,    0,    0,  170,  695,    0,  245, -140,    0,
 1019,    0,   75,    0,   23,   23,    0,    0,  -32,   13,
 -231,    0,    0, 1036,    4,    0,   81,   23,   81,   23,
    0,    0,    0,    0,    0,    0,    0, -231,   99,   99,
   33, -239,   40,   49,    0,    0, 1052,    0,  199, -231,
  200,  695,    0,  656, -140,    0,  -61,    0,    0,  138,
   65,    0, -201,    0,    0,    0,  303,  305,  315,    0,
    0,    0,    0,  -43,  148,    0,    0,    0,   94,   99,
    0,    0,  100,    0,    0,    0,  772,   74,  122,  375,
  861,    0,  695,    0,  160,    0,    0,  234, -140,    0,
    0,  108,    0,    0,    0,  126,    0, -231,    0,    0,
  877,  239,  199,    0,  199,  333,  893,  268,    0,  695,
    0,    0,    0,    0,    0,  132,  142,  149,    0,    0,
 1068,    0,    0,    0,  156,  371, -231,  618,  359,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    1,    0,    0,    0,
    0,    0,    0,    0,    0,  427,    0,    0,  343,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  307,    0,    0,    0,
    0,    0,   28,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  308,    0,    0,  428,    0,   -5,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  309,    0,  523,    0,    0,    0,
    0,    0,  -29,  -19,  -18,    0,    0,    0,    0,    0,
  450,    0,    0,  539,    0,    0,    0,  311,    0,    0,
    0,    0,    0,    0,    0,  593,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  319,    0,    0,    0,    0,
    0,    0,    0,    0,   55,   89,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  116,  143,  177,  420,
    0,    0,    0,    0,    0,    0,    0,    0,  475,  562,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  326,    0,  337,    0,    0, -199,    0,    0,    0,
  210,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  578,  500,
    0,    0,  -15,    0,    0,    0,    0,    0,  795,    0,
    0,    0,  341,    0,    0,    0,    0,    0,    0,    0,
    0,  260,    0,    0,    0,  288,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   16,    0,  798,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  332,  -11,  -42, -118,  334,    0,
  -79,    0,    0,  419,    0,    0,    0,  400,    0,    0,
    0,    0,    0,  768,    0,    0,    0,    0,    0,    0,
  286,  -88,    0,    0,    0,    0, 1176,    0,  424,   96,
    0,   95,    0,    0,    0,    0,  853,    0,    0,    0,
  246,    0,    0,    0,  236,    0,    0,
};
final static int YYTABLESIZE=1415;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        111,
  140,  112,  103,   85,  136,  182,   53,    3,  211,  149,
  111,   92,  112,  107,   73,    6,  183,   43,   42,   89,
   84,   91,   93,  222,   44,   58,   55,   97,   54,   78,
   79,   49,   50,    7,  223,   69,   12,   13,  131,  125,
  140,  140,  140,  140,  140,  140,   51,  140,   70,  126,
   52,   92,  192,  213,   99,  111,    6,  112,  157,  140,
  140,  140,  140,  241,  116,  157,   83,   97,   97,  117,
   97,   97,   97,   87,   44,  123,   71,   12,   13,  157,
  157,   49,   50,    7,   72,   97,   97,   97,  100,   97,
   49,   50,    7,   92,   99,   99,   51,   99,   99,   99,
   52,  104,  141,   91,   93,  234,  151,   58,  252,   52,
  230,  110,  258,   99,   99,  101,   99,    6,   49,   50,
    7,  164,  100,  140,  196,  140,  106,  101,  100,  100,
  109,  100,  100,  100,  197,  198,  199,   52,   12,   13,
    1,  111,   94,  112,  187,  127,  229,  100,  100,    2,
  100,  129,   97,  114,  115,  101,  101,  132,  101,  101,
  101,  194,  192,  167,  100,   98,  135,   99,  192,  101,
   49,   50,    7,    7,  101,  101,  102,  101,  238,   99,
  153,  239,   94,   94,  161,   94,   94,   94,  247,   52,
  162,  248,  155,  156,  176,  157,  158,  278,  165,  166,
  259,   94,   94,  239,   94,  168,  170,  233,  171,  181,
  267,  173,  175,  100,  236,  237,  102,  102,  102,  102,
  102,  102,   44,  210,    7,   45,   46,   47,   48,   49,
   50,    7,  178,    6,  148,  102,  102,  246,  102,   71,
  101,  266,  251,  268,   51,  185,  257,   72,   52,  188,
  149,  184,  131,  149,   12,   13,  140,   88,  140,  140,
  140,  140,  140,  140,  140,  140,  140,   94,  212,  140,
  140,  140,  140,  131,  131,  271,  131,  190,  140,  140,
  140,  140,  140,   97,  195,   97,   97,   97,   97,   97,
   97,   97,   97,   97,  193,  221,   97,   97,   97,   97,
  148,  102,  224,  148,  119,   97,   97,   97,   97,   97,
   99,  225,   99,   99,   99,   99,   99,   99,   99,   99,
   99,  227,  231,   99,   99,   99,   99,  177,  162,  240,
  178,  162,   99,   99,   99,   99,   99,  207,  208,  209,
  253,  254,   28,  243,  100,  244,  100,  100,  100,  100,
  100,  100,  100,  100,  100,  245,  260,  100,  100,  100,
  100,  249,  250,  265,   41,  134,  100,  100,  100,  100,
  100,  101,  262,  101,  101,  101,  101,  101,  101,  101,
  101,  101,   28,   28,  101,  101,  101,  101,  255,  166,
  263,  269,  270,  101,  101,  101,  101,  101,   94,  272,
   94,   94,   94,   94,   94,   94,   94,   94,   94,  273,
  277,   94,   94,   94,   94,  256,  274,  280,  178,   95,
   94,   94,   94,   94,   94,  276,    5,    6,   15,   42,
   51,   41,  102,   50,  102,  102,  102,  102,  102,  102,
  102,  102,  102,   76,   44,  102,  102,  102,  102,  127,
   72,   49,   50,    7,  102,  102,  102,  102,  102,   95,
   95,   73,   95,   95,   95,   74,   51,   28,  169,  189,
   52,  105,  128,  191,  129,   49,   50,    7,   95,   95,
  235,   95,  113,  264,  261,    0,    0,    0,    0,  127,
  127,    0,    0,  127,   52,    0,    0,  172,    0,  126,
    0,    0,    0,    0,   49,   50,    7,    0,  127,    0,
    0,   14,    0,    0,  129,  129,    0,    0,  129,   44,
    0,  118,   37,   52,    0,    0,   49,   50,    7,    0,
    0,    0,    0,  129,    0,    0,    0,    0,   66,  126,
  126,   51,   44,  126,   95,   52,  174,    0,    0,   49,
   50,    7,    0,   49,   50,    7,    0,    0,  126,    0,
    0,   64,   37,   37,   51,    0,    0,    0,   52,    0,
    0,    0,   52,    0,  127,    0,    0,   36,   66,   66,
    0,   37,    6,    0,    0,    0,    0,    0,    0,    7,
    8,    0,   67,    9,    0,   10,   11,   66,    0,  129,
   28,   64,   64,   12,   13,    0,    0,   28,   28,    0,
    0,   28,   28,   28,   28,    0,    0,   36,   36,    0,
   64,   28,   28,    0,  126,    0,    0,    0,    0,    0,
   44,    0,   67,   67,    0,    0,   36,   49,   50,    7,
    0,    0,    0,    0,    0,    0,    6,   37,    0,    0,
    0,   67,   51,    7,    8,    0,   52,    9,  279,   10,
   11,  178,    0,   66,    0,    0,    0,   12,   13,    0,
    0,    0,    0,    0,    0,   95,    0,   95,   95,   95,
   95,   95,   95,   95,   95,   95,   64,    0,   95,   95,
   95,   95,    0,    0,    0,   41,    0,   95,   95,   95,
   95,   95,   36,    0,    0,  127,    0,  127,    0,    0,
    0,    0,  127,  127,  127,  127,    0,   67,  127,  127,
  127,  127,    0,    0,    0,    0,    0,  127,  127,  127,
  129,  127,  129,    0,   41,    0,    0,  129,  129,  129,
  129,    0,    0,  129,  129,  129,  129,    0,    0,    0,
    0,    0,  129,  129,  129,  126,  129,  126,    0,    0,
    0,    0,  126,  126,  126,  126,    0,    0,  126,  126,
  126,  126,    0,    0,    0,   59,    0,  126,  126,  126,
   37,  126,   41,    0,    0,    0,    0,   37,   37,    0,
    0,   37,   37,   37,   37,    0,   66,    0,    0,    0,
    0,   37,   37,   66,   66,    0,    0,   66,   66,   66,
   66,   41,    0,   82,    0,    0,    0,   66,   66,   64,
   59,    0,    0,    0,    0,    0,   64,   64,    0,    0,
   64,   64,   64,   64,   37,   36,    0,  124,    0,   82,
   64,   64,   36,   36,    0,    0,   36,   36,   36,   36,
   67,    0,    0,   37,   38,   38,   36,   36,   67,    0,
    0,   67,   67,   67,   67,   91,   38,   38,   38,  159,
  160,   67,   67,   44,    0,   82,   82,    0,    0,    0,
   49,   50,    7,   86,    0,    0,    0,  179,   38,   82,
  180,    0,    0,    0,   38,   51,  163,    0,    0,   52,
   41,    0,    0,  179,    0,   59,    0,    0,    0,    0,
    0,    0,    0,    6,    0,    0,   41,    0,    0,   37,
    7,  138,    0,    0,    9,  139,   10,  140,  214,    0,
   38,   38,   41,    0,   12,   13,    0,  137,   82,   38,
    0,    0,    0,   38,   38,  220,    0,    0,   82,   41,
    0,    0,    6,    0,    0,    0,    0,    0,   38,    7,
    8,   82,    0,    9,    0,   10,   11,    0,   41,    0,
    0,    0,    0,   12,   13,    0,    0,    0,    0,    0,
    0,   38,    0,    0,   82,  186,   41,    0,    0,    0,
    0,   82,    0,   38,    0,    0,    0,  179,   38,    0,
    6,  218,   41,   38,    0,    0,    0,    7,    8,    0,
    0,    9,    0,   10,   11,  214,   38,  226,    0,   41,
    0,   12,   13,    0,    0,    0,    0,    0,    0,    6,
   82,   41,    0,    0,   81,    0,    7,  138,    0,   38,
    9,  139,   10,  140,   38,  179,   38,    0,   82,    0,
   12,   13,   37,   96,   82,    0,    0,    0,   41,   37,
   37,    0,    0,   37,   37,   37,   37,    0,   82,    0,
    0,  133,    0,   37,   37,   41,    0,    0,    0,   38,
    0,    0,    0,   38,    0,   38,    0,  152,    0,    0,
    0,   41,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   38,  163,    0,    0,   41,    0,   38,
    0,    0,   38,    0,    0,    0,  186,    0,    6,    0,
    0,    0,    0,   38,    0,    7,  138,    0,    0,    9,
  139,   10,  140,    0,    6,  111,    0,  112,    0,   12,
   13,    7,  138,  206,    0,    9,  139,   10,  140,    0,
    6,    0,   55,    0,   54,   12,   13,    7,  138,    0,
  218,    9,  139,   10,  140,    0,    0,    6,    0,    0,
    0,   12,   13,    0,    7,    8,  226,    0,    9,    0,
   10,   11,    0,   55,    0,   54,    6,    0,   12,   13,
   80,    0,  275,    7,    8,    0,    0,    9,    0,   10,
   11,    0,    0,    0,    6,    0,    0,   12,   13,    0,
   90,    7,    8,    0,    0,    9,   95,   10,   11,    0,
    6,    0,    0,    0,    0,   12,   13,    7,    8,    0,
    0,    9,    0,   10,   11,    0,    0,    6,    0,    0,
    0,   12,   13,    0,    7,    8,    0,    0,    9,    6,
   10,   11,    0,    0,    0,    0,    7,    8,   12,   13,
    9,    0,   10,   11,  150,    0,    0,    0,    0,    0,
   12,   13,    0,    0,    0,    0,    6,    0,    0,    0,
    0,    0,    0,    7,    8,    0,    0,    9,    0,   10,
   11,    0,    0,    6,    0,    0,    0,   12,   13,    0,
    7,    8,    0,    0,    9,    0,   10,   11,    0,    6,
    0,    0,    0,    0,   12,   13,    7,    8,    0,    0,
    9,    0,   10,   11,  201,    6,    0,    0,    0,    0,
   12,   13,    7,    8,    0,    0,    9,    0,   10,   11,
    0,    0,    0,    0,    0,    0,   12,   13,  110,    0,
    0,   45,   46,   47,   48,   49,   50,    7,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  201,    0,    0,    0,   52,    0,    0,    0,  242,   44,
    0,    0,   45,   46,   47,   48,   49,   50,    7,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   51,    0,    0,    0,   52,    0,    0,    0,    0,
    0,    0,    0,    0,  201,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         43,
    0,   45,   40,   44,   84,  263,   40,  123,   41,   40,
   43,   41,   45,   56,   40,  258,  274,   46,    3,   44,
   61,   41,   41,  263,  256,   41,   60,    0,   62,   14,
   15,  263,  264,  265,  274,   40,  279,  280,   44,  263,
   40,   41,   42,   43,   44,   45,  278,   47,  265,  273,
  282,   36,  141,   41,    0,   43,  258,   45,  258,   59,
   60,   61,   62,  265,   42,  265,   59,   40,   41,   47,
   43,   44,   45,  123,  256,  257,  265,  279,  280,  279,
  280,  263,  264,  265,  273,  265,   59,   60,    0,   62,
  263,  264,  265,  123,   40,   41,  278,   43,   44,   45,
  282,  265,   87,  123,  123,  194,   91,  123,  227,  282,
  190,  256,  231,   59,   60,    0,   62,  258,  263,  264,
  265,  106,   42,  123,  265,  125,  123,   47,   40,   41,
   41,   43,   44,   45,  275,  276,  277,  282,  279,  280,
  256,   43,    0,   45,  129,  263,  189,   59,   60,  265,
   62,  123,  125,   59,   60,   40,   41,   41,   43,   44,
   45,  146,  251,  256,   42,   43,  265,   45,  257,   47,
  263,  264,  265,  265,   59,   60,    0,   62,   41,  125,
   40,   44,   40,   41,   40,   43,   44,   45,   41,  282,
   41,   44,   98,   99,   41,  100,  101,  277,  267,  268,
   41,   59,   60,   44,   62,  111,  112,  192,  113,  274,
  253,  116,  117,  125,  276,  277,   40,   41,  256,   43,
   44,   45,  256,  256,  265,  259,  260,  261,  262,  263,
  264,  265,   44,  258,  265,   59,   60,  281,   62,  265,
  125,  253,  227,  255,  278,   41,  231,  273,  282,   41,
   41,  274,  258,   44,  279,  280,  256,  282,  258,  259,
  260,  261,  262,  263,  264,  265,  266,  125,  256,  269,
  270,  271,  272,  279,  280,  260,  282,   40,  278,  279,
  280,  281,  282,  256,   40,  258,  259,  260,  261,  262,
  263,  264,  265,  266,  125,  263,  269,  270,  271,  272,
   41,  125,  263,   44,   41,  278,  279,  280,  281,  282,
  256,  263,  258,  259,  260,  261,  262,  263,  264,  265,
  266,  123,  123,  269,  270,  271,  272,   41,   41,  265,
   44,   44,  278,  279,  280,  281,  282,  263,  264,  265,
  267,  268,    0,   41,  256,   41,  258,  259,  260,  261,
  262,  263,  264,  265,  266,   41,  123,  269,  270,  271,
  272,  268,  263,  125,   40,   41,  278,  279,  280,  281,
  282,  256,  265,  258,  259,  260,  261,  262,  263,  264,
  265,  266,   40,   41,  269,  270,  271,  272,  267,  268,
  265,   59,  125,  278,  279,  280,  281,  282,  256,  268,
  258,  259,  260,  261,  262,  263,  264,  265,  266,  268,
   40,  269,  270,  271,  272,   41,  268,   59,   44,    0,
  278,  279,  280,  281,  282,  270,    0,    0,   40,  123,
  123,  123,  256,  123,  258,  259,  260,  261,  262,  263,
  264,  265,  266,  125,  256,  269,  270,  271,  272,    0,
  125,  263,  264,  265,  278,  279,  280,  281,  282,   40,
   41,  125,   43,   44,   45,  125,  278,  125,  256,  138,
  282,   53,   73,  140,    0,  263,  264,  265,   59,   60,
  195,   62,   59,  248,  239,   -1,   -1,   -1,   -1,   40,
   41,   -1,   -1,   44,  282,   -1,   -1,  256,   -1,    0,
   -1,   -1,   -1,   -1,  263,  264,  265,   -1,   59,   -1,
   -1,  123,   -1,   -1,   40,   41,   -1,   -1,   44,  256,
   -1,  258,    0,  282,   -1,   -1,  263,  264,  265,   -1,
   -1,   -1,   -1,   59,   -1,   -1,   -1,   -1,    0,   40,
   41,  278,  256,   44,  125,  282,  256,   -1,   -1,  263,
  264,  265,   -1,  263,  264,  265,   -1,   -1,   59,   -1,
   -1,    0,   40,   41,  278,   -1,   -1,   -1,  282,   -1,
   -1,   -1,  282,   -1,  125,   -1,   -1,    0,   40,   41,
   -1,   59,  258,   -1,   -1,   -1,   -1,   -1,   -1,  265,
  266,   -1,    0,  269,   -1,  271,  272,   59,   -1,  125,
  258,   40,   41,  279,  280,   -1,   -1,  265,  266,   -1,
   -1,  269,  270,  271,  272,   -1,   -1,   40,   41,   -1,
   59,  279,  280,   -1,  125,   -1,   -1,   -1,   -1,   -1,
  256,   -1,   40,   41,   -1,   -1,   59,  263,  264,  265,
   -1,   -1,   -1,   -1,   -1,   -1,  258,  125,   -1,   -1,
   -1,   59,  278,  265,  266,   -1,  282,  269,   41,  271,
  272,   44,   -1,  125,   -1,   -1,   -1,  279,  280,   -1,
   -1,   -1,   -1,   -1,   -1,  256,   -1,  258,  259,  260,
  261,  262,  263,  264,  265,  266,  125,   -1,  269,  270,
  271,  272,   -1,   -1,   -1,   40,   -1,  278,  279,  280,
  281,  282,  125,   -1,   -1,  256,   -1,  258,   -1,   -1,
   -1,   -1,  263,  264,  265,  266,   -1,  125,  269,  270,
  271,  272,   -1,   -1,   -1,   -1,   -1,  278,  279,  280,
  256,  282,  258,   -1,   40,   -1,   -1,  263,  264,  265,
  266,   -1,   -1,  269,  270,  271,  272,   -1,   -1,   -1,
   -1,   -1,  278,  279,  280,  256,  282,  258,   -1,   -1,
   -1,   -1,  263,  264,  265,  266,   -1,   -1,  269,  270,
  271,  272,   -1,   -1,   -1,    8,   -1,  278,  279,  280,
  258,  282,   40,   -1,   -1,   -1,   -1,  265,  266,   -1,
   -1,  269,  270,  271,  272,   -1,  258,   -1,   -1,   -1,
   -1,  279,  280,  265,  266,   -1,   -1,  269,  270,  271,
  272,   40,   -1,   16,   -1,   -1,   -1,  279,  280,  258,
   53,   -1,   -1,   -1,   -1,   -1,  265,  266,   -1,   -1,
  269,  270,  271,  272,   40,  258,   -1,   70,   -1,   42,
  279,  280,  265,  266,   -1,   -1,  269,  270,  271,  272,
  258,   -1,   -1,   59,    2,    3,  279,  280,  266,   -1,
   -1,  269,  270,  271,  272,  123,   14,   15,   16,  102,
  103,  279,  280,  256,   -1,   78,   79,   -1,   -1,   -1,
  263,  264,  265,   31,   -1,   -1,   -1,  120,   36,   92,
  123,   -1,   -1,   -1,   42,  278,  125,   -1,   -1,  282,
   40,   -1,   -1,  136,   -1,  138,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  258,   -1,   -1,   40,   -1,   -1,  125,
  265,  266,   -1,   -1,  269,  270,  271,  272,  161,   -1,
   78,   79,   40,   -1,  279,  280,   -1,   85,  141,   87,
   -1,   -1,   -1,   91,   92,  178,   -1,   -1,  151,   40,
   -1,   -1,  258,   -1,   -1,   -1,   -1,   -1,  106,  265,
  266,  164,   -1,  269,   -1,  271,  272,   -1,   40,   -1,
   -1,   -1,   -1,  279,  280,   -1,   -1,   -1,   -1,   -1,
   -1,  129,   -1,   -1,  187,  125,   40,   -1,   -1,   -1,
   -1,  194,   -1,  141,   -1,   -1,   -1,  230,  146,   -1,
  258,  125,   40,  151,   -1,   -1,   -1,  265,  266,   -1,
   -1,  269,   -1,  271,  272,  248,  164,  125,   -1,   40,
   -1,  279,  280,   -1,   -1,   -1,   -1,   -1,   -1,  258,
  233,   40,   -1,   -1,  125,   -1,  265,  266,   -1,  187,
  269,  270,  271,  272,  192,  278,  194,   -1,  251,   -1,
  279,  280,  258,  125,  257,   -1,   -1,   -1,   40,  265,
  266,   -1,   -1,  269,  270,  271,  272,   -1,  271,   -1,
   -1,  125,   -1,  279,  280,   40,   -1,   -1,   -1,  227,
   -1,   -1,   -1,  231,   -1,  233,   -1,  125,   -1,   -1,
   -1,   40,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  251,  125,   -1,   -1,   40,   -1,  257,
   -1,   -1,  260,   -1,   -1,   -1,  125,   -1,  258,   -1,
   -1,   -1,   -1,  271,   -1,  265,  266,   -1,   -1,  269,
  270,  271,  272,   -1,  258,   43,   -1,   45,   -1,  279,
  280,  265,  266,  125,   -1,  269,  270,  271,  272,   -1,
  258,   -1,   60,   -1,   62,  279,  280,  265,  266,   -1,
  125,  269,  270,  271,  272,   -1,   -1,  258,   -1,   -1,
   -1,  279,  280,   -1,  265,  266,  125,   -1,  269,   -1,
  271,  272,   -1,   60,   -1,   62,  258,   -1,  279,  280,
   15,   -1,  125,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   -1,   -1,  258,   -1,   -1,  279,  280,   -1,
   35,  265,  266,   -1,   -1,  269,   41,  271,  272,   -1,
  258,   -1,   -1,   -1,   -1,  279,  280,  265,  266,   -1,
   -1,  269,   -1,  271,  272,   -1,   -1,  258,   -1,   -1,
   -1,  279,  280,   -1,  265,  266,   -1,   -1,  269,  258,
  271,  272,   -1,   -1,   -1,   -1,  265,  266,  279,  280,
  269,   -1,  271,  272,   89,   -1,   -1,   -1,   -1,   -1,
  279,  280,   -1,   -1,   -1,   -1,  258,   -1,   -1,   -1,
   -1,   -1,   -1,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   -1,  258,   -1,   -1,   -1,  279,  280,   -1,
  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,  258,
   -1,   -1,   -1,   -1,  279,  280,  265,  266,   -1,   -1,
  269,   -1,  271,  272,  149,  258,   -1,   -1,   -1,   -1,
  279,  280,  265,  266,   -1,   -1,  269,   -1,  271,  272,
   -1,   -1,   -1,   -1,   -1,   -1,  279,  280,  256,   -1,
   -1,  259,  260,  261,  262,  263,  264,  265,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  195,   -1,   -1,   -1,  282,   -1,   -1,   -1,  203,  256,
   -1,   -1,  259,  260,  261,  262,  263,  264,  265,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  278,   -1,   -1,   -1,  282,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  239,
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
"sentencia_asignacion_unaria : sentencia_asignacion_unaria_error",
"sentencia_asignacion_unaria_error : VAR ID expresion_aritmetica",
"sentencia_asignacion_multiple : lista_variables '=' lista_exp_aritmeticas",
"funcion : encabezado_funcion '{' cuerpo_funcion '}'",
"funcion : sentencia_lambda",
"funcion : funcion_error",
"encabezado_funcion : lista_tipos FUN ID '(' lista_param_formales ')'",
"cuerpo_funcion : cuerpo sentencia_funcion",
"cuerpo_funcion : sentencia_funcion cuerpo",
"cuerpo_funcion : cuerpo sentencia_funcion cuerpo",
"cuerpo_funcion : sentencia_funcion cuerpo sentencia_funcion",
"cuerpo_funcion : sentencia_funcion",
"sentencia_funcion : sentencia_ejecucion_retorno",
"sentencia_funcion : sentencia_retorno",
"sentencia_retorno : RETURN '(' lista_exp_aritmeticas ')' ';'",
"funcion_error : lista_tipos FUN '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'",
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
"factor : FUN ID '(' lista_param_reales ')'",
"factor : variable",
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

//#line 396 "grammar.y"

private static int yylval_recognition = 0;

static AnalizadorLexico lex = null;
static Parser par = null;
static Cursor cursor = null;

public static void main (String [] args) {

    System.out.println("Iniciando compilación ... ");
    Scanner lector = new Scanner(System.in);
    System.out.println("Ingrese la ruta deseada");
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
//#line 803 "Parser.java"
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
case 3:
//#line 32 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre del programa");}
break;
case 4:
//#line 33 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Debe indicar el programa entre {}");}
break;
case 5:
//#line 34 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Faltan los delimitadores de programa");}
break;
case 6:
//#line 35 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '}'");}
break;
case 7:
//#line 36 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '{'");}
break;
case 8:
//#line 37 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Hay errores lexicos o sintaticos no identificados");}
break;
case 28:
//#line 96 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
break;
case 32:
//#line 106 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
break;
case 35:
//#line 112 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
break;
case 40:
//#line 126 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
break;
case 41:
//#line 127 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
break;
case 42:
//#line 128 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
break;
case 45:
//#line 137 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}
break;
case 49:
//#line 149 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de iteracion");}
break;
case 50:
//#line 150 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de iteracion");}
break;
case 51:
//#line 151 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de iteracion");}
break;
case 54:
//#line 160 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado ID del for");}
break;
case 55:
//#line 161 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado FROM del for");}
break;
case 56:
//#line 162 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_1 del for");}
break;
case 57:
//#line 163 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado TO del for");}
break;
case 58:
//#line 164 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_2 del for");}
break;
case 61:
//#line 173 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
break;
case 66:
//#line 187 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de asignacion luego de var");}
break;
case 67:
//#line 191 "grammar.y"
{if (val_peek(2).ival != val_peek(0).ival) {Logger.logError(cursor.getCurrentLine(), "La cantidad de variables (" + val_peek(2).ival + ") no coincide con la cantidad de expresiones (" + val_peek(0).ival + ") en la asignación múltiple.");}}
break;
case 80:
//#line 220 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion");}
break;
case 85:
//#line 236 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta delimitador izquierdo '{' del cuerpo lambda");}
break;
case 91:
//#line 252 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
break;
case 92:
//#line 253 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
break;
case 93:
//#line 254 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
break;
case 99:
//#line 267 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '+''");}
break;
case 100:
//#line 268 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '-'");}
break;
case 101:
//#line 269 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '+'");}
break;
case 102:
//#line 270 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 107:
//#line 281 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '*'");}
break;
case 108:
//#line 282 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
break;
case 109:
//#line 283 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
break;
case 110:
//#line 284 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 117:
//#line 300 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en expresion_aritmetica TOI");}
break;
case 118:
//#line 301 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en expresion_aritmetica TOI");}
break;
case 119:
//#line 302 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en expresion_aritmetica TOI");}
break;
case 126:
//#line 315 "grammar.y"
{yyval.ival = val_peek(2).ival + 1;}
break;
case 127:
//#line 316 "grammar.y"
{yyval.ival = 1;}
break;
case 129:
//#line 321 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado derecho)");}
break;
case 133:
//#line 330 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de tipos)");}
break;
case 136:
//#line 338 "grammar.y"
{yyval.ival = val_peek(2).ival + 1;}
break;
case 137:
//#line 339 "grammar.y"
{yyval.ival = 1; }
break;
case 139:
//#line 344 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
break;
case 148:
//#line 365 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 149:
//#line 366 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 150:
//#line 367 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 151:
//#line 368 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 157:
//#line 383 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de LE o SE despues de CR");}
break;
case 158:
//#line 384 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de SE");}
break;
case 159:
//#line 385 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de LE");}
break;
case 162:
//#line 393 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta especificacion del parametro formal");}
break;
//#line 1172 "Parser.java"
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
