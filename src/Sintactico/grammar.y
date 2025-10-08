%{
    import java.io.*;

    import Lexico.AnalizadorLexico;

    import Tools.TablaSimbolos;
    import Tools.Pair;
    import Tools.TablaPalabrasReservadas;
    import Tools.Logger;
    import Tools.Cursor;

%}

%token TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR FOR FROM TO CR SE LE TOI INT FLOAT ARROW

%right TWO_POINTS_ASSIGNATION
%left '+' '-'
%left '*' '/'
%right UMINUS

%start  prog

%%

prog
    :   ID '{' cuerpo '}'
    |   prog_error
    ;

prog_error
    :   '{' cuerpo '}'      {Logger.logError(cursor.getCurrentLine(), "Falta el nombre del programa");}
//    |   ID '(' cuerpo ')'   {Logger.logError(cursor.getCurrentLine(), "Debe indicar el programa entre {}");}
    |   ID cuerpo           {Logger.logError(cursor.getCurrentLine(), "Faltan los delimitadores de programa");}
    |   ID '{' cuerpo       {Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '}'");}
    |   ID cuerpo '}'       {Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '{'");}
    ;

cuerpo
    :   cuerpo sentencia
    |   sentencia
    ;

sentencia
    :   sentencia_declarativa
    |   sentencia_ejecucion
    |   sentencia_ejecucion_error    {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
    |   sentencia_lambda
    ;

sentencia_declarativa
    :   funcion
    ;

sentencia_ejecucion
    :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica ';'
    |   IF '(' condicion ')' '{' cuerpo '}' ELSE '{' cuerpo '}' ENDIF ';'
    |   IF '(' condicion ')' '{' cuerpo '}' ENDIF ';'
    |   PRINT '(' STRING ')' ';'
    |   PRINT '(' expresion_aritmetica ')' ';'
    |   FOR '(' ID FROM CTE_INT TO CTE_INT ')' '{' cuerpo '}' ';'
    |   lista_variables '=' lista_exp_aritmeticas ';'
    ;

sentencia_ejecucion_error
    :   sentencia_ejecucion
    ;

sentencia_lambda
    :   '(' tipo ID ')' '{' cuerpo '}' '(' ID ')'
    |   '(' tipo ID ')' '{' cuerpo '}' '(' CTE_INT ')'
    |   '(' tipo ID ')' '{' cuerpo '}' '(' CTE_FLOAT ')'
    ;

funcion
    :   lista_tipos ID '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'
    ;

expresion_aritmetica
    :   expresion_aritmetica '+' expresion_aritmetica
    |   expresion_aritmetica '-' expresion_aritmetica
    |   expresion_aritmetica '*' expresion_aritmetica
    |   expresion_aritmetica '/' expresion_aritmetica
    |   '-' expresion_aritmetica %prec UMINUS
    |   TOI '(' expresion_aritmetica ')'
    |   operando
    ;

condicion
    :   expresion_aritmetica GREATER_OR_EQUAL expresion_aritmetica
    |   expresion_aritmetica LESS_OR_EQUAL expresion_aritmetica
    |   expresion_aritmetica EQUAL expresion_aritmetica
    |   expresion_aritmetica NOT_EQUAL expresion_aritmetica
    |   expresion_aritmetica '>' expresion_aritmetica
    |   expresion_aritmetica '<' expresion_aritmetica
    ;

lista_exp_aritmeticas
    :   lista_exp_aritmeticas ',' expresion_aritmetica
    |   expresion_aritmetica
    ;

lista_tipos
    :   lista_tipos ',' tipo
    |   tipo
    ;

lista_param_formales
    :   lista_param_formales ',' parametro_formal
    |   parametro_formal
    ;

operando
    :   CTE_INT
    |   CTE_FLOAT
    |   ID '(' lista_param_reales ')'
    |   variable
    ;

lista_variables
    :   lista_variables ',' variable
    |   variable
    ;

variable
    :   ID
    |   ID'.'ID
    ;

tipo
    :   INT
    |   FLOAT
    |   STRING
    ;

parametro_formal
    :   semantica_pasaje tipo ID
    |   tipo ID
    ;

lista_param_reales
    :   lista_param_reales ',' parametro_real
    |   parametro_real
    ;

semantica_pasaje
    :   CR SE
    |   CR LE
    ;

parametro_real
    :   expresion_aritmetica ARROW ID
    ;

%%

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