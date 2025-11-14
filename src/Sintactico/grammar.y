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


sentencia_seleccion
    :   IF parametros_seleccion cuerpo_seleccion  ENDIF  {
            $$=$3;
        }
    |   sentencia_seleccion_sin_endif  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;

cuerpo_seleccion
    :  '{' parte_if '}' {
            //Saco el nro de terceto del BF incompleto de la pila
            int numTercetoBackpatch = pila.pop(); // hago pop() del nro de terceto del BF incompleto

            //Obtengo referencia BF, obtengo su nro de terceto al que salta, parseo a integer  y hago + 1
            //porque tengo terceto BI y vuelvo agregarlo al BF terceto
            String auxString= listaTercetos.get(numTercetoBackpatch -1).getThird();
            Integer aux= Integer.parseInt(auxString.replaceAll("\\D","")) + 1 ;

            auxString = "(" + String.valueOf(aux) + ")";
            listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); // completo el tercer operando del BF

            //Creo el BI incompleto,agrego al arraylist y su nro de terceto en la pila
            $$=crearTerceto(new ParserVal("BI"), null, null);

            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

            pila.push( ((Terceto)$$.obj).getSoloNumTerceto() ); // pusheo el nro de terceto del BI incompleto

            // Creacion etiqueta
            int numTercetoActual = ((Terceto)$$.obj).getSoloNumTerceto() + 1;
            String etiqueta = "ETIQUETA"+ numTercetoActual;
            $$= crearTerceto(new ParserVal(etiqueta),null,null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

        } ELSE '{' parte_else '}' {
            //Saco el nro de terceto del BI incompleto de la pila para completarlo haciendo +1

            int numTercetoBackpatch = pila.pop(); // hago pop() del nro de terceto del BI incompleto

            Integer aux= Integer.parseInt(getReferencia($7).replaceAll("\\D","")) + 1 ;

            String auxString = "(" + String.valueOf(aux) + ")";
            System.out.println(listaTercetos.get(numTercetoBackpatch -1));
            listaTercetos.get(numTercetoBackpatch -1).addSecond(auxString);// completo el tercer operando del BI

            // Creacion etiqueta
            int numTercetoActual = listaTercetos.get(listaTercetos.size()-1).getSoloNumTerceto() + 1;
            String etiqueta = "ETIQUETA" + numTercetoActual;
            $$= crearTerceto(new ParserVal(etiqueta),null,null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

            $$=$7;
            }
    |  '{' parte_if '}' {
            //Saco el nro de terceto del BF incompleto de la pila ya que no hay ELSE
            //Aca no creo BI y no hago +1
            pila.pop();

            // Creacion etiqueta
            int numTercetoActual = listaTercetos.get(listaTercetos.size()-1).getSoloNumTerceto() + 1;
            String etiqueta = "ETIQUETA" + numTercetoActual;
            $$= crearTerceto(new ParserVal(etiqueta),null,null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

            $$=$2;
        }
    |   cuerpo_seleccion_error
    ;

cuerpo_seleccion_error
    :   '{' '}' {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en seleccion");}
    ;

parte_if
    :   cuerpo_ejecutable {
            //aca se completa BF con nro de terceto del cuerpo_ejecutable + 1
            //
            int numTercetoBackpatch = pila.peek();

            //obtengo referencia terceto de cuerpo_ejecutable, parseo a integer  y hago + 1
            Integer aux= Integer.parseInt(getReferencia($1).replaceAll("\\D","")) + 1 ;

            String auxString = "(" + String.valueOf(aux) + ")";
            listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); /* completo el tercer operando del BF*/

            $$=$1;
        }
    ;

parte_else
    :   cuerpo_ejecutable
    ;

sentencia_seleccion_sin_endif
    :   IF parametros_seleccion cuerpo_seleccion  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;

sentencia_iteracion
    :   FOR parametros_iteracion cuerpo_iteracion  {
        //Creo el terceto de incremento/decremento de la variable de control del for

        if (((Terceto)$2.obj).getFirst() == "<"){
            // terceto de incremento

            $$= crearTerceto(new ParserVal("+"), new ParserVal(((Terceto)$2.obj).getSecond()), new ParserVal("1"));
        }
        else{
            //terceto de decremento
            $$= crearTerceto(new ParserVal("-"), new ParserVal(((Terceto)$2.obj).getSecond()), new ParserVal("1"));
        }
        //agrego terceto de incremento/decremento al arraylist
        listaTercetos.add((Terceto)$$.obj);
        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

        //Creo terceto BI para volver al inicio de la iteracion y lo agrego
        $$= crearTerceto(new ParserVal("BI"), $2, null);

        listaTercetos.add((Terceto)$$.obj);
        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());


        $$=$3;
        }
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
            // $1.obj ya es un ArrayList<ParserVal>
            ((ArrayList<ParserVal>)$1.obj).add($3); // $3 es el ParserVal de la expresion
            $$ = $1; // Pasa la lista modificada hacia arriba
        }
    |   expresion_aritmetica  {
            ArrayList<ParserVal> exprs = new ArrayList<>();
            exprs.add($1); // $1 es el ParserVal de 'expresion_aritmetica'
            $$ = new ParserVal(exprs); // Crea un nuevo ParserVal para contener la lista
        }
    |   lista_exp_aritmeticas_error
    ;

lista_exp_aritmeticas_error
    :   lista_exp_aritmeticas  expresion_aritmetica    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de expresiones aritmeticas (lado derecho)");}
    ;

parametros_seleccion
    :   '(' condicion ')'  {
            //Creamos terceto BF incompleto, pusheamos a la pila su nro de terceto y agregamos arraylist
            $$= crearTerceto(new ParserVal("BF"), $2, null);

            int numTercetoActual = ((Terceto)$$.obj).getSoloNumTerceto();
            pila.push(numTercetoActual);;

            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        }
    |   parametros_seleccion_error
    ;

parametros_seleccion_error
    :   condicion ')'   {Logger.logError(cursor.getCurrentLine(), "Falta de '(' en condicion de seleccion");}
    |   '(' condicion   {Logger.logError(cursor.getCurrentLine(), "Falta de ')' en condicion de seleccion");}
    |   condicion       {Logger.logError(cursor.getCurrentLine(), "Falta de '()' en condicion de seleccion");}
    ;



parametros_iteracion
    :   '(' encabezado_iteracion ')' {
            //creacion de terceto BF incompleto, pusheamos a la pila su nro de terceto y agregamos arraylist
            $$= crearTerceto(new ParserVal("BF"), $2, null);

            int numTercetoActual = ((Terceto)$$.obj).getSoloNumTerceto();
            pila.push(numTercetoActual);

            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

            // tengo terceto de condicion de iteracion, para usarlo en creacion de BI y terceto de itereacion del for
            $$=$2;
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
                //Saco el nro de terceto del BF incompleto de la pila
                int numTercetoBackpatch = pila.pop();


                //obtengo referencia terceto de cuerpo_ejecutable, parseo a integer  y hago + 3
                // porque tengo terceto BI y terceto de incremeto de variable de control del for
                System.out.println("Raaa: " + $2.obj);
                Integer aux= Integer.parseInt(getReferencia($2).replaceAll("\\D","")) + 3 ;

                String auxString = "(" + String.valueOf(aux) + ")";
                listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); /* completo el tercer operando del BF*/

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
    :   lista_variables  '=' lista_exp_aritmeticas %prec SENTENCIA_ASIGNACION_PREC {
            ArrayList<ParserVal> listaVariables = (ArrayList<ParserVal>)$1.obj;
            ArrayList<ParserVal> listaExpresiones = (ArrayList<ParserVal>)$3.obj;
            for (int i = 0; i < listaVariables.size(); i++) {
                ParserVal variable = listaVariables.get(i);   // El ParserVal de la variable (contiene sval)
                ParserVal expresion = listaExpresiones.get(i); // El ParserVal de la expresion (contiene sval o obj)

                $$ = crearTerceto(new ParserVal(":="), variable, expresion);

                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            }

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
            //Crea el terceto de la condicion
            $$ = crearTerceto($2, $1, $3);
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
    :   cuerpo_ejecutable sentencia_ejecutable {
            $$=$2;
        }
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
            //Creacion y Asignacion de variable de control del for
            TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($1.sval, "CTE_INT", "INT", "Variable", ambito));
            $$ = crearTerceto(new ParserVal(":="),new ParserVal(aux),$3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
        };
        if (aux1 == aux2)
            Logger.logWarning(cursor.getCurrentLine(), "Cuerpo de for no ejecutado debido a constantes iguales");
        else{
            if (aux1 < aux2){
                $$=crearTerceto(new ParserVal("<"), $1, $5); //Creamos el Terceto de la condicion menor
            }
            else{
                $$=crearTerceto(new ParserVal(">"), $1, $5); //Creamos el Terceto de la condicion mayor
                }

            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            Logger.logRule(cursor.getCurrentLine(), "Sentencia FOR");
        }



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
            ((ArrayList<ParserVal>)$1.obj).add($3); // Agrega la nueva variable
            $$ = $1; // Pasa la lista modificada hacia arriba
        }
    |   variable  {
            ArrayList<ParserVal> vars = new ArrayList<>();
            vars.add($1); // $1 es el ParserVal de la regla 'variable'
            $$ = new ParserVal(vars); // Crea un nuevo ParserVal para contener la lista
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
    :   IF parametros_seleccion cuerpo_seleccion_retorno ENDIF {
            $$=$3;
        }
    |   sentencia_seleccion_sin_endif_retorno  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;
sentencia_seleccion_sin_endif_retorno
    :   IF parametros_seleccion cuerpo_seleccion_retorno  {Logger.logError(cursor.getCurrentLine(), "Falta de endif");}
    ;
sentencia_iteracion_retorno
    :   FOR parametros_iteracion cuerpo_iteracion_retorno  {

        //Creo el terceto de incremento/decremento de la variable de control del for

        if (((Terceto)$2.obj).getFirst() == "<"){
            // terceto de incremento

            $$= crearTerceto(new ParserVal("+"), new ParserVal(((Terceto)$2.obj).getSecond()), new ParserVal("1"));
        }
        else{
            //terceto de decremento
            $$= crearTerceto(new ParserVal("-"), new ParserVal(((Terceto)$2.obj).getSecond()), new ParserVal("1"));
        }
        //agrego terceto de incremento/decremento al arraylist
        listaTercetos.add((Terceto)$$.obj);
        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

        //Creo terceto BI para volver al inicio de la iteracion y lo agrego
        $$= crearTerceto(new ParserVal("BI"), $2, null);

        listaTercetos.add((Terceto)$$.obj);
        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());


        $$=$3;

        }
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
    :  '{' parte_if_retorno '}' {
        //Saco el nro de terceto del BF incompleto de la pila
        int numTercetoBackpatch = pila.pop(); // hago pop() del nro de terceto del BF incompleto

        //Obtengo referencia BF, obtengo su nro de terceto al que salta, parseo a integer  y hago + 1
        //porque tengo terceto BI y vuelvo agregarlo al BF terceto
        String auxString= listaTercetos.get(numTercetoBackpatch -1).getThird();
        Integer aux= Integer.parseInt(auxString.replaceAll("\\D","")) + 1 ;

        auxString = "(" + String.valueOf(aux) + ")";
        listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); // completo el tercer operando del BF

        //Creo el BI incompleto,agrego al arraylist y su nro de terceto en la pila
        $$=crearTerceto(new ParserVal("BI"), null, null);

        listaTercetos.add((Terceto)$$.obj);
        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());

        pila.push( ((Terceto)$$.obj).getSoloNumTerceto() ); // pusheo el nro de terceto del BI incompleto

        } ELSE '{' parte_else_retorno '}' {
            //Saco el nro de terceto del BI incompleto de la pila para completarlo haciendo +1

            int numTercetoBackpatch = pila.pop(); // hago pop() del nro de terceto del BI incompleto


            Integer aux= Integer.parseInt(getReferencia($7).replaceAll("\\D","")) + 1 ;

            String auxString = "(" + String.valueOf(aux) + ")";
            System.out.println(listaTercetos.get(numTercetoBackpatch -1));
            listaTercetos.get(numTercetoBackpatch -1).addSecond(auxString);// completo el tercer operando del BI

            $$=$7;
        }
    |  '{' parte_if_retorno '}' {
            //Saco el nro de terceto del BF incompleto de la pila ya que no hay ELSE
            //Aca no creo BI y no hago +1
            pila.pop();
            $$=$2;
        }
    ;

parte_if_retorno
    :   cuerpo_ejecutable_retorno {
            //aca se completa BF con nro de terceto del cuerpo_ejecutable + 1
            //
            int numTercetoBackpatch = pila.peek();

            //obtengo referencia terceto de cuerpo_ejecutable, parseo a integer  y hago + 1
            Integer aux= Integer.parseInt(getReferencia($1).replaceAll("\\D","")) + 1 ;

            String auxString = "(" + String.valueOf(aux) + ")";
            listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); /* completo el tercer operando del BF*/

            $$=$1;
        }
    ;

parte_else_retorno
    :   cuerpo_ejecutable_retorno
    ;

cuerpo_iteracion_retorno
    :   '{' cuerpo_ejecutable_retorno '}' {
            //Saco el nro de terceto del BF incompleto de la pila
            int numTercetoBackpatch = pila.pop();


            //obtengo referencia terceto de cuerpo_ejecutable, parseo a integer  y hago + 3
            // porque tengo terceto BI y terceto de incremeto de variable de control del for
            System.out.println("Raaa: " + $2.obj);
            Integer aux= Integer.parseInt(getReferencia($2).replaceAll("\\D","")) + 3 ;

            String auxString = "(" + String.valueOf(aux) + ")";
            listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); /* completo el tercer operando del BF*/

        }
//    |   cuerpo_iteracion_error
    ;
cuerpo_ejecutable_retorno
    :   cuerpo_ejecutable sentencia_retorno {
            $$=$2;
        }
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
static Stack<Integer> pila = new Stack<>();
static ArrayList<Terceto> listaTercetos = new ArrayList<>();

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