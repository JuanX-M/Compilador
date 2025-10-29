%{
    import java.io.*;
    import java.util.Scanner;
    import java.util.Stack;

    import Lexico.AnalizadorLexico;

    import Tools.TablaSimbolos;
    import Tools.Pair;
    import Tools.TablaPalabrasReservadas;
    import Tools.Logger;
    import Tools.Cursor;
    import Tools.Info;
    import Tools.Terceto;

%}

%token TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR FOR FROM TO CR SE LE TOI INT FLOAT ARROW FUN CTE_INT_NEGATIVE

%nonassoc SENTENCIA_ASIGNACION_PREC
%right TWO_POINTS_ASSIGNATION
%left ID

%start  prog

%%

prog
    :   ID '{' cuerpo '}'
            {Logger.logRule(cursor.getCurrentLine(), "Sentencia PROG");
            ambito = $1.sval;}
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
    :   cuerpo sentencia {$$=$2;}
    |   sentencia
    ;

sentencia
    :   sentencia_declarativa
    |   sentencia_ejecucion ';'
    |   sentencia_ejecucion_sin_coma
    ;

sentencia_declarativa
    :   funcion  {Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");}
    ;

sentencia_ejecucion
    :   sentencia_print  {Logger.logRule(cursor.getCurrentLine(), "Sentencia PRINT");}
    |   sentencia_seleccion
    |   sentencia_iteracion
    |   sentencia_asignacion
    ;

sentencia_ejecucion_retorno
    //:   sentencia_seleccion_retorno
    :   sentencia_iteracion_retorno
    ;

//sentencia_seleccion_retorno
//    :   IF parametros_seleccion cuerpo_seleccion_retorno ELSE cuerpo_seleccion_retorno ENDIF    {Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
//    |   IF parametros_seleccion cuerpo_seleccion_retorno ELSE cuerpo_seleccion ENDIF            {Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
//    |   IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion_retorno ENDIF            {Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");}
//    |   IF parametros_seleccion cuerpo_seleccion_retorno ENDIF                                  {Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");}
//    |   sentencia_seleccion_sin_endif  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
//   ;

//cuerpo_seleccion_retorno
//    :   '{' cuerpo_funcion '}'
//    |   cuerpo_seleccion_error
//    ;

sentencia_iteracion_retorno
    :   FOR parametros_iteracion cuerpo_iteracion_retorno  {Logger.logRule(cursor.getCurrentLine(), "Sentencia ITERACION");}
    ;

cuerpo_iteracion_retorno
    :   '{' cuerpo_funcion '}'
//    |   cuerpo_iteracion_error
    ;

sentencia_ejecucion_sin_coma
    :   sentencia_ejecucion  {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
  //  ;

sentencia_print
    :   PRINT '(' STRING ')'
    |   PRINT '(' lista_exp_aritmeticas ')'
    |   sentencia_print_error
    ;

sentencia_print_error
    :   PRINT '(' ')'       {Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
    ;

sentencia_seleccion
    :   IF parametros_seleccion cuerpo_if ENDIF	{
            if (!pila.isEmpty()) {
                $$ = pila.pop();
                System.out.println((Terceto)$$.obj);
                String auxString = getReferencia($3).replaceAll("\\D",""); //Agarramos el valor sin los parentesis
                Integer aux = Integer.parseInt(auxString);
                aux++;
                auxString = "(" + String.valueOf(aux) + ")";
                ((Terceto)$$.obj).addSecond(auxString);

                Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
                //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");
            }
            $$ = $3;
        }
//    |   IF parametros_seleccion cuerpo_seleccion ENDIF                  {Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");}
//    |   sentencia_seleccion_sin_endif                                   {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;
cuerpo_if
    :  if_seleccion else_seleccion {$$=$2;}
    |  if_seleccion {
            pila.pop();
            $$=$1;
        }
    ;
if_seleccion
    :   cuerpo_seleccion {
            $$ = pila.pop();
            System.out.println((Terceto)$$.obj);
            String auxString = getReferencia($1).replaceAll("\\D",""); //Agarramos el valor sin los parentesis

            Integer aux = Integer.parseInt(auxString);
            aux++;
            auxString = "(" + String.valueOf(aux) + ")";
            ((Terceto)$$.obj).addThird(auxString);
            pila.push(crearTerceto(new ParserVal("BI"), null, null));
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
            //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");

        }
    ;

else_seleccion
    :	ELSE cuerpo_seleccion {$$=$2;}
    ;

//sentencia_seleccion_sin_endif
//    :   IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion
//    |   IF parametros_seleccion cuerpo_seleccion
//    ;

parametros_seleccion
    :   '(' condicion ')'  {
            pila.push(crearTerceto(new ParserVal("BF"),$2,new ParserVal("1")));
            Logger.logRule(cursor.getCurrentLine(), "Sentencia CONDICION");
        }
    |   parametros_seleccion_error
    ;

parametros_seleccion_error
    :   condicion ')'   {Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
    |   '(' condicion   {Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
    |   condicion       {Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
    ;

cuerpo_seleccion
    :   '{' cuerpo '}' {$$=$2;}
    |   cuerpo_seleccion_error
    ;

cuerpo_seleccion_error
    :   '{' '}' {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}

sentencia_iteracion
    :   FOR parametros_iteracion cuerpo_iteracion  {Logger.logRule(cursor.getCurrentLine(), "Sentencia ITERACION");}
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
    :   sentencia_asignacion_unaria     {Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION UNARIA");}
    |   sentencia_asignacion_multiple   {Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION MULTIPLE");}
    ;

sentencia_asignacion_unaria
    :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey($2.sval)) {
                Logger.logError(cursor.getCurrentLine(), "Redeclaracion de variable");}
            else {
                $2.sval=$2.sval + ".INT." + ambito;
                TablaSimbolos.TABLA_SIMBOLOS.put($2.sval,new Info($2.sval, "CTE_INT", "INT", "Variable", ambito));
                //System.out.println(TablaSimbolos.TABLA_SIMBOLOS.get($2.sval));
            };
            $$ = crearTerceto($3, $2, $4);
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
        } //TODO: Lexema o ID
    |   ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey($1.sval))) {
                Logger.logError(cursor.getCurrentLine(), "Variable sin declarar");
            };
            $$ = crearTerceto($2, $1, $3);
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
        }
    |   sentencia_asignacion_unaria_error
    ;

sentencia_asignacion_unaria_error
    :   VAR ID expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion luego de var");}
    ;

sentencia_asignacion_multiple
    :   lista_variables '=' lista_exp_aritmeticas %prec SENTENCIA_ASIGNACION_PREC   {if ($1.ival != $3.ival) {Logger.logError(cursor.getCurrentLine(), "La cantidad de variables (" + $1.ival + ") no coincide con la cantidad de expresiones (" + $3.ival + ") en la asignación múltiple.");}}
    ;

funcion
    :   encabezado_funcion '{' cuerpo_funcion '}'   {Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");}
    |   sentencia_lambda                            {Logger.logRule(cursor.getCurrentLine(), "Sentencia LAMBDA");}
    //|   funcion_error
    ;

encabezado_funcion
    :   lista_tipos  FUN ID '(' lista_param_formales ')' { //put de funcion
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey($3.sval)) {
                Logger.logError(cursor.getCurrentLine(), "Redeclaracion de funcion");
            } else {
                System.out.println($1.sval);
                //TablaSimbolos.TABLA_SIMBOLOS.put($3.sval,new Info($3.sval,$1, ));
            };
        }
    | encabezado_funcion_error
    ;

encabezado_funcion_error
    : lista_tipos FUN '(' lista_param_formales ')' '{' cuerpo_funcion '}'  {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en declaracion de funcion");}
    ;

cuerpo_funcion
    :   cuerpo sentencia_funcion
    |   sentencia_funcion cuerpo
    |   cuerpo sentencia_funcion cuerpo
    |   sentencia_funcion cuerpo sentencia_funcion
    |   sentencia_funcion
    ;

sentencia_funcion
    :   sentencia_ejecucion_retorno ';'
    |   sentencia_retorno
    ;

sentencia_retorno
    : RETURN '(' lista_exp_aritmeticas ')' ';'
    ;

//funcion_error
//    :   lista_tipos FUN '(' lista_param_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'     {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion");}
//    ;

sentencia_lambda
    :   parametro_lambda cuerpo_lambda argumento_lambda
    ;

parametro_lambda
    :    '(' tipo ID ')'
    ;

cuerpo_lambda
    :   '{' cuerpo '}'
    |   cuerpo_lambda_error
    ;
cuerpo_lambda_error
    :   cuerpo '}'          {Logger.logError(cursor.getCurrentLine(), "Falta delimitador izquierdo '{' del cuerpo lambda");}
    //|   '{' cuerpo error  {Logger.logError(cursor.getCurrentLine(), "Falta delimitador derecho '}' del cuerpo lambda");}
    ;

argumento_lambda
    :   '(' ID ')'
    |   '(' CTE_INT ')'
    |   '(' CTE_FLOAT ')'
    ;

condicion
    :   expresion_aritmetica simbolo_comparador expresion_aritmetica	{
	$$=crearTerceto($2,$1,$3);
	Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
	}
    |   condicion_error
    ;

condicion_error
    :   expresion_aritmetica expresion_aritmetica   {Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
    |   expresion_aritmetica simbolo_comparador     {Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
    |   simbolo_comparador expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
    ;

expresion_aritmetica
    :   expresion_aritmetica '+' termino  {
            $$ = crearTerceto($2, $1, $3);
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   expresion_aritmetica '-' termino{
            $$ = crearTerceto($2, $1, $3);
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   expresion_aritmetica_toi {
            $$ = $1;
            Logger.logRule(cursor.getCurrentLine(), "Sentencia TOI");
        }
    |   termino { $$=$1;}
    |   expresion_aritmetica_error
    ;

expresion_aritmetica_error
    :   error '+' termino               {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '+''");}
    |   error '-' termino               {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '-'");}
    |   expresion_aritmetica '+' error  {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '+'");}
    |   expresion_aritmetica '-' error  {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
    ;

termino
    :   termino '*' factor {
            $$ = crearTerceto($2, $1, $3);
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   termino '/' factor{
            $$ = crearTerceto($2, $1, $3);
            Logger.logTerceto(cursor.getCurrentLine(), $$.obj);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   factor {$$ = $1;}
    |   termino_error
    ;

termino_error
    :   termino '*' error   {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '*'");}
    |   termino '/' error   {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
    |   error '*' factor    {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
    |   error '/' factor    {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
    ;

factor
    :   CTE_INT     {$$=$1;}
    |   CTE_FLOAT   {$$=$1;}
    |   invocacion_funcion {Logger.logRule(cursor.getCurrentLine(), "Sentencia INVOCACION FUNCION");}
    |   variable    {$$=$1;}
    ;

invocacion_funcion
    :   FUN ID '(' lista_param_reales ')' {
            if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey($2.sval))) {
                Logger.logError(cursor.getCurrentLine(), "Funcion sin declarar");};
        }
    |   invocacion_funcion_error
    ;
invocacion_funcion_error
    :   FUN '(' lista_param_reales ')' {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion en invocacion de funcion");}
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
    :   GREATER_OR_EQUAL    {$$=$1;}
    |   LESS_OR_EQUAL       {$$=$1;}
    |   EQUAL               {$$=$1;}
    |   NOT_EQUAL           {$$=$1;}
    |   '>'                 {$$=$1;}
    |   '<'                 {$$=$1;}
    ;

lista_exp_aritmeticas
    :   lista_exp_aritmeticas ',' expresion_aritmetica  {$$.ival = $1.ival + 1;}
    |   expresion_aritmetica                            {$$.ival = 1;}
    |   lista_exp_aritmeticas_error
    ;

lista_exp_aritmeticas_error
    :   lista_exp_aritmeticas  expresion_aritmetica    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de expresiones aritmeticas (lado derecho)");}
    ;

lista_tipos
    :   lista_tipos ',' tipo    {$$.sval = $1.sval + (",") + $3.sval;}
    |   tipo                    {$$.sval = $1.sval;}
    |   lista_tipos_error
    ;

lista_tipos_error
    :   lista_tipos tipo {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de tipos");}
    ;

lista_param_formales
    :   lista_param_formales ',' parametro_formal
    |   parametro_formal
    ;

lista_variables
    :   lista_variables ',' variable        {$$.ival = $1.ival + 1;}
    |   variable                            {$$.ival = 1; }
    |   lista_variables_error
    ;

lista_variables_error
    :   lista_variables variable    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
    ;

variable
    :   ID          {$$.sval=$1.sval;}
    |   ID  '.' ID
    ;

tipo
    :   INT     {$$.sval = "INT";}
    |   FLOAT   {$$.sval = "FLOAT";}
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
    |   parametro_real_error
    ;
parametro_real_error
    :   expresion_aritmetica ARROW  {Logger.logError(cursor.getCurrentLine(), "Falta especificacion del parametro formal");}
    ;
%%

private static int yylval_recognition = 0;

static AnalizadorLexico lex = null;
static Parser par = null;
static Cursor cursor = null;
static Integer numTerceto = 0;
static String ambito = null;
static Stack<ParserVal> pila = new Stack<>();

public static void main (String [] args) {

    System.out.println("Iniciando compilación ... ");

    /*Scanner lector = new Scanner(System.in);
    System.out.println("Usted se encuentra en: " + System.getProperty("user.dir"));
    System.out.println("Ingrese el archivo deseado, este debe estar dentro de data");
    String programa = lector.nextLine();
    lector.close();
    */

    String programa = "testing.txt"; //TODO:Despues lo cambiamos

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

private String getReferencia(ParserVal val) {
    if (val.obj != null) {
        // Es un Terceto, usamos su número de referencia
        return ((Terceto) val.obj).getNumTerceto();
    } else {
        // Es un valor simple (ID, CTE_INT, etc.), usamos su lexema
        return val.sval;
    }
}

private ParserVal crearTerceto(ParserVal operador, ParserVal operando1, ParserVal operando2) {
    String opIzquierdo;
    String opDerecho;
    if (operando1 == null){
        opIzquierdo = null;
    } else {
        opIzquierdo = getReferencia(operando1);
    }
    if (operando2 == null){
        opDerecho = null;
    } else {
        opDerecho = getReferencia(operando2);
    }

    //System.out.println("op1 " + operando1.obj);
    //System.out.println("op2 " + operando2.obj);
    String op = operador.sval;
    numTerceto += 1;
    Terceto nuevoTerceto = new Terceto(numTerceto,op, opIzquierdo, opDerecho);
    //Logger.logTerceto(cursor.getCurrentLine(), nuevoTerceto);

    ParserVal resultado = new ParserVal();
    resultado.obj = nuevoTerceto;
    resultado.sval = null;
    return resultado;
}