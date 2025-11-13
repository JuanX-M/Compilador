%{
    import java.io.*;
    import java.util.Scanner;
    import java.util.Stack;
    import java.util.ArrayList;
    import java.util.Comparator;

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
    :   nombre_programa '{' cuerpo '}'
    |   prog_error
    ;

prog_error
    :   '{' cuerpo '}'                  {Logger.logError(cursor.getCurrentLine(), "Falta el nombre del programa");}
    |   nombre_programa '(' cuerpo ')'  {Logger.logError(cursor.getCurrentLine(), "Debe indicar el programa entre {}");}
    |   nombre_programa  cuerpo         {Logger.logError(cursor.getCurrentLine(), "Faltan los delimitadores de programa");}
    |   nombre_programa  '{' cuerpo     {Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '}'");}
    |   nombre_programa  cuerpo '}'     {Logger.logError(cursor.getCurrentLine(), "Falta el delimitador de programa '{'");}
    |   error                           {Logger.logError(cursor.getCurrentLine(), "Hay errores lexicos o sintaticos no identificados");}
    ;

nombre_programa
    :   ID {
            Logger.logRule(cursor.getCurrentLine(), "Sentencia PROG");
            ambito = $1.sval;
        }
    ;

cuerpo
    :   cuerpo sentencia {$$=$2;}
    |   sentencia
    ;

sentencia
    :   sentencia_declarativa ';'
    |   sentencia_declarativa_sin_coma
    |   sentencia_ejecutable
    ;

sentencia_declarativa
    :   funcion  {Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");}
    |   declaracion_variable
//    |   declaracion_variable_sin_coma
    ;

sentencia_declarativa_sin_coma
    :   sentencia_declarativa
    ;

sentencia_ejecucion
    :   sentencia_print  {Logger.logRule(cursor.getCurrentLine(), "Sentencia PRINT");}
    |   sentencia_seleccion
    |   sentencia_iteracion
    |   sentencia_asignacion
    ;

sentencia_ejecucion_sin_coma
    :   sentencia_ejecucion  {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
    ;

funcion
    :   encabezado_funcion '{' cuerpo_funcion '}'   {
            Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");
            int pos = ambito.lastIndexOf('.');
            if (pos != -1) {
                ambito = ambito.substring(0, pos);
            }
        }
    |   sentencia_lambda    {Logger.logRule(cursor.getCurrentLine(), "Sentencia LAMBDA");}
    //|   funcion_error
    ;

declaracion_variable
    :   declaracion_unaria
    ;

//declaracion_variable_sin_coma
//    :   declaracion_variable    {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de la declaracion de variable.");}
//    ;

sentencia_print
    :   PRINT '(' STRING ')' {
            $$ = crearTerceto($1,$3,null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        }
    |   PRINT '(' lista_exp_aritmeticas ')' {
            $$ = crearTerceto($1,$3,null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        }
    |   sentencia_print_error
    ;

sentencia_print_error
    :   PRINT '(' ')'       {Logger.logError(cursor.getCurrentLine(), "Falta argumento en sentencia PRINT");}
    ;

//TODO: Se puede permitir declaracion de varibles de lso cuerpos if/else???
sentencia_seleccion
    :   IF parametros_seleccion cuerpo_seleccion ELSE  cuerpo_seleccion ENDIF    {
            auxParserVal = pila.pop();
            String auxString = getReferencia($5).replaceAll("\\D",""); //Agarramos el valor sin los parentesis
            Integer aux = Integer.parseInt(auxString);
            aux++;
            auxString = "(" + String.valueOf(aux) + ")";
            ((Terceto)auxParserVal.obj).addSecond(auxString);
            listaTercetos.add((Terceto)auxParserVal.obj);
            ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());
            //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");
            $$ = $5;
        }
    |   IF parametros_seleccion cuerpo_seleccion ENDIF{
            pila.pop(); // Sacamos el BI sin uso
            auxParserVal = pila.pop();
            listaTercetos.add((Terceto)auxParserVal.obj);
            ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());
            $$ = $3;
            Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");
        }
    |   sentencia_seleccion_sin_endif  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;

sentencia_seleccion_sin_endif
    :   IF parametros_seleccion cuerpo_seleccion ELSE cuerpo_seleccion
    |   IF parametros_seleccion cuerpo_seleccion
    ;

sentencia_iteracion
    :   FOR parametros_iteracion cuerpo_iteracion  {Logger.logRule(cursor.getCurrentLine(), "Sentencia ITERACION");}
    ;

sentencia_asignacion
    :   sentencia_asignacion_unaria     {Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION UNARIA");}
    |   sentencia_asignacion_multiple   {Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION MULTIPLE");}
    ;

encabezado_funcion
    :   lista_tipos FUN ID {
            String aux = ambito + '.' + $3.sval;
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey($3.sval)) {
                Logger.logError(cursor.getCurrentLine(), "Redeclaracion de funcion");
            } else {
                TablaSimbolos.TABLA_SIMBOLOS.put( aux ,new Info($3.sval, $2.sval, "INT", "Funcion", ambito));
                ambito += '.' + $3.sval;
            }
        } '(' lista_param_formales ')'
    |   encabezado_funcion_error
    ;

encabezado_funcion_error
    :   lista_tipos FUN '(' lista_param_formales ')'  {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en declaracion de funcion");}
    ;

cuerpo_funcion
    :   lista_sentencias_funcion sentencia_retorno
    |   sentencia_retorno
    ;

lista_sentencias_funcion
    :   lista_sentencias_funcion sentencia_funcion
    |   sentencia_funcion
    ;

sentencia_lambda
    :   parametro_lambda cuerpo_lambda argumento_lambda
    ;

declaracion_unaria
    :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            String aux = ambito + '.' + $2.sval;
            //TODO: PONER TIPO
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux)) {
                Logger.logError(cursor.getCurrentLine(), "Redeclaracion de variable");}
            else {
                TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($2.sval, "CTE_INT", "INT", "Variable", ambito));
                //System.out.println(TablaSimbolos.TABLA_SIMBOLOS.get($2.sval));
            };
            $2.sval = aux;
            $$ = crearTerceto($3, $2, $4);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        }
    |   declaracion_unaria_error
    ;

declaracion_unaria_error
    : VAR ID expresion_aritmetica    {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion en declaracion de variable");}
    ;

lista_exp_aritmeticas
    :   lista_exp_aritmeticas ',' expresion_aritmetica  {
            auxParserVal = pila.pop();
            auxParserVal=crearTerceto(new ParserVal(":="),auxParserVal, $3);
            listaTercetos.add((Terceto)auxParserVal.obj);
            ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());


        }
    |   expresion_aritmetica                            {

            auxParserVal = pila.pop();
            auxParserVal= crearTerceto(new ParserVal(":="),auxParserVal, $1);

            listaTercetos.add((Terceto)auxParserVal.obj);
            ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());

        }
    |   lista_exp_aritmeticas_error
    ;

lista_exp_aritmeticas_error
    :   lista_exp_aritmeticas  expresion_aritmetica    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de expresiones aritmeticas (lado derecho)");}
    ;

parametros_seleccion
    :   '(' condicion ')'  {
    		contadorParaBF++;
		String bf = "BFIF" + contadorParaBF;
		pila.push(crearTerceto(new ParserVal(bf),$2, null));
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
    :   '{' cuerpo_ejecutable '}' {
            if (((Terceto)pila.peek().obj).getFirst().contains("BFIF")) {

                auxParserVal = pila.pop();
                String auxString = getReferencia($2).replaceAll("\\D","");
                Integer aux = Integer.parseInt(auxString);
                aux++;
                auxString = "(" + String.valueOf(aux) + ")";
                ((Terceto)auxParserVal.obj).addThird(auxString);
                pila.push(auxParserVal);
                pila.push(crearTerceto(new ParserVal("BI"), null, null));
            } else{
                if (((Terceto)pila.peek().obj).getFirst().contains("BI")) {
                    auxParserVal = pila.pop(); //BI esta en la pila con dest. +1
                    auxParserVal2 = pila.pop(); //Tiene el BF
                    String auxString = ((Terceto)auxParserVal2.obj).getThird().replaceAll("\\D","");
                    Integer aux = Integer.parseInt(auxString);
                    aux++;
                    auxString = "(" + String.valueOf(aux) + ")";
                    ((Terceto)auxParserVal2.obj).addThird(auxString);
                    listaTercetos.add((Terceto)auxParserVal2.obj);
                    ((Terceto)auxParserVal2.obj).addLine(cursor.getCurrentLine());
                    pila.push(auxParserVal);
                };
            };
            $$ = $2;
        }
    |   cuerpo_seleccion_error
    ;

cuerpo_seleccion_error
    :   '{' '}' {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}

parametros_iteracion
    :   '(' encabezado_iteracion ')' {
    		$$= crearTerceto(new ParserVal("BF"),$2,null);
    		pila.push($$);
    	}
    |   parametros_iteracion_error
    ;

parametros_iteracion_error
    :   encabezado_iteracion ')'    {Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de iteracion");}
    |   '(' encabezado_iteracion    {Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de iteracion");}
    |   encabezado_iteracion        {Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de iteracion");}
    ;

cuerpo_iteracion
    :   '{' cuerpo_ejecutable '}'{
    		System.out.println("Entra");
    		if (pila.isEmpty())
    			System.out.println("bbbbb");
    		$$=pila.pop(); //Saco BF incompleto de la fila
    		// Hago suma con mas 3
    		String auxString = getReferencia($2).replaceAll("\\D",""); //Agarramos el valor sin los parentesis
    		Integer aux = Integer.parseInt(auxString);
	        aux=aux+3;
	    	auxString = "(" + String.valueOf(aux) + ")";

    		((Terceto)$$.obj).addThird(auxString); // completo BF y agrego a arraylist
    		listaTercetos.add((Terceto)$$.obj);
	        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

	        auxString = String.valueOf(((Terceto)$$.obj).getSecond()).replaceAll("\\D",""); // obtengo referencia del terceto de la condicion que esta en BF

	        int indice = Integer.parseInt(auxString)-1;
	        System.out.println(indice);
	        auxString = listaTercetos.get(indice).getSecond(); // accedo a al arraylist para obtener el terceto de la condicion
		System.out.println(auxString);
	        auxParserVal = crearTerceto(new ParserVal("+"),new ParserVal(auxString),new ParserVal("1")); // creo hago terceto de suma +1 del for
	        listaTercetos.add((Terceto)auxParserVal.obj);
		((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());
		auxString = "(" + Integer.toString(indice+1) + ")";
		auxParserVal = crearTerceto(new ParserVal("BI"),new ParserVal(auxString),null); // creo BI con direccion al terceto de condicion
		listaTercetos.add((Terceto)auxParserVal.obj);
		((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());

    	}
    |   cuerpo_iteracion_error
    ;

cuerpo_iteracion_error
    :   '{'  '}'    {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
    ;

sentencia_asignacion_unaria
    :   ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            String aux = ambito + '.' + $1.sval;
            if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux))) {
                Logger.logError(cursor.getCurrentLine(), "Variable sin declarar");
            };
            $1.sval = aux;
            $$ = crearTerceto($2, $1, $3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        }
    |   sentencia_asignacion_unaria_error
    ;

sentencia_asignacion_unaria_error
    :   ID expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion");}
    ;

sentencia_asignacion_multiple
    :   lista_variables {
            Stack<ParserVal> tempStack = new Stack<>();
            while (!pila.isEmpty()) {
                tempStack.push(pila.pop());
            }

            // Volver a llenar el Stack original
            pila.addAll(tempStack);
        } '=' lista_exp_aritmeticas %prec SENTENCIA_ASIGNACION_PREC {

        }
    ;

lista_tipos
    :   lista_tipos ',' tipo    {$$.sval = $1.sval + (",") + $3.sval;}
    |   tipo                    {$$ = $1;}
    |   lista_tipos_error
    ;

lista_tipos_error
    :   lista_tipos tipo {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de tipos");}
    ;

lista_param_formales
    :   lista_param_formales ',' parametro_formal
    |   parametro_formal
    ;

sentencia_funcion
    :   sentencia
    |   sentencia_ejecucion_retorno ';'
    |   sentencia_ejecucion_retorno_sin_coma

    ;
sentencia_ejecucion_retorno_sin_coma
    :   sentencia_ejecucion_retorno  {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
    ;
parametro_lambda
    :    '(' tipo ID ')'
    ;

cuerpo_lambda
    :   '{' cuerpo_ejecutable '}'
    |   cuerpo_lambda_error
    ;

cuerpo_lambda_error
    :   cuerpo_ejecutable '}'          {Logger.logError(cursor.getCurrentLine(), "Falta delimitador izquierdo '{' del cuerpo lambda");}
    //|   '{' cuerpo error  {Logger.logError(cursor.getCurrentLine(), "Falta delimitador derecho '}' del cuerpo lambda");}
    ;

argumento_lambda
    :   '(' ID ')'
    |   '(' CTE_INT ')'
    |   '(' CTE_FLOAT ')'
    ;

expresion_aritmetica
    :   expresion_aritmetica '+' termino  {

            $$ = crearTerceto($2, $1, $3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   expresion_aritmetica '-' termino{
            $$ = crearTerceto($2, $1, $3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
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

condicion
    :   expresion_aritmetica simbolo_comparador expresion_aritmetica    {
            $$=crearTerceto($2,$1,$3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        }
    |   condicion_error
    ;

condicion_error
    :   expresion_aritmetica expresion_aritmetica   {Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
    |   expresion_aritmetica simbolo_comparador     {Logger.logError(cursor.getCurrentLine(), "Falta de argumento derecho en condicion");}
    |   simbolo_comparador expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de argumento izquierdo en condicion");}
    ;

cuerpo_ejecutable
    :   cuerpo_ejecutable sentencia_ejecutable
    |   sentencia_ejecutable
    |   cuerpo_ejecutable_error
    ;
cuerpo_ejecutable_error
    :   sentencia_declarativa {Logger.logError(cursor.getCurrentLine(), "No se permiten declaraciones dentro de cuerpos ejecutables");}
    ;
sentencia_ejecutable
    :   sentencia_ejecucion ';'
    |   sentencia_ejecucion_sin_coma
    ;

encabezado_iteracion
    :   ID FROM CTE_INT TO CTE_INT {
    	int aux1;
    	aux1 = Integer.parseInt($3.sval);
    	int aux2;
	aux2 = Integer.parseInt($5.sval);

	String aux = ambito + '.' + $1.sval;
	if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux)) {
		Logger.logError(cursor.getCurrentLine(), "Redeclaracion de variable");}
	else {
		TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($1.sval, "CTE_INT", "INT", "Variable", ambito));
		$$ = crearTerceto(new ParserVal(":="),new ParserVal(aux),$3);
		listaTercetos.add((Terceto)$$.obj);
		((Terceto)$$.obj).addLine(cursor.getCurrentLine());
	};
	if (aux1 == aux2)
		Logger.logWarning(cursor.getCurrentLine(), "Cuerpo de for no ejecutado debido a constantes iguales");
	else{
		if (aux1 < aux2){
			$$=crearTerceto(new ParserVal("<"), $1, $5); //Creamos el Terceto de la condicion sin la direccion de la suma
		}
		else{
			$$=crearTerceto(new ParserVal(">"), $1, $5); //Creamos el Terceto de la condicion sin la direccion de la resta
	     	}

		listaTercetos.add((Terceto)$$.obj);
		((Terceto)$$.obj).addLine(cursor.getCurrentLine());
		Logger.logRule(cursor.getCurrentLine(), "Sentencia FOR");
	}

	//pila.push(crearTerceto(new ParserVal("BF"), $$, null));

    }
    |   encabezado_iteracion_error
    ;

encabezado_iteracion_error
    :   FROM CTE_INT TO CTE_INT {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado ID del for");}
    |   ID CTE_INT TO CTE_INT   {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado FROM del for");}
    |   ID FROM TO CTE_INT      {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_1 del for");}
    |   ID FROM CTE_INT CTE_INT {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado TO del for");}
    |   ID FROM CTE_INT TO      {Logger.logError(cursor.getCurrentLine(), "Falta de encabezado CTE_INT_2 del for");}
    ;

lista_variables
    :   lista_variables ',' variable        {
              pila.push($3);
        }
    |   variable  {
           pila.push($1);

        }
    |   lista_variables_error
    ;

lista_variables_error
    :   lista_variables variable    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de variables (lado izquierdo)");}
    ;

tipo
    :   INT     {$$ = $1;}
    |   FLOAT   {$$ = $1;}
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

sentencia_ejecucion_retorno
    :   sentencia_seleccion_retorno
    |   sentencia_iteracion_retorno
    ;

sentencia_retorno
    : RETURN '(' lista_exp_aritmeticas ')' ';'
    | sentencia_retorno_sin_coma
    ;
sentencia_retorno_sin_coma
    : RETURN '(' lista_exp_aritmeticas ')'  {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de la sentencia return.");}
    ;
termino
    :   termino '*' factor {
            $$ = crearTerceto($2, $1, $3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   termino '/' factor{
            $$ = crearTerceto($2, $1, $3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
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

variable
    :   ID          {
            String aux = ambito + '.' + $1.sval;
            if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux))) {
                Logger.logError(cursor.getCurrentLine(), "Variable sin declarar");
            };
            $$.sval = aux;
        }
    |   ID  '.' ID {
            String aux = ambito + '.' + $1.sval + "." + $3.sval;
            System.out.println("Variable compuesta: " + aux);
            if (!(TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux))) {
                Logger.logError(cursor.getCurrentLine(), "Variable sin declarar");
            };
            $$.sval = aux;
        }
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

sentencia_seleccion_retorno
    :   IF parametros_seleccion cuerpo_seleccion_retorno ELSE {} cuerpo_seleccion_retorno ENDIF {
            /*
            auxParserVal = pila.pop();
            String auxString = getReferencia($5).replaceAll("\\D",""); //Agarramos el valor sin los parentesis
            Integer aux = Integer.parseInt(auxString);
            aux++;
            auxString = "(" + String.valueOf(aux) + ")";
            ((Terceto)auxParserVal.obj).addSecond(auxString);
            //System.out.println(" if-else : "+ pila);
            listaTercetos.add((Terceto)auxParserVal.obj);
            ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());
            //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");
            $$ = $5;
            */
            //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF-ELSE");
        }
    |   IF parametros_seleccion cuerpo_seleccion_retorno ENDIF {
            /*
            pila.pop(); // Sacamos el BI sin uso
            auxParserVal = pila.pop();
            listaTercetos.add((Terceto)auxParserVal.obj);
            ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());
            $$ = $3;
            //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");
            //Logger.logRule(cursor.getCurrentLine(), "Sentencia IF");
            */
        }
//    |   sentencia_seleccion_sin_endif  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;

sentencia_iteracion_retorno
    :   FOR parametros_iteracion cuerpo_iteracion_retorno  {Logger.logRule(cursor.getCurrentLine(), "Sentencia ITERACION");}
    ;

factor
    :   CTE_INT     {$$=$1;}
    |   CTE_FLOAT   {$$=$1;}
    |   invocacion_funcion {
            //Crear BI a con dest al primer terceto de la funcion
            Logger.logRule(cursor.getCurrentLine(), "Sentencia INVOCACION FUNCION");
        }
    |   variable    {$$=$1;}
    ;

cuerpo_seleccion_retorno
    :   '{' cuerpo_ejecutable_retorno '}' {
            /*
            if (pila.size() == 1 ) {
                auxParserVal = pila.pop();
                String auxString = getReferencia($2).replaceAll("\\D","");
                Integer aux = Integer.parseInt(auxString);
                aux++;
                auxString = "(" + String.valueOf(aux) + ")";
                ((Terceto)auxParserVal.obj).addThird(auxString);
                pila.push(crearTerceto(new ParserVal("BI"), null, null));
                pila.push(auxParserVal);
            } else{
                if (pila.size() == 2 ) {
                    auxParserVal = pila.pop(); //BF esta en la pila con dest. +1
                    String auxString = ((Terceto)auxParserVal.obj).getThird().replaceAll("\\D","");
                    Integer aux = Integer.parseInt(auxString);
                    aux++;
                    auxString = "(" + String.valueOf(aux) + ")";
                    ((Terceto)auxParserVal.obj).addThird(auxString);
                    listaTercetos.add((Terceto)auxParserVal.obj);
                    ((Terceto)auxParserVal.obj).addLine(cursor.getCurrentLine());
                };
            };
            $$ = $2;
            */
        }
//    |   cuerpo_seleccion_error
    ;

cuerpo_iteracion_retorno
    :   '{' cuerpo_ejecutable_retorno '}'
//    |   cuerpo_iteracion_error
    ;
cuerpo_ejecutable_retorno
    :   cuerpo_ejecutable sentencia_retorno
    |   sentencia_retorno
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


lista_param_reales
    :   lista_param_reales ',' parametro_real
    |   parametro_real
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
static ArrayList<Terceto> listaTercetos = new ArrayList<>();
static ParserVal auxParserVal = new ParserVal();
static ParserVal auxParserVal2 = new ParserVal();
static int contadorParaBF = 0;
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

    listaTercetos.sort(Comparator.comparingInt(Terceto::getSoloNumTerceto));
    System.out.println("\nLista de Tercetos: "+ listaTercetos);
    for(Terceto t : listaTercetos){
        Logger.logTerceto(t.getLinea(), t);
    }

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