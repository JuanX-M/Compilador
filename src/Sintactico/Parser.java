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
public final static short UMINUS=283;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    2,    2,    2,    2,    2,    1,    1,    3,
    3,    3,    4,    5,    5,    5,    5,    6,    8,    8,
    8,   13,    9,    9,    9,   16,   16,   14,   14,   18,
   18,   18,   15,   15,   19,   10,   20,   20,   23,   23,
   23,   22,   22,   24,   24,   24,   24,   24,   21,   21,
   25,   11,   11,   26,   26,   29,   27,    7,    7,    7,
   34,   33,   35,   36,   37,   37,   37,   17,   17,   41,
   41,   41,   28,   28,   28,   28,   28,   44,   44,   44,
   44,   42,   42,   42,   42,   45,   45,   45,   45,   40,
   40,   40,   40,   43,   43,   48,   48,   48,   39,   39,
   39,   39,   39,   39,   12,   12,   12,   49,   31,   31,
   32,   32,   30,   30,   30,   51,   47,   47,   38,   38,
   38,   50,   50,   50,   53,   53,   53,   53,   46,   46,
   52,   52,   52,   55,   55,   55,   54,
};
final static short yylen[] = {                            2,
    4,    1,    3,    4,    2,    3,    3,    2,    1,    1,
    2,    1,    1,    1,    1,    1,    1,    1,    4,    4,
    1,    3,    6,    4,    1,    5,    3,    3,    1,    2,
    2,    1,    3,    1,    2,    3,    3,    1,    2,    2,
    1,    5,    1,    4,    4,    4,    4,    4,    3,    1,
    2,    1,    1,    4,    1,    3,    3,   13,    1,    1,
   12,    3,    4,    3,    3,    3,    3,    3,    1,    2,
    2,    2,    3,    3,    1,    1,    1,    3,    3,    3,
    3,    3,    3,    1,    1,    3,    3,    3,    3,    1,
    1,    4,    1,    4,    1,    4,    4,    4,    1,    1,
    1,    1,    1,    1,    3,    1,    1,    3,    3,    1,
    3,    1,    3,    1,    1,    2,    1,    3,    1,    1,
    1,    3,    2,    1,    2,    1,    2,    1,    3,    1,
    2,    2,    1,    1,    1,    1,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    2,  121,    0,    0,    0,    0,    0,
  119,  120,    0,    0,    0,    9,   10,    0,   12,   13,
   14,   15,   16,   17,   21,   25,   52,   53,   55,    0,
    0,   59,   60,    0,  110,  114,  115,    0,    0,    0,
    0,   99,  100,  101,  102,   90,   91,    0,    0,    0,
  103,  104,    0,    0,   29,    0,    0,   84,   69,    0,
   75,   77,   85,   93,   95,    0,    0,    0,    0,    0,
    0,    0,   38,   43,    0,    0,    0,    7,    8,   11,
    0,    0,  116,    0,    0,    0,    0,    0,    0,    3,
  118,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   34,   30,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   22,    0,    0,  107,    0,    0,    0,    0,
    0,    0,    0,   36,   50,   39,    1,    4,    0,    0,
  113,    0,  128,    0,  135,  136,    0,    0,  112,    0,
  124,  133,  109,    0,    0,   62,    0,    0,   88,   89,
    0,    0,    0,    0,  130,   28,   35,    0,    0,   24,
    0,    0,    0,    0,   68,   86,   82,   87,   83,   19,
    0,   20,    0,    0,    0,    0,    0,    0,   37,   51,
    0,   63,    0,  131,  132,    0,    0,  123,  127,    0,
   64,    0,    0,    0,   98,   96,   97,   94,    0,   92,
    0,   33,    0,    0,    0,   45,   47,    0,   46,   44,
   49,    0,    0,  111,  122,   66,   67,   65,  137,  129,
   23,   42,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   61,   58,
};
final static short yydgoto[] = {                          3,
   15,    4,   16,   17,   18,   19,   20,   21,   22,   23,
   24,  114,   25,   53,  101,   26,   54,   55,  102,   71,
  124,   72,   73,   74,  125,   27,   28,  115,   29,   30,
   31,  137,   32,   33,   34,   88,  146,   35,   57,   58,
   59,   60,   61,   62,   63,  154,   64,   65,  116,  139,
   37,  140,  141,  155,  142,
};
final static short yysindex[] = {                      -118,
  381,  753,    0,    0,    0,    7,  -33,   26, -218,  -25,
    0,    0,  753,  753,  589,    0,    0,   11,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -41,
  -40,    0,    0,  -45,    0,    0,    0, -228,  609, -183,
   95,    0,    0,    0,    0,    0,    0,  -31,   70,  786,
    0,    0,    2,   60,    0,  280, -179,    0,    0,   34,
    0,    0,    0,    0,    0,  234,  -66, -214, -140, -209,
    8,  105,    0,    0,  625,  323, -129,    0,    0,    0,
 -143, -115,    0,  118,  341, -228,  753,  129, -129,    0,
    0, -179, -179,  -93,  -93, -143, -143, -143,  123,  637,
 -193,    0,    0,   50,  -71,   40,  -93,   34,   34,  218,
  315,  132,    0,   -6,  -12,    0, -143,  -12, -100, -249,
  -95,  140,  654,    0,    0,    0,    0,    0,  141,  -18,
    0,  341,    0, -158,    0,    0,   61,  -70,    0, -200,
    0,    0,    0,  666, -247,    0,   34,   34,    0,    0,
  -22,   -9,  -35,   67,    0,    0,    0,  682,    2,    0,
   50,   34,   50,   34,    0,    0,    0,    0,    0,    0,
 -143,    0, -143,  -12,  -63, -224,  -59,  -57,    0,    0,
  708,    0,   68,    0,    0,   84,  341,    0,    0,  -52,
    0,  167,  174,  178,    0,    0,    0,    0,  -14,    0,
 -143,    0,  -15,  -12,  -12,    0,    0,  -28,    0,    0,
    0,  120,  753,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  753,  725,  741,  -16,    4,  215,  231, -143,
 -143,   -4,   13,  199,  219,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    1,    0,    0,    0,    0,
    0,    0,    0,    0,  279,    0,    0,  572,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  162,    0,    0,    0,    0,    0,   28,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  172,    0,    0,  298,    0,  -38,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  183,    0,
  489,    0,    0,    0,    0,    0,  -30,  -29,  -19,    0,
    0,    0,    0,    0,  444,    0,    0,  506,    0,    0,
    0,  189,    0,    0,    0,    0,    0,    0,    0,  518,
    0,    0,    0, -113,    0,    0,    0,  100,    0,    0,
    0,    0,    0,    0,    0,    0,   55,   89,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  116,  143,  177,  410,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  534,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  124,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  560,  461,  473,    0,    0,  -17,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  863,    0,   52,    0,    0,    0,    0,    0,    0,    0,
    0,  -68,    0,    0,  142,    0,  278,    0,    0,    0,
    0,  259,    0,    0,    0,    0,    0,  942,    0,    0,
    0,  198,    0,    0,    0,    0,    0,   69,  275,  384,
    0,  370,    0,    0,    0,    0,  954,    0,    0,  145,
    0,    0,    0,  137,    0,
};
final static int YYTABLESIZE=1179;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         85,
  117,  110,   82,   86,    2,  110,   50,  105,   97,  106,
   71,   70,  130,  176,   70,  192,  193,  194,  196,   81,
  105,   72,  106,   48,  177,  173,   52,   76,   51,    5,
  105,  198,  106,  105,  172,  106,  234,  173,  207,  173,
  117,  117,  117,  117,  117,  117,   67,  117,  119,  208,
   11,   12,   40,  235,   78,   68,  173,    5,  120,  117,
  117,  117,  117,   69,  189,   66,   79,   76,   76,   80,
   76,   76,   76,  159,  160,  110,  104,   87,   11,   12,
  111,   91,   77,   46,   47,    6,   76,   76,   79,   76,
   79,   94,   71,   70,   78,   78,   95,   78,   78,   78,
  103,  186,   49,   72,  187,   48,   89,  200,  212,   98,
  201,  187,   41,   78,   78,   80,   78,  184,  185,   46,
   47,    6,  121,  117,  100,  117,   79,   79,   79,   79,
  123,   79,   79,   79,   48,  129,   94,   92,   49,   93,
  126,   95,   73,  126,  134,  126,    1,   79,   79,    6,
   79,  134,   76,  138,  143,   80,   80,  132,   80,   80,
   80,  232,  233,  156,  125,  134,  134,  125,  145,   46,
   47,    6,  170,  175,   80,   80,   81,   80,  178,   78,
  179,  182,   73,   73,  161,   73,   73,   73,   49,   41,
  117,   46,   47,    6,  188,   79,   46,   47,    6,  206,
  138,   73,   73,  209,   73,  210,  213,  216,  190,   79,
   49,   48,  215,   79,  217,   49,   81,   81,  218,   81,
   81,   81,   41,    6,   96,   42,   43,   44,   45,   46,
   47,    6,   79,  195,  222,   81,   81,  171,   81,   68,
   80,   84,  223,  110,   48,  199,  197,   69,   49,  171,
  219,  171,  221,  228,  230,  138,  117,  236,  117,  117,
  117,  117,  117,  117,  117,  117,  117,   73,  171,  117,
  231,  117,  117,  229,  113,   79,   79,  237,    5,  117,
  117,  117,  117,   76,   32,   76,   76,   76,   76,   76,
   76,   76,   76,   76,   41,  163,   76,    6,   76,   76,
  203,   81,   46,   47,    6,   31,   76,   76,   76,   76,
   78,   40,   78,   78,   78,   78,   78,   78,   78,   78,
   78,   49,  105,   78,  106,   78,   78,   99,  122,  183,
  107,  214,    0,   78,   78,   78,   78,  220,    0,   52,
    0,   51,    0,    0,   79,    0,   79,   79,   79,   79,
   79,   79,   79,   79,   79,    0,    0,   79,    0,   79,
   79,    0,   38,  128,    0,    0,    0,   79,   79,   79,
   79,   80,    0,   80,   80,   80,   80,   80,   80,   80,
   80,   80,    0,    0,   80,    0,   80,   80,    0,    0,
    0,    0,    0,    0,   80,   80,   80,   80,   73,    0,
   73,   73,   73,   73,   73,   73,   73,   73,   73,   74,
    0,   73,    0,   73,   73,    0,    0,    0,    0,    0,
   14,   73,   73,   73,   73,  108,  109,    0,    0,    0,
    0,    0,   81,    0,   81,   81,   81,   81,   81,   81,
   81,   81,   81,  106,    0,   81,    0,   81,   81,   74,
   74,    0,   74,   74,   74,   81,   81,   81,   81,    0,
  108,  147,  148,    0,    0,    0,    0,    0,   74,   74,
    0,   74,  105,  166,  162,  164,    0,  149,  150,    0,
   46,   47,    6,  106,  106,    0,    0,  106,   27,   41,
  165,  112,    0,  167,  169,    0,   46,   47,    6,   49,
  108,  108,  106,   13,  108,   56,    0,    0,    0,    0,
    0,   48,  105,  105,    0,   49,  105,   57,    0,  108,
    0,    0,    0,    0,    0,    0,    0,    0,   27,   27,
    0,  105,    0,   54,   74,  104,    0,    0,   42,   43,
   44,   45,   46,   47,    6,   56,   56,   27,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   57,   57,   26,
    0,   49,    0,    0,   56,    0,    0,    0,  106,    0,
  168,   18,    0,   54,   54,    0,   57,   46,   47,    6,
    5,    0,    0,    0,    0,  108,    0,    6,    7,    0,
    0,    8,   54,    9,   10,    0,   49,  105,    5,   26,
   26,   11,   12,    0,    0,  133,    0,    0,    0,    0,
    0,   18,   18,   27,    0,  134,  135,  136,   26,   11,
   12,    0,    0,    0,    0,    0,    0,    0,   38,    0,
   56,    0,    0,    0,    0,    0,    0,    0,    5,    0,
    0,    0,   57,    0,    0,    6,    7,    0,   38,    8,
    0,    9,   10,    0,    0,    0,    0,    0,   54,   11,
   12,    0,    0,    0,   38,   74,    0,   74,   74,   74,
   74,   74,   74,   74,   74,   74,   38,    0,   74,    0,
   74,   74,    0,    0,   26,    0,    0,    0,   74,   74,
   74,   74,    0,   38,    0,    0,   18,    0,    0,  106,
    0,  106,    0,    0,    0,   38,    0,    0,  106,  106,
    0,    0,  106,   78,  106,  106,  108,    0,  108,    0,
    0,   38,  106,  106,    0,  108,  108,    0,  105,  108,
  105,  108,  108,   90,    0,    0,    0,  105,  105,  108,
  108,  105,    0,  105,  105,    0,   27,   38,    0,  127,
    0,  105,  105,   27,   27,    0,    0,   27,    0,   27,
   27,  157,    0,   56,   38,    0,    0,   27,   27,    0,
   56,   56,    0,    0,   56,   57,   56,   56,  180,    0,
   38,    0,   57,   57,   56,   56,   57,    0,   57,   57,
  191,   54,   38,    0,    0,    0,   57,   57,   54,   54,
    0,    0,   54,    0,   54,   54,  202,    0,    0,    0,
    0,    0,   54,   54,    0,    0,    0,   26,    0,    0,
    0,    0,    0,    0,   26,   26,    0,    0,   26,   18,
   26,   26,  211,    0,    0,    0,   18,   18,   26,   26,
   18,    0,   18,   18,    0,   52,    5,   51,    0,  226,
   18,   18,    0,    6,    7,    0,    0,    8,    0,    9,
   10,    0,    0,    0,   39,  227,    5,   11,   12,    0,
    0,    0,    0,    6,    7,   75,   76,    8,    0,    9,
   10,    0,    5,    0,    0,    0,    0,   11,   12,    6,
    7,    0,    0,    8,    5,    9,   10,    0,    0,    0,
    0,    6,    7,   11,   12,    8,    0,    9,   10,    0,
    0,    5,    0,    0,    0,   11,   12,    0,    6,    7,
    0,    0,    8,    5,    9,   10,    0,    0,    0,    0,
    6,    7,   11,   12,    8,    0,    9,   10,    0,    5,
    0,    0,    0,    0,   11,   12,    6,    7,   56,  144,
    8,    0,    9,   10,   36,   36,    0,    0,    0,    0,
   11,   12,  158,    0,    0,    5,   36,   36,   36,    0,
    0,    0,    6,    7,    0,    0,    8,    0,    9,   10,
    0,    0,    5,   83,    0,  181,   11,   12,    0,    6,
    7,   56,   36,    8,    0,    9,   10,    0,    5,    0,
    0,    0,    0,   11,   12,    6,    7,    0,  118,    8,
    5,    9,   10,    0,    0,    0,    0,    6,    7,   11,
   12,    8,    0,    9,   10,    0,    0,    0,   36,   36,
    0,   11,   12,    0,    0,  131,    0,  151,  152,  153,
   36,   41,    0,    0,   42,   43,   44,   45,   46,   47,
    6,    0,    0,   36,    0,    0,    0,    0,  174,    0,
    0,    0,    0,   48,    0,    0,    0,   49,    0,    0,
    0,    0,    0,    0,    0,  224,   36,    0,    0,    0,
    0,    0,    0,    0,    0,  225,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   36,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   36,  204,    0,  205,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   36,    0,    0,    0,    0,    0,
    0,    0,  153,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   36,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   36,   36,   36,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   40,   44,   44,  123,   44,   40,   43,   40,   45,
   41,   41,   81,  263,   40,  263,  264,  265,   41,   61,
   43,   41,   45,   41,  274,   44,   60,    0,   62,  258,
   43,   41,   45,   43,   41,   45,   41,   44,  263,   44,
   40,   41,   42,   43,   44,   45,  265,   47,  263,  274,
  279,  280,   46,   41,    0,  265,   44,  258,  273,   59,
   60,   61,   62,  273,  265,   40,   15,   40,   41,   59,
   43,   44,   45,  267,  268,   42,  256,  123,  279,  280,
   47,  265,   14,  263,  264,  265,   59,   60,    0,   62,
   39,   42,  123,  123,   40,   41,   47,   43,   44,   45,
   41,   41,  282,  123,   44,  123,   38,   41,   41,   40,
   44,   44,  256,   59,   60,    0,   62,  276,  277,  263,
  264,  265,  263,  123,  123,  125,   75,   76,   40,   41,
  123,   43,   44,   45,  278,  265,   42,   43,  282,   45,
   41,   47,    0,   44,  258,   41,  265,   59,   60,  265,
   62,  265,  125,   85,   86,   40,   41,   40,   43,   44,
   45,  230,  231,   41,   41,  279,  280,   44,   40,  263,
  264,  265,   41,  274,   59,   60,    0,   62,  274,  125,
   41,   41,   40,   41,  256,   43,   44,   45,  282,  256,
  257,  263,  264,  265,  265,  144,  263,  264,  265,  263,
  132,   59,   60,  263,   62,  263,  123,   41,  140,  158,
  282,  278,  265,  125,   41,  282,   40,   41,   41,   43,
   44,   45,  256,  265,  256,  259,  260,  261,  262,  263,
  264,  265,  181,  256,  263,   59,   60,  256,   62,  265,
  125,  282,  123,  282,  278,  281,  256,  273,  282,  256,
  265,  256,  268,  270,   40,  187,  256,   59,  258,  259,
  260,  261,  262,  263,  264,  265,  266,  125,  256,  269,
   40,  271,  272,  270,   41,  224,  225,   59,    0,  279,
  280,  281,  282,  256,  123,  258,  259,  260,  261,  262,
  263,  264,  265,  266,  123,  256,  269,    0,  271,  272,
  159,  125,  263,  264,  265,  123,  279,  280,  281,  282,
  256,  123,  258,  259,  260,  261,  262,  263,  264,  265,
  266,  282,   43,  269,   45,  271,  272,   50,   70,  132,
   56,  187,   -1,  279,  280,  281,  282,  201,   -1,   60,
   -1,   62,   -1,   -1,  256,   -1,  258,  259,  260,  261,
  262,  263,  264,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   40,   41,   -1,   -1,   -1,  279,  280,  281,
  282,  256,   -1,  258,  259,  260,  261,  262,  263,  264,
  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,   -1,
   -1,   -1,   -1,   -1,  279,  280,  281,  282,  256,   -1,
  258,  259,  260,  261,  262,  263,  264,  265,  266,    0,
   -1,  269,   -1,  271,  272,   -1,   -1,   -1,   -1,   -1,
   40,  279,  280,  281,  282,   56,   57,   -1,   -1,   -1,
   -1,   -1,  256,   -1,  258,  259,  260,  261,  262,  263,
  264,  265,  266,    0,   -1,  269,   -1,  271,  272,   40,
   41,   -1,   43,   44,   45,  279,  280,  281,  282,   -1,
    0,   92,   93,   -1,   -1,   -1,   -1,   -1,   59,   60,
   -1,   62,    0,  256,  105,  106,   -1,   94,   95,   -1,
  263,  264,  265,   40,   41,   -1,   -1,   44,    0,  256,
  107,  258,   -1,  110,  111,   -1,  263,  264,  265,  282,
   40,   41,   59,  123,   44,    0,   -1,   -1,   -1,   -1,
   -1,  278,   40,   41,   -1,  282,   44,    0,   -1,   59,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,   41,
   -1,   59,   -1,    0,  125,  256,   -1,   -1,  259,  260,
  261,  262,  263,  264,  265,   40,   41,   59,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,   41,    0,
   -1,  282,   -1,   -1,   59,   -1,   -1,   -1,  125,   -1,
  256,    0,   -1,   40,   41,   -1,   59,  263,  264,  265,
  258,   -1,   -1,   -1,   -1,  125,   -1,  265,  266,   -1,
   -1,  269,   59,  271,  272,   -1,  282,  125,  258,   40,
   41,  279,  280,   -1,   -1,  265,   -1,   -1,   -1,   -1,
   -1,   40,   41,  125,   -1,  275,  276,  277,   59,  279,
  280,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   40,   -1,
  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  258,   -1,
   -1,   -1,  125,   -1,   -1,  265,  266,   -1,   40,  269,
   -1,  271,  272,   -1,   -1,   -1,   -1,   -1,  125,  279,
  280,   -1,   -1,   -1,   40,  256,   -1,  258,  259,  260,
  261,  262,  263,  264,  265,  266,   40,   -1,  269,   -1,
  271,  272,   -1,   -1,  125,   -1,   -1,   -1,  279,  280,
  281,  282,   -1,   40,   -1,   -1,  125,   -1,   -1,  256,
   -1,  258,   -1,   -1,   -1,   40,   -1,   -1,  265,  266,
   -1,   -1,  269,  125,  271,  272,  256,   -1,  258,   -1,
   -1,   40,  279,  280,   -1,  265,  266,   -1,  256,  269,
  258,  271,  272,  125,   -1,   -1,   -1,  265,  266,  279,
  280,  269,   -1,  271,  272,   -1,  258,   40,   -1,  125,
   -1,  279,  280,  265,  266,   -1,   -1,  269,   -1,  271,
  272,  125,   -1,  258,   40,   -1,   -1,  279,  280,   -1,
  265,  266,   -1,   -1,  269,  258,  271,  272,  125,   -1,
   40,   -1,  265,  266,  279,  280,  269,   -1,  271,  272,
  125,  258,   40,   -1,   -1,   -1,  279,  280,  265,  266,
   -1,   -1,  269,   -1,  271,  272,  125,   -1,   -1,   -1,
   -1,   -1,  279,  280,   -1,   -1,   -1,  258,   -1,   -1,
   -1,   -1,   -1,   -1,  265,  266,   -1,   -1,  269,  258,
  271,  272,  125,   -1,   -1,   -1,  265,  266,  279,  280,
  269,   -1,  271,  272,   -1,   60,  258,   62,   -1,  125,
  279,  280,   -1,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   -1,   -1,    2,  125,  258,  279,  280,   -1,
   -1,   -1,   -1,  265,  266,   13,   14,  269,   -1,  271,
  272,   -1,  258,   -1,   -1,   -1,   -1,  279,  280,  265,
  266,   -1,   -1,  269,  258,  271,  272,   -1,   -1,   -1,
   -1,  265,  266,  279,  280,  269,   -1,  271,  272,   -1,
   -1,  258,   -1,   -1,   -1,  279,  280,   -1,  265,  266,
   -1,   -1,  269,  258,  271,  272,   -1,   -1,   -1,   -1,
  265,  266,  279,  280,  269,   -1,  271,  272,   -1,  258,
   -1,   -1,   -1,   -1,  279,  280,  265,  266,    7,   87,
  269,   -1,  271,  272,    1,    2,   -1,   -1,   -1,   -1,
  279,  280,  100,   -1,   -1,  258,   13,   14,   15,   -1,
   -1,   -1,  265,  266,   -1,   -1,  269,   -1,  271,  272,
   -1,   -1,  258,   30,   -1,  123,  279,  280,   -1,  265,
  266,   50,   39,  269,   -1,  271,  272,   -1,  258,   -1,
   -1,   -1,   -1,  279,  280,  265,  266,   -1,   67,  269,
  258,  271,  272,   -1,   -1,   -1,   -1,  265,  266,  279,
  280,  269,   -1,  271,  272,   -1,   -1,   -1,   75,   76,
   -1,  279,  280,   -1,   -1,   82,   -1,   96,   97,   98,
   87,  256,   -1,   -1,  259,  260,  261,  262,  263,  264,
  265,   -1,   -1,  100,   -1,   -1,   -1,   -1,  117,   -1,
   -1,   -1,   -1,  278,   -1,   -1,   -1,  282,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  213,  123,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  223,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  144,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  158,  171,   -1,  173,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  181,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  201,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  213,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  223,  224,  225,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=283;
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
"LE","TOI","INT","FLOAT","ARROW","FUN","UMINUS",
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
"funcion : lista_tipos FUN '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'",
"funcion : sentencia_lambda",
"funcion : funcion_error",
"funcion_error : lista_tipos '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'",
"sentencia_lambda : parametro_lambda cuerpo_lambda argumento_lambda",
"parametro_lambda : '(' tipo ID ')'",
"cuerpo_lambda : '{' cuerpo '}'",
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
"factor : FUN '(' lista_param_reales ')'",
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
"lista_exp_aritmeticas_error : lista_exp_aritmeticas error expresion_aritmetica",
"lista_tipos : lista_tipos ',' tipo",
"lista_tipos : tipo",
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
};

//#line 335 "grammar.y"

private static int yylval_recognition = 0;

static AnalizadorLexico lex = null;
static Parser par = null;
static Cursor cursor = null;

public static void main (String [] args) {

    System.out.println("Iniciando compilación ... ");
    String programa = "samplePrograms/testing.txt";

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
//#line 708 "Parser.java"
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
//#line 29 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre del programa");}
break;
case 4:
//#line 30 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Debe indicar el programa entre {}");}
break;
case 5:
//#line 31 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Faltan los delimitadores de programa");}
break;
case 6:
//#line 32 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '}'");}
break;
case 7:
//#line 33 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '{'");}
break;
case 18:
//#line 66 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
break;
case 22:
//#line 76 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
break;
case 25:
//#line 82 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
break;
case 30:
//#line 96 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
break;
case 31:
//#line 97 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
break;
case 32:
//#line 98 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
break;
case 35:
//#line 107 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}
break;
case 39:
//#line 119 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de iteracion");}
break;
case 40:
//#line 120 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de iteracion");}
break;
case 41:
//#line 121 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de iteracion");}
break;
case 44:
//#line 130 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado ID del for");}
break;
case 45:
//#line 131 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado FROM del for");}
break;
case 46:
//#line 132 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_1 del for");}
break;
case 47:
//#line 133 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado TO del for");}
break;
case 48:
//#line 134 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_2 del for");}
break;
case 51:
//#line 143 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
break;
case 56:
//#line 157 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de asignacion luego de var");}
break;
case 61:
//#line 171 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion");}
break;
case 70:
//#line 198 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
break;
case 71:
//#line 199 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
break;
case 72:
//#line 200 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
break;
case 78:
//#line 212 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '+''");}
break;
case 79:
//#line 213 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '-'");}
break;
case 80:
//#line 214 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '+'");}
break;
case 81:
//#line 215 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 86:
//#line 226 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '*'");}
break;
case 87:
//#line 227 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
break;
case 88:
//#line 228 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
break;
case 89:
//#line 229 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 96:
//#line 245 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en expresion_aritmetica TOI");}
break;
case 97:
//#line 246 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en expresion_aritmetica TOI");}
break;
case 98:
//#line 247 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en expresion_aritmetica TOI");}
break;
case 108:
//#line 266 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado derecho)");}
break;
case 116:
//#line 286 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
break;
case 125:
//#line 307 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 126:
//#line 308 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 127:
//#line 309 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 128:
//#line 310 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 134:
//#line 325 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de LE o SE despues de CR");}
break;
case 135:
//#line 326 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de SE");}
break;
case 136:
//#line 327 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de LE");}
break;
//#line 1041 "Parser.java"
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
