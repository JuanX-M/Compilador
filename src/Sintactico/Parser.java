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
//#line 26 "Parser.java"




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
public final static short UMINUS=282;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    2,    3,    4,    4,    4,
    4,    4,    4,    4,    5,    5,    5,    6,    9,    9,
    9,    9,    9,    9,    9,    8,    8,    8,    8,    8,
    8,    7,    7,   12,   12,   13,   13,   14,   14,   14,
   14,   10,   10,   17,   17,   11,   11,   11,   15,   15,
   16,   16,   18,   18,   19,
};
final static short yylen[] = {                            2,
    4,    2,    1,    1,    2,    1,    1,    4,   12,    8,
    4,    4,   11,    3,   10,   10,   10,   13,    3,    3,
    3,    3,    2,    4,    1,    3,    3,    3,    3,    3,
    3,    3,    1,    3,    1,    3,    1,    1,    1,    4,
    1,    3,    1,    1,    3,    1,    1,    1,    3,    2,
    3,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,   48,    0,    0,    0,    0,    0,   46,
   47,    0,    0,    3,    4,    0,    6,    7,    0,   35,
    0,   43,    0,    0,    0,    0,    0,    0,    1,    2,
    5,    0,    0,    0,    0,   45,   38,   39,    0,    0,
    0,    0,    0,   25,   41,    0,    0,    0,    0,    0,
    0,    0,   42,    0,   34,    0,    0,   23,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   11,
   12,    0,    0,    0,    0,    0,    0,    0,   37,    0,
    0,    0,   52,    0,    0,    0,    0,    0,    0,   19,
   20,   21,   22,    0,    0,    0,    0,    0,   53,   54,
   50,    0,    0,    0,    0,   40,    0,   24,    0,    0,
    0,    0,   36,   49,   55,   51,    0,    0,    0,    0,
    0,   10,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   16,   17,   15,    0,    0,   13,    0,
    9,    0,   18,
};
final static short yydgoto[] = {                          2,
   13,   14,   15,   16,   17,   18,   51,   42,   52,   19,
   20,   21,   78,   44,   79,   82,   45,   80,   83,
};
final static short yysindex[] = {                      -256,
  -91,    0,   37,    0,   -1,   22,   24, -197,   45,    0,
    0, -242,  -35,    0,    0,   36,    0,    0,   10,    0,
  -33,    0, -187,  -37,   49, -176, -174, -166,    0,    0,
    0,  -37, -162,   68, -242,    0,    0,    0,    4,   77,
  -37,   80,   27,    0,    0,   91,   64,  -37, -137,  100,
   98,   41,    0, -232,    0, -140,  -37,    0,   29,  -37,
  -37,  -37,  -37, -140, -140, -140, -140,  -37,  -37,    0,
    0,   98, -115,   30,  -37, -263, -111,   52,    0, -242,
 -126,   69,    0,  104,   37,   41,   41,   41,   41,    0,
    0,    0,    0,   41,   41, -118,   37,   41,    0,    0,
    0,   34, -232, -107, -232,    0, -140,    0,  -23, -104,
  -11,   37,    0,    0,    0,    0, -216,  119,  121,    1,
   39,    0,   40, -135, -106,   37,   37,  124,  125,  126,
  128,   13,   25,    0,    0,    0,  -37,  -99,    0,   71,
    0,  111,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,   19,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  -41,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  112,  -13,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  113,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  132,  133,  134,  135,    0,
    0,    0,    0,  136,  137,    0,    0,   38,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,
};
final static short yygindex[] = {                         0,
  -70,   11,    0,    0,    0,    0,  -36,    0,   -2,    0,
   -5,    0,    0,  -31,   32,    0,    7,    0,   72,
};
final static int YYTABLESIZE=327;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         44,
   44,   44,   44,   44,   12,   44,   28,   41,    1,   22,
   35,   72,   99,  100,  109,    4,   12,   44,   44,   22,
   44,   43,   47,   30,   81,    4,  111,   33,   12,   55,
   33,    3,   90,   91,   92,   93,   10,   11,   58,   53,
   12,  120,   76,   56,   23,   33,   10,   11,   77,   23,
  121,  122,   12,   33,   84,  132,  133,   86,   87,   88,
   89,   24,   44,   25,   12,   94,   95,   26,   66,   64,
   32,   65,   98,   67,  104,   81,   12,   36,   32,   44,
   48,   32,   66,   64,   27,   65,   69,   67,   68,   29,
   49,   22,  102,   41,   31,  103,   32,   77,   50,   77,
  140,  117,    5,   22,   71,   66,   64,   54,   65,  106,
   67,  142,  107,  119,   75,   22,   57,   22,   22,   30,
   59,   30,   37,   38,   39,  125,   22,  128,  129,  130,
   30,   70,   22,   22,  113,   73,  115,  138,   22,   22,
   74,   75,   30,   30,  108,   66,   64,   96,   65,  139,
   67,   85,   97,  101,  105,  110,  112,  114,  118,  123,
  124,  126,  127,  131,  134,  135,  136,  137,  141,  143,
   14,    8,   26,   27,   28,   29,   30,   31,  116,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   44,   44,   44,
   44,    0,    4,    0,    0,   37,   38,   39,    0,    5,
    6,   34,    0,    7,    4,    8,    9,    0,    0,   44,
   40,    5,    6,   10,   11,    7,    4,    8,    9,    0,
    0,    0,    0,    5,    6,   10,   11,    7,    4,    8,
    9,    0,    0,    0,    0,    5,    6,   10,   11,    7,
    4,    8,    9,    0,    0,    0,    0,    5,    6,   10,
   11,    7,    4,    8,    9,   60,   61,   62,   63,    5,
    6,   10,   11,    7,    4,    8,    9,    0,    0,    0,
    0,    5,    6,   10,   11,    7,   46,    8,    9,    0,
    0,   37,   38,   39,    0,   10,   11,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   40,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   40,   47,   12,   45,  265,    3,
   44,   48,  276,  277,   85,  258,   40,   59,   60,   13,
   62,   24,   25,   13,   56,  258,   97,   41,   40,   35,
   44,  123,   64,   65,   66,   67,  279,  280,   41,   33,
   40,  112,  275,   40,   46,   59,  279,  280,   54,   46,
  267,  268,   40,   44,   57,  126,  127,   60,   61,   62,
   63,   40,   44,   40,   40,   68,   69,  265,   42,   43,
   61,   45,   75,   47,   80,  107,   40,  265,   41,   61,
  257,   44,   42,   43,   40,   45,   60,   47,   62,  125,
  265,   85,   41,   45,   59,   44,   59,  103,  265,  105,
  137,  125,  265,   97,   41,   42,   43,   40,   45,   41,
   47,   41,   44,  125,   44,  109,   40,  111,  112,  109,
   41,  111,  263,  264,  265,  125,  120,  263,  264,  265,
  120,   41,  126,  127,  103,  273,  105,  125,  132,  133,
   41,   44,  132,  133,   41,   42,   43,  263,   45,  125,
   47,  123,  123,  265,  281,  274,  123,  265,  263,   41,
   40,  123,  123,  270,   41,   41,   41,   40,  268,   59,
   59,   59,   41,   41,   41,   41,   41,   41,  107,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,  260,  261,
  262,   -1,  258,   -1,   -1,  263,  264,  265,   -1,  265,
  266,  265,   -1,  269,  258,  271,  272,   -1,   -1,  281,
  278,  265,  266,  279,  280,  269,  258,  271,  272,   -1,
   -1,   -1,   -1,  265,  266,  279,  280,  269,  258,  271,
  272,   -1,   -1,   -1,   -1,  265,  266,  279,  280,  269,
  258,  271,  272,   -1,   -1,   -1,   -1,  265,  266,  279,
  280,  269,  258,  271,  272,  259,  260,  261,  262,  265,
  266,  279,  280,  269,  258,  271,  272,   -1,   -1,   -1,
   -1,  265,  266,  279,  280,  269,  258,  271,  272,   -1,
   -1,  263,  264,  265,   -1,  279,  280,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  278,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=282;
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
"LE","TOI","INT","FLOAT","ARROW","UMINUS",
};
final static String yyrule[] = {
"$accept : prog",
"prog : ID '{' cuerpo '}'",
"cuerpo : cuerpo sentencia",
"cuerpo : sentencia",
"sentencia : sentencia_declarativa",
"sentencia : sentencia_ejecucion ';'",
"sentencia : sentencia_lambda",
"sentencia_declarativa : funcion",
"sentencia_ejecucion : VAR ID TWO_POINTS_ASSIGNATION lista_exp_aritmeticas",
"sentencia_ejecucion : IF '(' condicion ')' '{' cuerpo '}' ELSE '{' cuerpo '}' ENDIF",
"sentencia_ejecucion : IF '(' condicion ')' '{' cuerpo '}' ENDIF",
"sentencia_ejecucion : PRINT '(' STRING ')'",
"sentencia_ejecucion : PRINT '(' expresion_aritmetica ')'",
"sentencia_ejecucion : FOR '(' ID FROM CTE_INT TO CTE_INT ')' '{' cuerpo '}'",
"sentencia_ejecucion : lista_variables '=' lista_exp_aritmeticas",
"sentencia_lambda : '(' tipo ID ')' '{' cuerpo '}' '(' ID ')'",
"sentencia_lambda : '(' tipo ID ')' '{' cuerpo '}' '(' CTE_INT ')'",
"sentencia_lambda : '(' tipo ID ')' '{' cuerpo '}' '(' CTE_FLOAT ')'",
"funcion : lista_tipos ID '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'",
"expresion_aritmetica : expresion_aritmetica '+' operando",
"expresion_aritmetica : expresion_aritmetica '-' operando",
"expresion_aritmetica : expresion_aritmetica '*' operando",
"expresion_aritmetica : expresion_aritmetica '/' operando",
"expresion_aritmetica : '-' expresion_aritmetica",
"expresion_aritmetica : TOI '(' expresion_aritmetica ')'",
"expresion_aritmetica : operando",
"condicion : expresion_aritmetica GREATER_OR_EQUAL expresion_aritmetica",
"condicion : expresion_aritmetica LESS_OR_EQUAL expresion_aritmetica",
"condicion : expresion_aritmetica EQUAL expresion_aritmetica",
"condicion : expresion_aritmetica NOT_EQUAL expresion_aritmetica",
"condicion : expresion_aritmetica '>' expresion_aritmetica",
"condicion : expresion_aritmetica '<' expresion_aritmetica",
"lista_exp_aritmeticas : lista_exp_aritmeticas ',' expresion_aritmetica",
"lista_exp_aritmeticas : expresion_aritmetica",
"lista_tipos : lista_tipos ',' tipo",
"lista_tipos : tipo",
"lista_param_formales : lista_param_formales ',' parametro_formal",
"lista_param_formales : parametro_formal",
"operando : CTE_INT",
"operando : CTE_FLOAT",
"operando : ID '(' lista_param_reales ')'",
"operando : variable",
"lista_variables : lista_variables ',' variable",
"lista_variables : variable",
"variable : ID",
"variable : ID '.' ID",
"tipo : INT",
"tipo : FLOAT",
"tipo : STRING",
"parametro_formal : semantica_pasaje tipo ID",
"parametro_formal : tipo ID",
"lista_param_reales : lista_param_reales ',' parametro_real",
"lista_param_reales : parametro_real",
"semantica_pasaje : CR SE",
"semantica_pasaje : CR LE",
"parametro_real : operando ARROW parametro_formal",
};

//#line 120 "grammar.y"

private static int yylval_recognition = 0;

static AnalizadorLexico lex = null;
static Parser par = null;

public static void main (String [] args) {

    System.out.println("Iniciando compilación ... ");
    String programa = "samplePrograms/testing.txt";



    TablaPalabrasReservadas tablaPalabrasReservadas = new TablaPalabrasReservadas();
    tablaPalabrasReservadas.cargarTabla();

    lex = new AnalizadorLexico (programa) ;
    System.out.println(lex.getTodosLosTokens());
    par = new Parser (false);
    par.run () ;



    System.out.println("TablaSimbolos: " + TablaSimbolos.TABLA_SIMBOLOS);
    System.out.println(Logger.generateLog());

    System.out.println("Fin compilación");
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
//#line 405 "Parser.java"
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
