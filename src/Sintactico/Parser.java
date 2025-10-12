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
public final static short SENTENCIA_ASIGNACION_PREC=283;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    2,    2,    2,    2,    2,    1,    1,    3,
    3,    3,    4,    5,    5,    5,    5,    6,    8,    8,
    8,   13,    9,    9,    9,   16,   16,   14,   14,   18,
   18,   18,   15,   15,   19,   10,   20,   20,   23,   23,
   23,   22,   22,   24,   24,   24,   24,   24,   21,   21,
   25,   11,   11,   26,   26,   29,   27,    7,    7,    7,
   34,   33,   35,   36,   36,   39,   37,   37,   37,   17,
   17,   42,   42,   42,   28,   28,   28,   28,   28,   45,
   45,   45,   45,   43,   43,   43,   43,   46,   46,   46,
   46,   41,   41,   41,   41,   44,   44,   49,   49,   49,
   40,   40,   40,   40,   40,   40,   12,   12,   12,   50,
   31,   31,   31,   51,   32,   32,   30,   30,   30,   53,
   48,   48,   38,   38,   38,   52,   52,   52,   55,   55,
   55,   55,   47,   47,   54,   54,   54,   57,   57,   57,
   56,   56,   58,
};
final static short yylen[] = {                            2,
    4,    1,    3,    4,    2,    3,    3,    2,    1,    1,
    2,    1,    1,    1,    1,    1,    1,    1,    4,    4,
    1,    3,    6,    4,    1,    5,    3,    3,    1,    2,
    2,    1,    3,    1,    2,    3,    3,    1,    2,    2,
    1,    5,    1,    4,    4,    4,    4,    4,    3,    1,
    2,    1,    1,    4,    1,    3,    3,   13,    1,    1,
   12,    3,    4,    3,    1,    2,    3,    3,    3,    3,
    1,    2,    2,    2,    3,    3,    1,    1,    1,    3,
    3,    3,    3,    3,    3,    1,    1,    3,    3,    3,
    3,    1,    1,    4,    1,    4,    1,    4,    4,    4,
    1,    1,    1,    1,    1,    1,    3,    1,    1,    2,
    3,    1,    1,    2,    3,    1,    3,    1,    1,    2,
    1,    3,    1,    1,    1,    3,    2,    1,    2,    1,
    2,    1,    3,    1,    2,    2,    1,    1,    1,    1,
    3,    1,    2,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    2,  125,    0,    0,    0,    0,    0,
  123,  124,    0,    0,    0,    9,   10,    0,   12,   13,
   14,   15,   16,   17,   21,   25,   52,   53,   55,    0,
    0,   59,   60,    0,  112,  118,  113,  119,    0,    0,
    0,    0,  101,  102,  103,  104,   92,   93,    0,    0,
    0,  105,  106,    0,    0,   29,    0,    0,   86,   71,
    0,   77,   79,   87,   95,   97,    0,    0,    0,    0,
    0,    0,    0,   38,   43,    0,    0,    0,    7,    8,
   11,    0,    0,  120,    0,    0,    0,  114,    0,    0,
    0,   65,    0,    3,  122,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   34,   30,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   22,    0,    0,  109,
    0,    0,    0,    0,    0,    0,    0,   36,   50,   39,
    1,    4,    0,    0,  117,    0,  132,    0,  139,  140,
    0,    0,  116,    0,  128,  137,  111,    0,   66,    0,
   62,    0,    0,   90,   91,    0,    0,    0,    0,  134,
  142,   28,   35,    0,    0,   24,    0,    0,    0,    0,
   70,   88,   84,   89,   85,   19,   20,    0,    0,    0,
    0,    0,    0,    0,   37,   51,    0,   63,    0,  135,
  136,    0,    0,  127,  131,    0,   64,    0,    0,    0,
  100,   98,   99,   96,    0,   94,    0,   33,    0,    0,
   45,   47,    0,   46,   44,   49,    0,    0,  115,  126,
   68,   69,   67,  141,  133,   23,   42,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   61,   58,
};
final static short yydgoto[] = {                          3,
   15,    4,   16,   17,   18,   19,   20,   21,   22,   23,
   24,  118,   25,   54,  105,   26,   55,   56,  106,   72,
  128,   73,   74,   75,  129,   27,   28,  119,   29,   30,
   31,  141,   32,   33,   34,   91,  151,   35,   92,   58,
   59,   60,   61,   62,   63,   64,  159,   65,   66,  120,
   37,  143,   38,  144,  145,  160,  146,  161,
};
final static short yysindex[] = {                      -118,
  583,  826,    0,    0,    0,  -24,  -31,    7, -225,  -30,
    0,    0,  826,  826,  606,    0,    0,  -10,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -41,
   -5,    0,    0,  622,    0,    0,    0,    0, -226,  634,
 -199,  124,    0,    0,    0,    0,    0,    0,  -33,   52,
  889,    0,    0,    2,   72,    0,  865, -189,    0,    0,
   23,    0,    0,    0,    0,    0,  262,  -67, -246, -145,
 -247,    4,   90,    0,    0,  657,  323, -128,    0,    0,
    0,  237, -121,    0,  106,  294, -226,    0,  826,  674,
  110,    0, -128,    0,    0, -189, -189, -124, -124,  237,
  237,  237,  111,  699, -187,    0,    0,   35, -154, -144,
 -124,   23,   23,  -56,  240,  113,    0,  297,   -7,    0,
  237,   -7, -112, -250, -106,  131,  717,    0,    0,    0,
    0,    0,  133,  784,    0,  294,    0, -191,    0,    0,
   15,  -84,    0, -228,    0,    0,    0,  740,    0,  -15,
    0,   23,   23,    0,    0,  -37,  -29,  -43,   50,    0,
    0,    0,    0,  756,    2,    0,   35,   23,   35,   23,
    0,    0,    0,    0,    0,    0,    0,  237,   -7,   -7,
  -78, -240,  -70,  -68,    0,    0,  773,    0,  101,    0,
    0,   69,  294,    0,    0,  -66,    0,  160,  165,  169,
    0,    0,    0,    0,  -52,    0,  237,    0,  -28,   -7,
    0,    0,  -47,    0,    0,    0,  119,  826,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  826,  792,  810,
  -18,  -14,  206,  214,  237,  237,  348,  575,  196,  199,
    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    1,    0,    0,    0,    0,
    0,    0,    0,    0,  269,    0,    0,  204,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  148,    0,    0,    0,    0,    0,
   28,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  153,    0,    0,  278,    0,   64,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  162,    0,  495,    0,    0,    0,    0,    0,
  -26,  -22,  -20,    0,    0,    0,    0,    0,  386,    0,
    0,  507,    0,    0,    0,  172,    0,    0,    0,    0,
    0,    0,    0,  550,    0,    0,    0, -201,    0,    0,
    0,  129,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   55,   89,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  116,  143,  177,  420,
    0,    0,    0,    0,    0,    0,    0,    0,  447,  523,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  138,    0,    0,    0,    0,
    0,    0,    0,    0,  150,    0,    0,    0,  546,  472,
    0,    0,  -16,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
  928,    0, 1093,    0,    0,    0,    0,    0,    0,    0,
    0,  -71,    0,    0,  136,    0,  245,    0,    0,    0,
    0,  227,    0,    0,    0,    0,    0,  986,    0,    0,
    0,  168,    0,    0,    0,    0,    0,   19,    0,  248,
  469,    0,   26,    0,    0,    0,    0, 1101,    0,    0,
    0,  130,    0,    0,    0,  105,    0,    0,
};
final static int YYTABLESIZE=1331;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        109,
  121,  110,   83,  202,    2,  109,  101,  110,   51,   71,
  134,  204,  182,  109,   73,  110,  123,   69,   72,   82,
   74,   41,  212,  183,   48,   70,  124,   78,   53,    5,
   52,    5,   78,  213,   86,  109,  195,  110,   87,   68,
  121,  121,  121,  121,  121,  121,   67,  121,   81,   88,
   11,   12,   11,   12,   80,  192,  138,   93,  193,  121,
  121,  121,  121,  138,  114,   95,  108,   78,   78,  115,
   78,   78,   78,   47,   48,    6,   98,  138,  138,  165,
  166,   99,  112,  113,  190,  191,   78,   78,   81,   78,
  206,  102,   50,  207,   80,   80,   73,   80,   80,   80,
   72,  167,   74,  112,  142,  147,   48,  112,   47,   48,
    6,  169,  107,   80,   80,   82,   80,  125,   47,   48,
    6,  152,  153,  121,  104,  121,  127,   50,   81,   81,
  130,   81,   81,   81,  168,  170,  133,   50,   47,   48,
    6,  217,   75,    6,  193,  136,    1,   81,   81,  150,
   81,  162,   78,  176,  142,   82,   82,   50,   82,   82,
   82,  181,  196,  237,  238,   98,   96,  184,   97,  130,
   99,  185,  130,  188,   82,   82,   83,   82,  129,   80,
  194,  129,   75,   75,  211,   75,   75,   75,   42,  121,
  143,  218,  214,  143,  215,   47,   48,    6,  220,  172,
  221,   75,   75,   18,   75,  222,   47,   48,    6,  223,
   49,  142,  224,   81,   50,  227,   83,   83,  201,   83,
   83,   83,  100,    6,   42,   50,  203,   43,   44,   45,
   46,   47,   48,    6,   69,   83,   83,  205,   83,  226,
   82,  228,   70,   18,   18,  235,   49,  198,  199,  200,
   50,  233,    5,  236,  241,  234,  121,  242,  121,  121,
  121,  121,  121,  121,  121,  121,  121,   75,    5,  121,
   32,  121,  121,   11,   12,   41,   85,    6,  121,  121,
  121,  121,  121,   78,   31,   78,   78,   78,   78,   78,
   78,   78,   78,   78,   40,  103,   78,  126,   78,   78,
  209,   83,  117,  189,  111,   78,   78,   78,   78,   78,
   80,  225,   80,   80,   80,   80,   80,   80,   80,   80,
   80,  112,  219,   80,    0,   80,   80,    0,   18,    0,
    0,    0,   80,   80,   80,   80,   80,  177,    0,    0,
  178,    0,  112,  112,   81,  112,   81,   81,   81,   81,
   81,   81,   81,   81,   81,    0,    0,   81,    0,   81,
   81,    0,   39,  132,    0,    0,   81,   81,   81,   81,
   81,   82,    0,   82,   82,   82,   82,   82,   82,   82,
   82,   82,    0,    0,   82,  108,   82,   82,  239,    0,
    0,  178,    0,   82,   82,   82,   82,   82,   75,    0,
   75,   75,   75,   75,   75,   75,   75,   75,   75,    0,
    0,   75,    0,   75,   75,    0,    0,    0,    0,   76,
   75,   75,   75,   75,   75,  108,  108,    0,    0,  108,
    0,    0,   83,    0,   83,   83,   83,   83,   83,   83,
   83,   83,   83,    0,  108,   83,  110,   83,   83,    0,
    0,    0,    0,    0,   83,   83,   83,   83,   83,   76,
   76,   18,   76,   76,   76,    0,    0,    0,   18,   18,
    0,  107,   18,    0,   18,   18,    0,    0,   76,   76,
    0,   76,   18,   18,    0,    0,  110,  110,    0,    0,
  110,    0,   42,    0,   27,  174,    0,    0,    0,   47,
   48,    6,   47,   48,    6,  110,   56,    0,    0,    0,
  108,  107,  107,    0,   49,  107,    0,   42,   50,  116,
    0,   50,   54,    0,   47,   48,    6,    0,    0,    0,
  107,    0,    0,    0,   27,   27,    0,    0,    0,   49,
    0,    0,    0,   50,   76,   26,   56,   56,    0,   57,
    0,    5,   42,   27,    0,    0,    0,    0,  137,   47,
   48,    6,   54,   54,    0,   56,  154,  155,  138,  139,
  140,  110,   11,   12,   49,    0,    0,    0,   50,  171,
    5,   54,  173,  175,    0,   26,   26,    6,    7,   57,
   57,    8,    0,    9,   10,    0,  107,    0,    0,    0,
    0,   11,   12,   42,   26,    0,    0,    0,   57,    0,
   47,   48,    6,    0,    0,  240,    0,    0,  178,   27,
    0,    0,   14,    0,    0,   49,    0,    0,    0,   50,
    0,   56,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  108,    0,  108,    0,   39,    0,   54,  108,  108,
  108,  108,    0,    0,  108,    0,  108,  108,    0,    0,
    0,   39,    0,  108,  108,  108,    0,  108,    0,    0,
   26,    0,    0,   39,   57,   76,    0,   76,   76,   76,
   76,   76,   76,   76,   76,   76,    0,    0,   76,    0,
   76,   76,    0,    0,    0,    0,   39,   76,   76,   76,
   76,   76,  110,    0,  110,   13,    0,    0,    0,  110,
  110,  110,  110,   39,    0,  110,    0,  110,  110,    0,
    0,    0,    0,    0,  110,  110,  110,  107,  110,  107,
   79,    0,    0,    0,  107,  107,  107,  107,   39,    0,
  107,    0,  107,  107,   89,    0,    0,    0,    0,  107,
  107,  107,   27,  107,    0,    0,   39,    0,   94,   27,
   27,    0,    0,   27,   56,   27,   27,    0,    0,    0,
    0,   56,   56,   27,   27,   56,    0,   56,   56,   39,
   54,  131,    0,    0,    0,   56,   56,   54,   54,    0,
    0,   54,    0,   54,   54,   39,    0,    0,  149,    0,
    0,   54,   54,   26,    0,    0,    0,   57,    0,    0,
   26,   26,   39,    0,   26,   57,   26,   26,   57,    0,
   57,   57,    0,  163,   26,   26,    0,  178,   57,   57,
   42,   39,    0,    0,    0,    0,    0,   47,   48,    6,
    5,  186,    0,    0,    0,    0,    0,    6,    7,   39,
    0,    8,   49,    9,   10,    0,   50,    0,    0,    0,
    0,   11,   12,    5,  197,   39,    0,    0,    0,    0,
    6,    7,    0,    0,    8,    0,    9,   10,    0,    5,
  208,    0,    0,    0,   11,   12,    6,    7,    0,    0,
    8,    5,    9,   10,    0,    0,    0,  216,    6,    7,
   11,   12,    8,    0,    9,   10,    0,  109,    0,  110,
    0,    0,   11,   12,    5,    0,  231,    0,    0,    0,
    0,    6,    7,    0,   53,    8,   52,    9,   10,   40,
    0,    5,    0,    0,  232,   11,   12,    0,    6,    7,
   76,   77,    8,    0,    9,   10,    0,    0,   53,    0,
   52,    0,   11,   12,    0,    0,    5,    0,    0,    0,
    0,   90,    0,    6,    7,    0,    0,    8,    0,    9,
   10,    0,    0,    0,    5,    0,    0,   11,   12,    0,
    0,    6,    7,    0,    0,    8,    0,    9,   10,    0,
    0,    0,   57,    0,    0,   11,   12,    5,    0,    0,
    0,    0,    0,    0,    6,    7,    0,    0,    8,    0,
    9,   10,    0,    5,    0,    0,  148,    0,   11,   12,
    6,    7,    0,    0,    8,    0,    9,   10,    0,    0,
    5,  164,    0,    0,   11,   12,   57,    6,    7,   42,
    0,    8,    0,    9,   10,    0,   47,   48,    6,    5,
    0,   11,   12,  122,  187,    0,    6,    7,    0,    0,
    8,   49,    9,   10,    0,   50,    0,    5,    0,    0,
   11,   12,    0,    0,    6,    7,    0,    0,    8,    0,
    9,   10,    0,    5,    0,  156,  157,  158,   11,   12,
    6,    7,    0,    0,    8,    0,    9,   10,    0,    0,
    0,   36,   36,  179,   11,   12,  180,   80,    0,    0,
    0,    0,    0,   36,   36,   36,    0,    0,    0,  179,
  108,    0,    0,   43,   44,   45,   46,   47,   48,    6,
   84,    0,   80,    0,   36,    0,    0,    0,    0,    0,
   36,    0,    0,    0,   42,  229,   50,   43,   44,   45,
   46,   47,   48,    6,    0,  230,    0,    0,    0,    0,
    0,    0,    0,  210,    0,    0,   49,    0,   80,   80,
   50,    0,    0,    0,    0,    0,   36,   36,    0,    0,
    0,    0,   80,  135,    0,    0,    0,    0,    0,   36,
   36,    0,  158,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   36,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  179,  179,    0,    0,    0,   36,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   80,    0,    0,    0,    0,    0,    0,    0,   36,    0,
    0,    0,    0,    0,    0,    0,   80,    0,    0,    0,
    0,    0,    0,    0,   36,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   80,
    0,    0,    0,    0,    0,    0,    0,   36,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   36,    0,
    0,   80,   80,    0,    0,    0,    0,    0,   36,   36,
   36,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         43,
    0,   45,   44,   41,  123,   43,   40,   45,   40,   40,
   82,   41,  263,   43,   41,   45,  263,  265,   41,   61,
   41,   46,  263,  274,   41,  273,  273,    0,   60,  258,
   62,  258,   14,  274,   40,   43,  265,   45,   44,  265,
   40,   41,   42,   43,   44,   45,   40,   47,   59,   31,
  279,  280,  279,  280,    0,   41,  258,   39,   44,   59,
   60,   61,   62,  265,   42,  265,  256,   40,   41,   47,
   43,   44,   45,  263,  264,  265,   42,  279,  280,  267,
  268,   47,   57,   58,  276,  277,   59,   60,    0,   62,
   41,   40,  282,   44,   40,   41,  123,   43,   44,   45,
  123,  256,  123,   40,   86,   87,  123,   44,  263,  264,
  265,  256,   41,   59,   60,    0,   62,  263,  263,  264,
  265,   96,   97,  123,  123,  125,  123,  282,   40,   41,
   41,   43,   44,   45,  109,  110,  265,  282,  263,  264,
  265,   41,    0,  265,   44,   40,  265,   59,   60,   40,
   62,   41,  125,   41,  136,   40,   41,  282,   43,   44,
   45,  274,  144,  235,  236,   42,   43,  274,   45,   41,
   47,   41,   44,   41,   59,   60,    0,   62,   41,  125,
  265,   44,   40,   41,  263,   43,   44,   45,  256,  257,
   41,  123,  263,   44,  263,  263,  264,  265,  265,  256,
   41,   59,   60,    0,   62,   41,  263,  264,  265,   41,
  278,  193,  265,  125,  282,  263,   40,   41,  256,   43,
   44,   45,  256,  265,  256,  282,  256,  259,  260,  261,
  262,  263,  264,  265,  265,   59,   60,  281,   62,  268,
  125,  123,  273,   40,   41,   40,  278,  263,  264,  265,
  282,  270,  258,   40,   59,  270,  256,   59,  258,  259,
  260,  261,  262,  263,  264,  265,  266,  125,    0,  269,
  123,  271,  272,  279,  280,  123,  282,    0,  278,  279,
  280,  281,  282,  256,  123,  258,  259,  260,  261,  262,
  263,  264,  265,  266,  123,   51,  269,   71,  271,  272,
  165,  125,   41,  136,   57,  278,  279,  280,  281,  282,
  256,  207,  258,  259,  260,  261,  262,  263,  264,  265,
  266,  258,  193,  269,   -1,  271,  272,   -1,  125,   -1,
   -1,   -1,  278,  279,  280,  281,  282,   41,   -1,   -1,
   44,   -1,  279,  280,  256,  282,  258,  259,  260,  261,
  262,  263,  264,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   40,   41,   -1,   -1,  278,  279,  280,  281,
  282,  256,   -1,  258,  259,  260,  261,  262,  263,  264,
  265,  266,   -1,   -1,  269,    0,  271,  272,   41,   -1,
   -1,   44,   -1,  278,  279,  280,  281,  282,  256,   -1,
  258,  259,  260,  261,  262,  263,  264,  265,  266,   -1,
   -1,  269,   -1,  271,  272,   -1,   -1,   -1,   -1,    0,
  278,  279,  280,  281,  282,   40,   41,   -1,   -1,   44,
   -1,   -1,  256,   -1,  258,  259,  260,  261,  262,  263,
  264,  265,  266,   -1,   59,  269,    0,  271,  272,   -1,
   -1,   -1,   -1,   -1,  278,  279,  280,  281,  282,   40,
   41,  258,   43,   44,   45,   -1,   -1,   -1,  265,  266,
   -1,    0,  269,   -1,  271,  272,   -1,   -1,   59,   60,
   -1,   62,  279,  280,   -1,   -1,   40,   41,   -1,   -1,
   44,   -1,  256,   -1,    0,  256,   -1,   -1,   -1,  263,
  264,  265,  263,  264,  265,   59,    0,   -1,   -1,   -1,
  125,   40,   41,   -1,  278,   44,   -1,  256,  282,  258,
   -1,  282,    0,   -1,  263,  264,  265,   -1,   -1,   -1,
   59,   -1,   -1,   -1,   40,   41,   -1,   -1,   -1,  278,
   -1,   -1,   -1,  282,  125,    0,   40,   41,   -1,    0,
   -1,  258,  256,   59,   -1,   -1,   -1,   -1,  265,  263,
  264,  265,   40,   41,   -1,   59,   98,   99,  275,  276,
  277,  125,  279,  280,  278,   -1,   -1,   -1,  282,  111,
  258,   59,  114,  115,   -1,   40,   41,  265,  266,   40,
   41,  269,   -1,  271,  272,   -1,  125,   -1,   -1,   -1,
   -1,  279,  280,  256,   59,   -1,   -1,   -1,   59,   -1,
  263,  264,  265,   -1,   -1,   41,   -1,   -1,   44,  125,
   -1,   -1,   40,   -1,   -1,  278,   -1,   -1,   -1,  282,
   -1,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  256,   -1,  258,   -1,   40,   -1,  125,  263,  264,
  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,   -1,
   -1,   40,   -1,  278,  279,  280,   -1,  282,   -1,   -1,
  125,   -1,   -1,   40,  125,  256,   -1,  258,  259,  260,
  261,  262,  263,  264,  265,  266,   -1,   -1,  269,   -1,
  271,  272,   -1,   -1,   -1,   -1,   40,  278,  279,  280,
  281,  282,  256,   -1,  258,  123,   -1,   -1,   -1,  263,
  264,  265,  266,   40,   -1,  269,   -1,  271,  272,   -1,
   -1,   -1,   -1,   -1,  278,  279,  280,  256,  282,  258,
  125,   -1,   -1,   -1,  263,  264,  265,  266,   40,   -1,
  269,   -1,  271,  272,  123,   -1,   -1,   -1,   -1,  278,
  279,  280,  258,  282,   -1,   -1,   40,   -1,  125,  265,
  266,   -1,   -1,  269,  258,  271,  272,   -1,   -1,   -1,
   -1,  265,  266,  279,  280,  269,   -1,  271,  272,   40,
  258,  125,   -1,   -1,   -1,  279,  280,  265,  266,   -1,
   -1,  269,   -1,  271,  272,   40,   -1,   -1,  125,   -1,
   -1,  279,  280,  258,   -1,   -1,   -1,  258,   -1,   -1,
  265,  266,   40,   -1,  269,  266,  271,  272,  269,   -1,
  271,  272,   -1,  125,  279,  280,   -1,   44,  279,  280,
  256,   40,   -1,   -1,   -1,   -1,   -1,  263,  264,  265,
  258,  125,   -1,   -1,   -1,   -1,   -1,  265,  266,   40,
   -1,  269,  278,  271,  272,   -1,  282,   -1,   -1,   -1,
   -1,  279,  280,  258,  125,   40,   -1,   -1,   -1,   -1,
  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,  258,
  125,   -1,   -1,   -1,  279,  280,  265,  266,   -1,   -1,
  269,  258,  271,  272,   -1,   -1,   -1,  125,  265,  266,
  279,  280,  269,   -1,  271,  272,   -1,   43,   -1,   45,
   -1,   -1,  279,  280,  258,   -1,  125,   -1,   -1,   -1,
   -1,  265,  266,   -1,   60,  269,   62,  271,  272,    2,
   -1,  258,   -1,   -1,  125,  279,  280,   -1,  265,  266,
   13,   14,  269,   -1,  271,  272,   -1,   -1,   60,   -1,
   62,   -1,  279,  280,   -1,   -1,  258,   -1,   -1,   -1,
   -1,   34,   -1,  265,  266,   -1,   -1,  269,   -1,  271,
  272,   -1,   -1,   -1,  258,   -1,   -1,  279,  280,   -1,
   -1,  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,
   -1,   -1,    7,   -1,   -1,  279,  280,  258,   -1,   -1,
   -1,   -1,   -1,   -1,  265,  266,   -1,   -1,  269,   -1,
  271,  272,   -1,  258,   -1,   -1,   89,   -1,  279,  280,
  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,   -1,
  258,  104,   -1,   -1,  279,  280,   51,  265,  266,  256,
   -1,  269,   -1,  271,  272,   -1,  263,  264,  265,  258,
   -1,  279,  280,   68,  127,   -1,  265,  266,   -1,   -1,
  269,  278,  271,  272,   -1,  282,   -1,  258,   -1,   -1,
  279,  280,   -1,   -1,  265,  266,   -1,   -1,  269,   -1,
  271,  272,   -1,  258,   -1,  100,  101,  102,  279,  280,
  265,  266,   -1,   -1,  269,   -1,  271,  272,   -1,   -1,
   -1,    1,    2,  118,  279,  280,  121,   15,   -1,   -1,
   -1,   -1,   -1,   13,   14,   15,   -1,   -1,   -1,  134,
  256,   -1,   -1,  259,  260,  261,  262,  263,  264,  265,
   30,   -1,   40,   -1,   34,   -1,   -1,   -1,   -1,   -1,
   40,   -1,   -1,   -1,  256,  218,  282,  259,  260,  261,
  262,  263,  264,  265,   -1,  228,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,  178,   -1,   -1,  278,   -1,   76,   77,
  282,   -1,   -1,   -1,   -1,   -1,   76,   77,   -1,   -1,
   -1,   -1,   90,   83,   -1,   -1,   -1,   -1,   -1,   89,
   90,   -1,  207,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  104,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  237,  238,   -1,   -1,   -1,  127,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  148,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  148,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  164,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  164,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  187,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  187,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  218,   -1,
   -1,  229,  230,   -1,   -1,   -1,   -1,   -1,  228,  229,
  230,
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
"LE","TOI","INT","FLOAT","ARROW","FUN","SENTENCIA_ASIGNACION_PREC",
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

//#line 348 "grammar.y"

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
//#line 751 "Parser.java"
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
case 18:
//#line 68 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
break;
case 22:
//#line 78 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
break;
case 25:
//#line 84 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
break;
case 30:
//#line 98 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
break;
case 31:
//#line 99 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
break;
case 32:
//#line 100 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
break;
case 35:
//#line 109 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}
break;
case 39:
//#line 121 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de iteracion");}
break;
case 40:
//#line 122 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de iteracion");}
break;
case 41:
//#line 123 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de iteracion");}
break;
case 44:
//#line 132 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado ID del for");}
break;
case 45:
//#line 133 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado FROM del for");}
break;
case 46:
//#line 134 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_1 del for");}
break;
case 47:
//#line 135 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado TO del for");}
break;
case 48:
//#line 136 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_2 del for");}
break;
case 51:
//#line 145 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
break;
case 56:
//#line 159 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de asignacion luego de var");}
break;
case 61:
//#line 173 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion");}
break;
case 66:
//#line 189 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta delimitador izquierdo '{' del cuerpo lambda");}
break;
case 72:
//#line 205 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
break;
case 73:
//#line 206 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
break;
case 74:
//#line 207 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
break;
case 80:
//#line 219 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '+''");}
break;
case 81:
//#line 220 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '-'");}
break;
case 82:
//#line 221 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '+'");}
break;
case 83:
//#line 222 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 88:
//#line 233 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '*'");}
break;
case 89:
//#line 234 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
break;
case 90:
//#line 235 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
break;
case 91:
//#line 236 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
break;
case 98:
//#line 252 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '(' en expresion_aritmetica TOI");}
break;
case 99:
//#line 253 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ')' en expresion_aritmetica TOI");}
break;
case 100:
//#line 254 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de '()' en expresion_aritmetica TOI");}
break;
case 110:
//#line 273 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado derecho)");}
break;
case 114:
//#line 282 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de tipos)");}
break;
case 120:
//#line 296 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
break;
case 129:
//#line 317 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 130:
//#line 318 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
break;
case 131:
//#line 319 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 132:
//#line 320 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
break;
case 138:
//#line 335 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de LE o SE despues de CR");}
break;
case 139:
//#line 336 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de SE");}
break;
case 140:
//#line 337 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de LE");}
break;
case 143:
//#line 345 "grammar.y"
{Logger.logError(cursor.getCurrentLine(), "Falta especificacion del parametro formal");}
break;
//#line 1096 "Parser.java"
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
