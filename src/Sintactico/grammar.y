%{
    import java.io.*;

    import Lexico.AnalizadorLexico;

    import Tools.TablaSimbolos;
    import Tools.Pair;
    import Tools.TablaPalabrasReservadas;
    import Tools.Logger;
    import Tools.Cursor;

%}

%token TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR FOR FROM TO CR SE LE TOI INT FLOAT ARROW FUN

%right TWO_POINTS_ASSIGNATION
%right UMINUS

%start  prog

%%

prog
    :   ID '{' cuerpo '}'
    |   prog_error
    ;

prog_error
    :   '{' cuerpo '}'      {Logger.logError(cursor.getCurrentLine(), "Falta el nombre del programa");}
    |   ID '(' cuerpo ')'   {Logger.logError(cursor.getCurrentLine(), "Debe indicar el programa entre {}");}
    |   ID cuerpo           {Logger.logError(cursor.getCurrentLine(), "Faltan los delimitadores de programa");}
    |   ID '{' cuerpo       {Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '}'");}
    |   ID cuerpo '}'       {Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '{'");}
    |   error               {Logger.logError(cursor.getCurrentLine(), "Hay errores lexicos o sintaticos no identificados");}
    ;

cuerpo
    :   cuerpo sentencia
    |   sentencia
    ;

sentencia
    :   sentencia_declarativa
    |   sentencia_ejecucion ';'
    |   sentencia_ejecucion_sin_coma
//    |   sentencia_lambda
//    |   sentencia_error
    ;

//sentencia_error
//    :   error {Logger.logError(cursor.getCurrentLine(), "Hay errores lexicos o sintaticos no identificados");}
//    ;

sentencia_declarativa
    :   funcion
    ;

sentencia_ejecucion
    :   sentencia_print
    |   sentencia_seleccion
    |   sentencia_iteracion
    |   sentencia_asignacion
    ;

sentencia_ejecucion_sin_coma
    :   sentencia_ejecucion     {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
    ;

sentencia_print
    :   PRINT '(' STRING ')'
    |   PRINT '(' lista_exp_aritmeticas ')'
    |   sentencia_print_error
    ;

sentencia_print_error
    :   PRINT '(' ')'       {Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
    ;

sentencia_seleccion
    :   IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion ENDIF
    |   IF parametros_seleccion cuerpo_seleccion ENDIF
    |   sentencia_seleccion_sin_endif  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;

sentencia_seleccion_sin_endif
    :   IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion
    |   IF parametros_seleccion cuerpo_seleccion
    ;

parametros_seleccion
    :    '(' condicion ')'
    |   parametros_seleccion_error
    ;

parametros_seleccion_error
    :   condicion ')'   {Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
    |   '(' condicion   {Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
    |   condicion       {Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
    ;

cuerpo_seleccion
    :   '{' cuerpo '}'
    |   cuerpo_seleccion_error
    ;

cuerpo_seleccion_error
    :   '{' '}' {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}

sentencia_iteracion
    :   FOR parametros_iteracion cuerpo_iteracion
    ;

parametros_iteracion
    :   '(' encabezado_iteracion ')'
    |   parametros_iteracion_error
    ;

parametros_iteracion_error
    :   encabezado_iteracion ')'    {Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de iteracion");}
    |   '(' encabezado_iteracion    {Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de iteracion");}
    |   encabezado_iteracion        {Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de iteracion");}
    ;

encabezado_iteracion
    :   ID FROM CTE_INT TO CTE_INT
    |   encabezado_iteracion_error
    ;

encabezado_iteracion_error
    :   FROM CTE_INT TO CTE_INT {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado ID del for");}
    |   ID CTE_INT TO CTE_INT   {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado FROM del for");}
    |   ID FROM TO CTE_INT      {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_1 del for");}
    |   ID FROM CTE_INT CTE_INT {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado TO del for");}
    |   ID FROM CTE_INT TO      {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_2 del for");}
    ;

cuerpo_iteracion
    :   '{' cuerpo '}'
    |   cuerpo_iteracion_error
    ;

cuerpo_iteracion_error
    :   '{'  '}'    {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
    ;

sentencia_asignacion
    :   sentencia_asignacion_unaria
    |   sentencia_asignacion_multiple
    ;

sentencia_asignacion_unaria
    :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica
    |   sentencia_asignacion_unaria_error


sentencia_asignacion_unaria_error
    :   VAR ID expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion luego de var");}
    ;

sentencia_asignacion_multiple
    :   lista_variables '=' lista_exp_aritmeticas
    ;

funcion
    :   lista_tipos FUN ID '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'
    |   sentencia_lambda
    |   funcion_error
    ;

funcion_error
    :   lista_tipos FUN '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'     {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion");}
    ;

sentencia_lambda
    :   parametro_lambda cuerpo_lambda argumento_lambda
    ;

parametro_lambda
    :    '(' tipo ID ')'
    ;

cuerpo_lambda
    :   '{' cuerpo '}'
    ;

argumento_lambda
    :   '(' ID ')'
    |   '(' CTE_INT ')'
    |   '(' CTE_FLOAT ')'
    ;

condicion
    :   expresion_aritmetica simbolo_comparador factor
    |   condicion_error
    ;

condicion_error
    :   expresion_aritmetica termino               {Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
    |   expresion_aritmetica simbolo_comparador    {Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
    |   simbolo_comparador termino                 {Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
    ;

expresion_aritmetica
    :   expresion_aritmetica '+' termino
    |   expresion_aritmetica '-' termino
    |   expresion_aritmetica_toi
    |   termino
    |   expresion_aritmetica_error
    ;

expresion_aritmetica_error
    :   error '+' termino               {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '+''");}
    |   error '-' termino               {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '-'");}
    |   expresion_aritmetica '+' error  {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '+'");}
    |   expresion_aritmetica '-' error  {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
    ;

termino
    :   termino '*' factor
    |   termino '/' factor
    |   factor
    |   termino_error
    ;

termino_error
    :   termino '*' error   {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '*'");}
    |   termino '/' error   {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
    |   error '*' factor    {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
    |   error '/' factor    {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
    ;

factor
    :   CTE_INT
    |   CTE_FLOAT
    |   ID '(' lista_param_reales ')'
    |   variable
    ;

expresion_aritmetica_toi
    :   TOI '(' expresion_aritmetica ')'
    |   expresion_aritmetica_toi_error
    ;

expresion_aritmetica_toi_error
    :   TOI error expresion_aritmetica ')'      {Logger.logError(cursor.getCurrentLine(), "Falta de '(' en expresion_aritmetica TOI");}
    |   TOI '(' expresion_aritmetica error      {Logger.logError(cursor.getCurrentLine(), "Falta de ')' en expresion_aritmetica TOI");}
    |   TOI error expresion_aritmetica error    {Logger.logError(cursor.getCurrentLine(), "Falta de '()' en expresion_aritmetica TOI");}
    ;

simbolo_comparador
    :   GREATER_OR_EQUAL
    |   LESS_OR_EQUAL
    |   EQUAL
    |   NOT_EQUAL
    |   '>'
    |   '<'

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

lista_variables
    :   lista_variables ',' variable
    |   variable
    |   lista_variables_error
    ;

lista_variables_error
    :   variable_error variable_error    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
    ;

variable
    :   ID
    |   ID '.' ID
    ;

variable_error
    :   variable
    ;

tipo
    :   INT
    |   FLOAT
    |   STRING
    ;

parametro_formal
    :   semantica_pasaje tipo ID
    |   tipo ID
    |   parametro_formal_error
    ;

parametro_formal_error
    :   semantica_pasaje tipo   {Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
    |   tipo                    {Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");}
    |   semantica_pasaje ID     {Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
    |   ID                      {Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");}
    ;

lista_param_reales
    :   lista_param_reales ',' parametro_real
    |   parametro_real
    ;

semantica_pasaje
    :   CR SE
    |   CR LE
    |   semantica_pasaje_error
    ;

semantica_pasaje_error
    :   CR  {Logger.logError(cursor.getCurrentLine(), "Falta de LE o SE despues de CR");}
    |   SE  {Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de SE");}
    |   LE  {Logger.logError(cursor.getCurrentLine(), "Falta de CR antes de LE");}
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