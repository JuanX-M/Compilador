%{
    import java.io.*;
    import java.util.Scanner;
    import java.util.Stack;
    import java.util.ArrayList;
    import java.util.Comparator;
    import java.math.BigDecimal;
    import Tools.GeneradorAssembler;
    import Lexico.AnalizadorLexico;
    import Tools.GeneradorAssembler;
    import Tools.TablaSimbolos;
    import Tools.Pair;
    import Tools.TablaPalabrasReservadas;
    import Tools.Logger;
    import Tools.Cursor;
    import Tools.Info;
    import Tools.Terceto;

%}

%token TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR FOR FROM TO CR SE LE TOI INT FLOAT ARROW FUN

%nonassoc SENTENCIA_ASIGNACION_PREC
%right TWO_POINTS_ASSIGNATION
%left ID
%nonassoc LOW_PREC
%left '+' '-'
%left '*' '/'
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
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey($1.sval)) {
                Logger.logError(cursor.getCurrentLine(), "Nombre de programa ya existe");
            } else {
                TablaSimbolos.TABLA_SIMBOLOS.put($1.sval, new Info($1.sval, "ID", "PROGRAMA", $1.sval));
            }
            Logger.logRule(cursor.getCurrentLine(), "Sentencia PROG");
            ambito = $1.sval;
            // -------------------------------------------------------
            // NUEVO CÓDIGO: Salto al inicio del programa
            // -------------------------------------------------------
            // 1. Crear el BI (será el Terceto #1)
            ParserVal tercetoBI = crearTerceto(new ParserVal("BI"), null, null);
            listaTercetos.add((Terceto)tercetoBI.obj);
            ((Terceto)tercetoBI.obj).addLine(cursor.getCurrentLine());
            // 2. Crear la Etiqueta de Inicio (será el Terceto #2)
            // Esto marca explícitamente donde empieza la ejecución lógica
            int numTercetoEtiqueta = ((Terceto)tercetoBI.obj).getSoloNumTerceto() + 1;
            String nombreEtiqueta = "ETIQUETA_MAIN"; // O "ETIQUETA" + numTercetoEtiqueta
            ParserVal tercetoLabel = crearTerceto(new ParserVal(nombreEtiqueta), null, null);
            listaTercetos.add((Terceto)tercetoLabel.obj);
            ((Terceto)tercetoLabel.obj).addLine(cursor.getCurrentLine());
            // 3. Completar el BI inmediatamente para que apunte a la etiqueta
            // Usamos setSecond o setThird según tu convención para BI (usualmente es el destino)
            String destino = "(" + String.valueOf(numTercetoEtiqueta) + ")";
            ((Terceto)tercetoBI.obj).addSecond(destino);
            // -------------------------------------------------------
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
    :   funcion  {
        Logger.logRule(cursor.getCurrentLine(), "Sentencia DECLARACION FUNCION");}
    |   declaracion_variable
    ;

sentencia_declarativa_sin_coma
    :   sentencia_declarativa {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de las sentencias.");}
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
            //cuerpo_funcion me devuelve el terceto RET
            int pos = ambito.lastIndexOf('.');
            if (pos != -1) {
                ambito = ambito.substring(0, pos);
            }
            // 1. Sacamos de la pila el número de terceto del BI que se creó en 'lista_tipos'
            int numTercetoBI = pila.pop();
            // 2. Calculamos a dónde debe saltar (al siguiente terceto disponible después de la función)
            // Obtenemos el último terceto generado (que es el RET) y sumamos 1
            int numTercetoDestino = listaTercetos.get(listaTercetos.size()-1).getSoloNumTerceto() + 1;
            String destino = "(" + String.valueOf(numTercetoDestino) + ")";
            // 3. Completamos el BI. Como el BI se creó con (BI, null, null),
            // el destino suele ir en el segundo operando.
            listaTercetos.get(numTercetoBI - 1).addSecond(destino);
        }
    |   sentencia_lambda    {Logger.logRule(cursor.getCurrentLine(), "Sentencia LAMBDA");}
    ;

declaracion_variable
    :   declaracion_unaria
    ;

sentencia_print
    :   PRINT '(' STRING ')' {
            $$ = crearTerceto($1,$3,null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo("STRING");
        }
    |   PRINT '(' lista_exp_aritmeticas ')' {
            /* $3.obj es el ArrayList<ParserVal> de las expresiones */
            ArrayList<ParserVal> expresiones = (ArrayList<ParserVal>)$3.obj;
            /* Iterar y crear un terceto PRINT por cada expresion */
            for (ParserVal expr : expresiones) {
                /* 'expr' es el ParserVal (Terceto o ID) que queremos imprimir */
                $$ = crearTerceto($1, expr, null);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                ((Terceto)$$.obj).addTipo(getTipoParserVal(expr));
            }
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
    :   FOR parametros_iteracion cuerpo_iteracion {
	        if ($2.obj != null){
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
                ((Terceto)$$.obj).addTipo("INT");
                int aux = ((Terceto)$2.obj).getSoloNumTerceto()-1; //Necesito la posicion de la etiqueta
                String aux2 = "(" + String.valueOf(aux) + ")";
                //Creo terceto BI para volver al inicio de la iteracion y lo agrego
                $$= crearTerceto(new ParserVal("BI"), new ParserVal(aux2), null);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                // Creacion etiqueta
                int numTercetoActual = listaTercetos.get(listaTercetos.size()-1).getSoloNumTerceto() + 1;
                String etiqueta = "ETIQUETA" + numTercetoActual;
                $$= crearTerceto(new ParserVal(etiqueta),null,null);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                $$=$3;
	        }
	    }
    ;

sentencia_asignacion
    :   sentencia_asignacion_unaria     {Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION UNARIA");}
    |   sentencia_asignacion_multiple   {Logger.logRule(cursor.getCurrentLine(), "Sentencia ASIGNACION MULTIPLE");}
    ;

encabezado_funcion
    :   lista_tipos FUN ID  {
             String aux = $3.sval + '.' + ambito;
             if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux)) {
             	if (TablaSimbolos.TABLA_SIMBOLOS.get(aux).getUso().contains("Variable"))
             		Logger.logError(cursor.getCurrentLine(), "El identificador '"+$3.sval+"' ya fue declarado como Variable.");
		        else
                	Logger.logError(cursor.getCurrentLine(), "Redeclaracion de funcion " +"'" +$3.sval+ "'");
             } else {
                 TablaSimbolos.TABLA_SIMBOLOS.put(aux ,new Info($3.sval, $2.sval, "INT", "Funcion", ambito));
                 TablaSimbolos.TABLA_SIMBOLOS.get(aux).setListaVariablesRetorno(new ArrayList<>((ArrayList<String>)$1.obj));
                 System.out.println("auxInfo:"+TablaSimbolos.TABLA_SIMBOLOS.get(aux));
             }
             ambito += '.' + $3.sval;
    }   '(' lista_param_formales ')' {
            // $1.obj trae la lista de variables auxiliares de retorno (ej: [aux0.main, aux1.main])
            int indiceCorte = ((ArrayList<String>)$1.obj).size();
            //creacion de tercetos auxiliares con ambito del padre de la funcion
            String auxambito=ambito;
            int pos = auxambito.lastIndexOf('.');
            if (pos != -1) {
                auxambito = auxambito.substring(0, pos);
            }
            //itero en arraylist ((ArrayList<String>)$6.obj) y creo tercetos auxiliares con el tipo
            //del parametro formal
            ArrayList<String> listaParametros = (ArrayList<String>)$6.obj;
            // Buscamos la función en la tabla usando su nombre ($3) y el ámbito del padre (auxambito)
            String claveFuncion = $3.sval + "." + auxambito;
            Info infoFuncion = TablaSimbolos.TABLA_SIMBOLOS.get(claveFuncion);
            if (infoFuncion != null) {
                infoFuncion.setListaParametrosFormales((ArrayList<String>)$6.obj);
            }
            String valorDefecto;
            for (String parametro : listaParametros) {
                String auxString = "aux" + String.valueOf(contadorVariablesAuxTercetos);
                Info pfInfo = TablaSimbolos.TABLA_SIMBOLOS.get(parametro);
                //hago put en la tabla de simbolos de la nueva variable auxiliar
                if (pfInfo.getTipo().equalsIgnoreCase("FLOAT")){
                    TablaSimbolos.TABLA_SIMBOLOS.put(auxString + "."+ auxambito, new Info(auxString, "ID", "FLOAT", "Variable", auxambito));
                    valorDefecto = "-1.0";
                } else {
                    TablaSimbolos.TABLA_SIMBOLOS.put(auxString + "."+ auxambito, new Info(auxString, "ID", "INT", "Variable", auxambito));
                    valorDefecto = "-1";
                }
                //asociacion de aux con parametro formal, para usar en asgincaciones de return
                TablaSimbolos.TABLA_SIMBOLOS.get(parametro).setVarAux(auxString + "."+ auxambito);
                if (!TablaSimbolos.TABLA_SIMBOLOS.get(parametro).getUso().contains("CV")) {
                    ((ArrayList<String>)$1.obj).add(parametro); // hago esto para para luego realizar asignaciones de return al finalizar funcion, solo con parametros CR
                }
                $$= crearTerceto(new ParserVal(":="), new ParserVal(auxString + "."+ auxambito), new ParserVal(valorDefecto));
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                ((Terceto)$$.obj).addTipo(pfInfo.getTipo());
                contadorVariablesAuxTercetos++;
		    }
		    // Creacion etiqueta
		    int numTercetoActual = listaTercetos.get(listaTercetos.size()-1).getSoloNumTerceto() + 1;
		    String etiqueta = "ETIQUETA" + numTercetoActual;
		    $$= crearTerceto(new ParserVal(etiqueta),null,null);
		    listaTercetos.add((Terceto)$$.obj);
		    ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
		    if (infoFuncion != null) {
                infoFuncion.setNroTercetoEtiqueta(getReferencia($$));
            }
		    //creacion de tercetos que inicializan parametros formales
		    for (String parametro : listaParametros) {
			    if (TablaSimbolos.TABLA_SIMBOLOS.get(parametro).getUso().contains("CV")){
			        $$= crearTerceto(new ParserVal(":="),
				    new ParserVal(parametro),
				    new ParserVal(TablaSimbolos.TABLA_SIMBOLOS.get(parametro).getVarAux()));
			    } else {
			        if (TablaSimbolos.TABLA_SIMBOLOS.get(parametro).getTipo().equalsIgnoreCase("FLOAT")){
				        valorDefecto = "-1.0";
			        } else {
				        valorDefecto = "-1";
			        }
			        $$= crearTerceto(new ParserVal(":="),
			        new ParserVal(parametro),
			        new ParserVal(valorDefecto));
			    }
		        listaTercetos.add((Terceto)$$.obj);
		        ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
		        ((Terceto)$$.obj).addTipo(TablaSimbolos.TABLA_SIMBOLOS.get(parametro).getTipo());
		    }
            $$.obj = $1.obj; //devuelvo arraylist de variables auxiliares de tipos + parametros formales
            $$.ival = indiceCorte;
        }
    |   encabezado_funcion_error
    ;

encabezado_funcion_error
    :   lista_tipos FUN '(' lista_param_formales ')'  {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en declaracion de funcion");}
    ;

cuerpo_funcion
    :   lista_sentencias_funcion sentencia_retorno {
            $$ = $2;
        }
    |   sentencia_retorno
    |   lista_sentencias_funcion_error
    ;

lista_sentencias_funcion_error
    :   lista_sentencias_funcion sentencia_retorno lista_sentencias_funcion{
            $$ = $2;
            Logger.logError(cursor.getCurrentLine(), "Sentencias ejecutables luego de un return obligatorio");
        }
    | lista_sentencias_funcion{
                  $$ = $1;
                  Logger.logError(cursor.getCurrentLine(), "Falta sentencia return al final de la funcion");
              }
    ;

lista_sentencias_funcion
    :   lista_sentencias_funcion sentencia_funcion
    |   sentencia_funcion
    ;

sentencia_lambda
    :   parametro_lambda {
            if ($1.sval != null){
                // asignamos el argumento al paramtero
                if (TablaSimbolos.TABLA_SIMBOLOS.get($1.sval).getTipo().contains("INT")){
                    $$ = crearTerceto(new ParserVal(":="),$1,new ParserVal("-1"));
                } else {
                    $$ = crearTerceto(new ParserVal(":="),$1,new ParserVal("-1.0"));
                }

                int numTercetoActual = ((Terceto)$$.obj).getSoloNumTerceto();
                pila.push(numTercetoActual);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                ((Terceto)$$.obj).addTipo(TablaSimbolos.TABLA_SIMBOLOS.get($1.sval).getTipo());
            }
        }   cuerpo_lambda argumento_lambda {
                if ($4.sval != null && $1.sval != null) {
                    Info infoPar = TablaSimbolos.TABLA_SIMBOLOS.get($1.sval);
                    Info infoArg = TablaSimbolos.TABLA_SIMBOLOS.get($4.sval);
                    int numTercetoBackpatch = pila.pop();
                    if (infoPar != null && infoArg != null){
                        if(infoArg.getUso().contains("SE")){
                            Logger.logError(cursor.getCurrentLine(), "El Argumento '" +infoArg.getNombre()+ "' es SE, no puede ser leido");
                        }
                        if(infoPar.getTipo().equals(infoArg.getTipo())) {
                            listaTercetos.get(numTercetoBackpatch -1).addThird(getReferencia($4));
                        } else {
                            listaTercetos.remove(numTercetoBackpatch -1);
                            Logger.logError(cursor.getCurrentLine(), "Incompatibilidad de tipos en parametro y argumento de sentencia Lambda");
                        }
                    } else {
                        Logger.logError(cursor.getCurrentLine(), "Incompatibilidad de tipos en parametro y argumento de sentencia Lambda");
                    }
                }
            }
    ;

declaracion_unaria
    :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            String aux = $2.sval + '.' + ambito;
            ParserVal auxParserVal= $4;
            if ( auxParserVal.obj!=null && auxParserVal.obj.getClass().equals(java.util.ArrayList.class)){
                 Logger.logError(cursor.getCurrentLine(), "Funcion con retorno multiple en declaracion simple.");
                 // Recuperación de error: tomamos el primero para seguir compilando
                 auxParserVal = ((ArrayList<ParserVal>)auxParserVal.obj).get(0);
            }
            String tipoInferido = getTipoParserVal(auxParserVal);
            // Verificamos si el símbolo ya existe en el ámbito actual
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux)) {
                Info infoExistente = TablaSimbolos.TABLA_SIMBOLOS.get(aux);
                String uso = infoExistente.getUso();
                if (uso.equals("Funcion")) {
                    Logger.logError(cursor.getCurrentLine(), "El identificador '" + $2.sval + "' ya fue declarado como Función.");
                } else {
                    Logger.logError(cursor.getCurrentLine(), "Redeclaración de variable '" + $2.sval + "'.");
                }
            } else {
                // Si no existe, lo agregamos normalmente
                TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($2.sval, "ID", tipoInferido, "Variable", ambito));
            }
            $2.sval = aux;
            $$ = crearTerceto($3, $2, auxParserVal);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo(tipoInferido);
        }
    |   declaracion_unaria_error
    ;

declaracion_unaria_error
    :   VAR ID expresion_aritmetica    {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion en declaracion de variable");}
    |   VAR ID'.'ID  TWO_POINTS_ASSIGNATION expresion_aritmetica    {Logger.logError(cursor.getCurrentLine(), "Declaracion de una variable con ambito");}
    ;

lista_exp_aritmeticas
    :   lista_exp_aritmeticas ',' expresion_aritmetica %prec LOW_PREC {
            ArrayList<ParserVal> listaPrincipal = (ArrayList<ParserVal>)$1.obj;
            ParserVal nuevaExpresion = $3;
            // Chequeo si lo que viene es una lista (retorno múltiple) usando getClass
            if (nuevaExpresion.obj != null && nuevaExpresion.obj.getClass().equals(java.util.ArrayList.class)) {
                // CASO: Función con múltiples retornos.
                // Concatenamos los retornos a la lista principal.
                ArrayList<ParserVal> retornosFuncion = (ArrayList<ParserVal>) nuevaExpresion.obj;
                listaPrincipal.addAll(retornosFuncion);
            } else {
                // CASO: Expresión simple o función con 1 retorno.
                // Lo agregamos como un elemento más.
                listaPrincipal.add(nuevaExpresion);
            }
            $$ = $1; // Pasa la lista acumulada hacia arriba
        }
    |   expresion_aritmetica %prec LOW_PREC {
            // $1 es el ParserVal de 'expresion_aritmetica'
            if ($1.obj != null && $1.obj.getClass().equals(java.util.ArrayList.class)) {
                // Es de una funcion, $1.obj ya es el ArrayList<ParserVal> que queremos
                $$ = $1;
            } else {
                // Es una expresion simple o funcion con 1 retorno.
                // Creamos una nueva lista y lo agregamos.
                ArrayList<ParserVal> exprs = new ArrayList<>();
                exprs.add($1);
                $$ = new ParserVal(exprs);
            }
        }
    |   lista_exp_aritmeticas_error
    ;

lista_exp_aritmeticas_error
    :   lista_exp_aritmeticas expresion_aritmetica %prec LOW_PREC    {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de expresiones aritmeticas (lado derecho)");}
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
            //obtengo referencia terceto de cuerpo_ejecutable, parseo a integer  y hago + 3
            // porque tengo terceto BI y terceto de incremeto de variable de control del for
            if ($2.sval != null){
                //Saco el nro de terceto del BF incompleto de la pila
                int numTercetoBackpatch = pila.pop();
                Integer aux= Integer.parseInt(getReferencia($2).replaceAll("\\D","")) + 3 ;
	    	String auxString = "(" + String.valueOf(aux) + ")";
	    	listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); /* completo el tercer operando del BF*/
	    }
    	}
    |   cuerpo_iteracion_error
    ;

cuerpo_iteracion_error
    :   '{'  '}'    {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en iteracion");}
    ;

sentencia_asignacion_unaria
    :   ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            //Chequeo ambito
            if (!TablaSimbolos.TABLA_SIMBOLOS.containsKey($1.sval + '.' + ambito)) {
                Logger.logError(cursor.getCurrentLine(), "Variable '"+ $1.sval +"' sin declarar");
            }else {
                //obtengo ambito en donde fue declarada
                $1.sval = $1.sval + '.' + ambito;
            }
            ParserVal auxParserVal = $3;
            if (auxParserVal.obj!=null && auxParserVal.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Asignación de retorno múltiple a variable simple.");
                auxParserVal = ((ArrayList<ParserVal>)auxParserVal.obj).get(0);
            }
            if (!checkTipo($1, auxParserVal)) {
                Logger.logError(cursor.getCurrentLine(), "Error de tipo en asignación entre " + getTipoParserVal($1) + " y " + getTipoParserVal(auxParserVal) );
            }
            $$ = crearTerceto($2, $1, auxParserVal);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo(getTipoParserVal(auxParserVal));
        }
    |   ID'.'ID TWO_POINTS_ASSIGNATION expresion_aritmetica {
            //Chequeo ambito
            String nombreAmbitoActual = "";
            int ultimoPunto = ambito.lastIndexOf('.');
            if (ultimoPunto != -1) {
                nombreAmbitoActual = ambito.substring(ultimoPunto + 1);
            } else {
                nombreAmbitoActual = ambito;
            }
            // Comparamos si el ID del ámbito especificado ($1.sval) es igual al actual
            if ($1.sval.equals(nombreAmbitoActual)) {
                 Logger.logError(cursor.getCurrentLine(), "No se permite especificar mismo ambito en variables de ambito local.");
            }
            String ambitoaux = ambito;
            if (ambito.contains($1.sval)) {
                int indiceInicio = ambito.indexOf($1.sval);
                int indiceFinal = indiceInicio + $1.sval.length();
                ambitoaux = ambito.substring(0, indiceFinal);
            }else {
                Logger.logError(cursor.getCurrentLine(), "Funcion " + $1.sval +" no esta al alcance");
            }
            if (getScope(ambitoaux,$3.sval) == null) {
                Logger.logError(cursor.getCurrentLine(), "Variable con ambito '"+ $1.sval + "."+$3.sval +"' sin declarar");
            }else {
                //obtengo ambito en donde fue declarada
                $3.sval = $3.sval + '.' + getScope(ambitoaux,$3.sval);
            }
            ParserVal auxParserVal =$5;
            if (auxParserVal.obj!=null && auxParserVal.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Asignación de retorno múltiple a variable simple.");
                auxParserVal= ((ArrayList<ParserVal>)auxParserVal.obj).get(0);
            }
            if (!checkTipo($3, auxParserVal))
                Logger.logError(cursor.getCurrentLine(), "Error de tipo en asignación entre " + getTipoParserVal($3) + " y " + getTipoParserVal(auxParserVal) );
            $$ = crearTerceto($4, $3, auxParserVal);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo(getTipoParserVal(auxParserVal));
        }
    |   sentencia_asignacion_unaria_error
    ;

sentencia_asignacion_unaria_error
    :   ID expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion");}
    |   ID'.'ID expresion_aritmetica     {Logger.logError(cursor.getCurrentLine(), "Falta de asignacion");}
    ;

sentencia_asignacion_multiple
    :   lista_variables  '=' lista_exp_aritmeticas %prec SENTENCIA_ASIGNACION_PREC {
            ArrayList<ParserVal> listaVariables = (ArrayList<ParserVal>)$1.obj;
            ArrayList<ParserVal> listaExpresiones = (ArrayList<ParserVal>)$3.obj;
            /*
                Para distinguir cantidad de retornos de una funcion pregunto por el ival de cada parserval en  listaExpresiones
                Esto ya que concateno los auxiliares con lista de expresiones aritmeticas y en cada parserval de esto poner ahi el ival
            */
            boolean hayFuncion=false;
            int k =0;
            while (k < listaExpresiones.size() && !hayFuncion){
                    if (listaExpresiones.get(k).ival != 0){
                        hayFuncion=true;
                    }
                    k++;
            }
            if (hayFuncion){
                if (listaVariables.size() < listaExpresiones.size()) {
                    Logger.logWarning(cursor.getCurrentLine(),
                        "La cantidad de variables (" + listaVariables.size() + ") es menor a la cantidad de asignaciones (" + listaExpresiones.size() + ")" + " se descartaran " + (listaExpresiones.size() - listaVariables.size()) );
                }else if (listaVariables.size() > listaExpresiones.size()){

                        Logger.logError(cursor.getCurrentLine(), "La cantidad de variables (" + listaVariables.size() + ") es mayor a la cantidad de asignaciones (" + listaExpresiones.size() + ").");
                }
                for (int i = 0; i < listaVariables.size() && i < listaExpresiones.size() ; i++) {
                    ParserVal variable = listaVariables.get(i);   // El ParserVal de la variable (contiene sval)
                    ParserVal expresion = listaExpresiones.get(i); // El ParserVal de la expresion (contiene sval o obj)
                    if (!checkTipo(variable, expresion)) {
                         Logger.logError(cursor.getCurrentLine(), "Tipo en asignación múltiple (pos " + (i+1) + "): " + "No se puede asignar " + getTipoParserVal(expresion) + " a " + getTipoParserVal(variable));
                    }
                    $$ = crearTerceto(new ParserVal(":="), variable, expresion);
                    listaTercetos.add((Terceto)$$.obj);
                    ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                    ((Terceto)$$.obj).addTipo(getTipoParserVal(expresion));
                }
            } else{
                if (listaVariables.size() != listaExpresiones.size()) {
                    Logger.logError(cursor.getCurrentLine(),
                        "La cantidad de variables (" + listaVariables.size() + ") no coincide con la cantidad de asignaciones (" + listaExpresiones.size() + ").");
                }
                for (int i = 0; i < listaVariables.size() && i < listaExpresiones.size() ; i++) {
                ParserVal variable = listaVariables.get(i);
                ParserVal expresion = listaExpresiones.get(i);
                if (!checkTipo(variable, expresion)) {
                     Logger.logError(cursor.getCurrentLine(), "Tipo en asignación múltiple (pos " + (i+1) + "): " + "No se puede asignar " + getTipoParserVal(expresion) + " a " + getTipoParserVal(variable));
                }
                $$ = crearTerceto(new ParserVal(":="), variable, expresion);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                ((Terceto)$$.obj).addTipo(getTipoParserVal(expresion));
            }
            }
        }
    ;

lista_tipos
    :   lista_tipos ',' tipo    {
            //creacion variable auxiliar para poner en la tabla
            String auxString = "aux" + String.valueOf(contadorVariablesAuxTercetos);
            //defino con que tipo de valor debo inicializar terceto auxiliar
            if ($3.sval.contains("float")){
                TablaSimbolos.TABLA_SIMBOLOS.put(auxString + "."+ ambito, new Info(auxString, "ID", "FLOAT", "Variable", ambito));
                $3.sval = "-1.0";
            } else {
                TablaSimbolos.TABLA_SIMBOLOS.put(auxString + "."+ ambito, new Info(auxString, "ID", "INT", "Variable", ambito));
                $3.sval = "-1";
            };
            //creacion de terceto con variable auxiliar
            //la varaible auxiliar tiene mismo ambito que la funcion, no dentro de esta
            $$=crearTerceto(new ParserVal (":="), new ParserVal (auxString+"."+ambito), new ParserVal ($3.sval));
            contadorVariablesAuxTercetos++;
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((ArrayList<String>)$1.obj).add(auxString+"."+ambito); // Agrega aux al arraylist}
            ((Terceto)$$.obj).addTipo($1.sval);
            $$ = $1; // Pasa la lista modificada hacia arriba, no terceto auxiliar creado ni auxString
        }
    |   tipo    {
            //Aca se genera Terceto BI incompleto para saltar al siguiente nro terceto para no ejecutar la funcion
            $$=crearTerceto(new ParserVal ("BI"), null,null);
            contadorVariablesAuxTercetos++;
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            pila.push( ((Terceto)$$.obj).getSoloNumTerceto());
            ArrayList<String> listaVariablesAux = new ArrayList<>();
            //lista con variables aux con ambito, se utilizan para chequeos y asignacion luego en los return
            //creacion variable auxiliar para poner en la tabla
            String auxString = "aux" + String.valueOf(contadorVariablesAuxTercetos);
            //defino con que tipo de valor debo inicializar terceto auxiliar
            if ($1.sval.contains("float")){
                TablaSimbolos.TABLA_SIMBOLOS.put(auxString + "."+ ambito, new Info(auxString, "ID", "FLOAT", "Variable", ambito));
                $1.sval = "-1.0";
            } else {
                TablaSimbolos.TABLA_SIMBOLOS.put(auxString + "."+ ambito, new Info(auxString, "ID", "INT", "Variable", ambito));
                $1.sval = "-1";
            };
            //creacion de terceto con variable auxiliar
            //la varaible auxiliar tiene mismo ambito que la funcion, no dentro de esta
            $$=crearTerceto(new ParserVal (":="), new ParserVal (auxString+"."+ambito), new ParserVal ($1.sval));
            contadorVariablesAuxTercetos++;
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo($1.sval);
            listaVariablesAux.add(auxString+"."+ambito);
            $$ = new ParserVal(listaVariablesAux);//paso lista de tipos, no la variable
        }
    |   lista_tipos_error
    ;

lista_tipos_error
    :   lista_tipos tipo {Logger.logError(cursor.getCurrentLine(), "Falta de ',' en declaracion de tipos");}
    ;

lista_param_formales
    :   lista_param_formales ',' parametro_formal{
            ((ArrayList<String>)$1.obj).add($3.sval); // Agrega el nuevo parametro formal
            $$ = $1; // Pasa la lista modificada hacia arriba
        }
    |   parametro_formal {
            ArrayList<String> listaParametros = new ArrayList<>();
            //lista con parametros formales con ambito
            listaParametros.add($1.sval);
            $$ = new ParserVal(listaParametros);//paso lista de tipos, no la variable
        }
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
    :   '(' tipo ID ')' {
            ambito += ".lambda" + cursor.getCurrentLine();
            String aux = $3.sval + "." + ambito;
            TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($3.sval, "ID", $2.sval.toUpperCase(), "Variable", ambito));
            $$.sval = aux;
        }
        | parametro_lambda_error
    ;

parametro_lambda_error
    :   '(' ID ')' {
            $$.sval = null;
            Logger.logError(cursor.getCurrentLine(), "Falta del tipo en parametro_lambda");
        }
    |   '(' tipo ')' {
            $$.sval = null;
            Logger.logError(cursor.getCurrentLine(), "Falta del ID en parametro_lambda");
        }
    |   '(' error ')' {
            $$.sval = null;
            Logger.logError(cursor.getCurrentLine(), "Falta del tipo e ID en parametro_lambda");
        }
    ;

cuerpo_lambda
    :   '{' cuerpo_ejecutable '}'
    |   cuerpo_lambda_error
    ;

cuerpo_lambda_error
    :   cuerpo_ejecutable '}'   {Logger.logError(cursor.getCurrentLine(), "Falta delimitador izquierdo '{' del cuerpo lambda");}
    //|   '{' cuerpo error      {Logger.logError(cursor.getCurrentLine(), "Falta delimitador derecho '}' del cuerpo lambda");}
    |   '{' '}'                 {Logger.logError(cursor.getCurrentLine(), "Falta de cuerpo en sentencia lambda");}
    ;

argumento_lambda
    :   '(' variable ')'  {
            reducirAmbito();
            $$.sval = $2.sval;
        }
    |   '(' CTE_INT ')' {
            reducirAmbito();
            controlarEntero($2.sval);
            $$.sval = $2.sval;
        }
    |   '(' CTE_FLOAT ')' {
            reducirAmbito();
            controlarFlotante($2.sval);
            $$.sval = $2.sval;
        }
    | argumento_lambda_error
    ;

argumento_lambda_error
    :   '(' error ')' {
            $$.sval = null;
            Logger.logError(cursor.getCurrentLine(), "Argumento de lambda invalido");
        }


expresion_aritmetica
    :   expresion_aritmetica '+' termino  {
            ParserVal opIzq = $1;
            ParserVal opDer = $3;
            if (opIzq.obj != null && opIzq.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando izquierdo es función con retorno múltiple.");
                opIzq = ((ArrayList<ParserVal>)opIzq.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opIzq.sval).getTipo().contains("INT")){
                       opIzq.ival = 1;
                } else {
                    opIzq.ival = 2;
                }
            }
            if (opDer.obj != null && opDer.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando derecho es función con retorno múltiple.");
                opDer = ((ArrayList<ParserVal>)opDer.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opDer.sval).getTipo().contains("INT")){
                       opDer.ival = 1;
                } else {
                    opDer.ival = 2;
                }
            }
            if (!checkTipo(opIzq, opDer)) {
                 Logger.logError(cursor.getCurrentLine(), "Tipos incompatibles en suma entre " + getTipoParserVal(opIzq) + " y " + getTipoParserVal(opDer));
            }
            $$ = crearTerceto($2, opIzq, opDer);
            $$.sval = getTipoParserVal(opIzq);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo($$.sval);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   expresion_aritmetica '-' termino{
            ParserVal opIzq = $1;
            ParserVal opDer = $3;
            if (opIzq.obj != null && opIzq.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando izquierdo es función con retorno múltiple.");
                opIzq = ((ArrayList<ParserVal>)opIzq.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opIzq.sval).getTipo().contains("INT")){
                    opIzq.ival = 1;
                } else {
                    opIzq.ival = 2;
                }
            }
            if (opDer.obj != null && opDer.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando derecho es función con retorno múltiple.");
                opDer = ((ArrayList<ParserVal>)opDer.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opDer.sval).getTipo().contains("INT")){
                       opDer.ival = 1;
                } else {
                    opDer.ival = 2;
                }
            }
            if (!checkTipo(opIzq, opDer)) {
                 Logger.logError(cursor.getCurrentLine(), "Tipos incompatibles en resta entre " + getTipoParserVal(opIzq) + " y " + getTipoParserVal(opDer));
            }
            $$ = crearTerceto($2, opIzq, opDer);
            $$.sval = getTipoParserVal(opIzq);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo($$.sval);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   expresion_aritmetica_toi {
            $$ = $1;
            //devolvemos el terceto
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
            //TODO: HAY QUE CHEQUEAR SI ES UNA FUNCION CON MULTIPLES RETORNOS, SI HAY EN ALGUNO DE AMBOS LADOS TIRAR ERROR SEMANTICO
            //Y AGARRAR EL DEL POSICION 0
            if (!checkTipo($1, $3)) {
                 Logger.logError(cursor.getCurrentLine(), "Tipos incompatibles en condicion entre " + getTipoParserVal($1) + " y " + getTipoParserVal($3));
            }
            //Crea el terceto de la condicion
            $$ = crearTerceto($2, $1, $3);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo(getTipoParserVal($3));
        }
    |   condicion_error
    ;

condicion_error
    :   expresion_aritmetica error   {Logger.logError(cursor.getCurrentLine(), "Falta de simbolo comparador en condicion");}
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
            String aux = $1.sval + '.' + ambito;
            if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux)) {
                Logger.logError(cursor.getCurrentLine(), "Redeclaracion de variable");}
            else {
                //Creacion y Asignacion de variable de control del for
                TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($1.sval, "CTE_INT", "INT", "Variable", ambito));
                $$ = crearTerceto(new ParserVal(":="),new ParserVal(aux),$3);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                ((Terceto)$$.obj).addTipo("INT");
                // Creacion etiqueta
                int numTercetoActual = listaTercetos.get(listaTercetos.size()-1).getSoloNumTerceto() + 1;
                String etiqueta = "ETIQUETA" + numTercetoActual;
                $$= crearTerceto(new ParserVal(etiqueta),null,null);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            };
            //ACA
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
                ((Terceto)$$.obj).addTipo("INT");
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
    :   lista_variables ',' variable    {
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
    :   semantica_pasaje tipo ID {
            //hago put del parametro formal en tabla de simbolos
            String auxString= $3.sval + "."+ ambito;
            TablaSimbolos.TABLA_SIMBOLOS.put(auxString, new Info($3.sval, "ID", $2.sval.toUpperCase(), "PF " +$1.sval.toUpperCase(), ambito));
            $$.sval = auxString;
        }
    |   tipo ID{
            //hago put del parametro formal en tabla de simbolos
            String auxString= $2.sval + "."+ ambito;
            TablaSimbolos.TABLA_SIMBOLOS.put(auxString, new Info($2.sval, "ID", $1.sval.toUpperCase(),"PF CV", ambito));
            $$.sval = auxString;
        }
    |   parametro_formal_error
    ;

parametro_formal_error
    :   semantica_pasaje tipo   {
            Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");
            String aux = "PARAM_ERROR_" + cursor.getCurrentLine() + "." + ambito;
            TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info("PARAM_ERROR", "ID", $2.sval, "PF CV", ambito));
            $$.sval = aux;
        }
    |   tipo {
            Logger.logError(cursor.getCurrentLine(), "Falta el nombre de parametro formal en declaracion de funcion");
            String dummyName = "PARAM_ERROR_" + cursor.getCurrentLine() + "." + ambito;
            TablaSimbolos.TABLA_SIMBOLOS.put(dummyName, new Info("PARAM_ERROR", "ID", $1.sval, "PF CV", ambito));
            $$.sval = dummyName;
        }
    |   semantica_pasaje ID     {
            Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");
            String aux = $2.sval + "." + ambito;
            // Lo registramos para que si se usa en el cuerpo, exista.
            TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($2.sval, "ID", "SIN TIPO", "PF " + $1.sval.toUpperCase(), ambito));
            $$.sval = aux;
        }
    |   ID                      {
            Logger.logError(cursor.getCurrentLine(), "Falta de tipo en parametro formal en declaracion de funcion");
            String aux = $1.sval + "." + ambito;
            TablaSimbolos.TABLA_SIMBOLOS.put(aux, new Info($1.sval, "ID", "SIN TIPO", "PF ", ambito));
            $$.sval = aux;
        }
    ;

sentencia_ejecucion_retorno
    :   sentencia_seleccion_retorno
    |   sentencia_iteracion_retorno
    ;

sentencia_retorno
    : RETURN '(' lista_exp_aritmeticas ')' ';' {
            int ultimoPunto = ambito.lastIndexOf('.');
            String nombreFuncion = ambito.substring(ultimoPunto + 1);
            String ambitoPadre = ambito.substring(0, ultimoPunto);
            Info infoFuncion=null;
            String claveBusqueda = nombreFuncion + "." + ambitoPadre;
            infoFuncion = TablaSimbolos.TABLA_SIMBOLOS.get(claveBusqueda);
            if (infoFuncion != null && !infoFuncion.getUso().contains("Variable")) {
                ArrayList<String> listaVariablesRetorno = infoFuncion.getListaVariablesRetorno();
                ArrayList<String> listaParametrosFormales = infoFuncion.getListaParametrosFormales();
                ArrayList<ParserVal> listaExpAritmeticas = (ArrayList<ParserVal>)$3.obj;
                if (listaVariablesRetorno.size() > listaExpAritmeticas.size()) {
                    Logger.logError(cursor.getCurrentLine(),
                        "La función declara " + listaVariablesRetorno.size() +
                        " tipos de retorno, pero retorna solo " + listaExpAritmeticas.size() + " valores.");
                } else if (listaExpAritmeticas.size() > listaVariablesRetorno.size()) {
                    Logger.logError(cursor.getCurrentLine(),
                        "La función retorna " + listaExpAritmeticas.size() +
                        " valores, pero solo se declararon " + listaVariablesRetorno.size() + " tipos de retorno.");
                }
                // Generación de tercetos de asignación de retorno
                for (int i = 0; i < listaVariablesRetorno.size() && i < listaExpAritmeticas.size(); i++){
                    ParserVal variableEsperada = new ParserVal(listaVariablesRetorno.get(i));
                    ParserVal expresionRetornada = listaExpAritmeticas.get(i);

                    if (!checkTipo(variableEsperada, expresionRetornada)) {
                        Logger.logError(cursor.getCurrentLine(),
                            "Error de tipo en retorno (posición " + (i+1) + "): Se esperaba " +
                            getTipoParserVal(variableEsperada) + " pero se encontró " + getTipoParserVal(expresionRetornada));
                    }
                    ParserVal t = crearTerceto(new ParserVal(":="), variableEsperada, expresionRetornada);
                    listaTercetos.add((Terceto)t.obj);
                    ((Terceto)t.obj).addLine(cursor.getCurrentLine());
                }
                // Asignación de parámetros formales CR (Copia Resultado) a auxiliares
                if (listaParametrosFormales != null) {
                    for (int i = 0; i < listaParametrosFormales.size(); i++){
                        Info paramInfo = TablaSimbolos.TABLA_SIMBOLOS.get(listaParametrosFormales.get(i));
                        if (paramInfo != null && paramInfo.getUso().contains("CR")){
                            String auxString = paramInfo.getVarAux();
                            ParserVal asig = crearTerceto(new ParserVal(":="), new ParserVal(auxString), new ParserVal(listaParametrosFormales.get(i)));
                            listaTercetos.add((Terceto)asig.obj);
                            ((Terceto)asig.obj).addLine(cursor.getCurrentLine());
                        }
                    }
            }
        }
            ParserVal tRet = crearTerceto(new ParserVal("RET"), null, null);
            listaTercetos.add((Terceto)tRet.obj);
            ((Terceto)tRet.obj).addLine(cursor.getCurrentLine());
            $$ = tRet;
       }
    | sentencia_retorno_sin_coma
    ;

sentencia_retorno_sin_coma
    : RETURN '(' lista_exp_aritmeticas ')'  {Logger.logError(cursor.getCurrentLine(), "Falta de ';' al final de la sentencia return.");}
    ;

termino
    :   termino '*' factor {
            ParserVal opIzq = $1;
            ParserVal opDer = $3;
            if (opIzq.obj != null && opIzq.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando izquierdo es función con retorno múltiple.");
                opIzq = ((ArrayList<ParserVal>)opIzq.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opIzq.sval).getTipo().contains("INT")){
                       opIzq.ival = 1;
                } else {
                    opIzq.ival = 2;
                }
            }
            if (opDer.obj != null && opDer.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando derecho es función con retorno múltiple.");
                opDer = ((ArrayList<ParserVal>)opDer.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opDer.sval).getTipo().contains("INT")){
                       opDer.ival = 1;
                } else {
                    opDer.ival = 2;
                }
            }
            if (!checkTipo(opIzq, opDer)) {
                 Logger.logError(cursor.getCurrentLine(), "Tipos incompatibles en multiplicacion entre " + getTipoParserVal(opIzq) + " y " + getTipoParserVal(opDer));
            }
            $$ = crearTerceto($2, opIzq, opDer);
            $$.sval = getTipoParserVal(opIzq);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo($$.sval);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   termino '/' factor{
            ParserVal opIzq = $1;
            ParserVal opDer = $3;
            if (opIzq.obj != null && opIzq.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando izquierdo es función con retorno múltiple.");
                opIzq = ((ArrayList<ParserVal>)opIzq.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opIzq.sval).getTipo().contains("INT")){
                       opIzq.ival = 1;
                } else {
                    opIzq.ival = 2;
                }
            }
            if (opDer.obj != null && opDer.obj.getClass().equals(java.util.ArrayList.class)){
                Logger.logError(cursor.getCurrentLine(), "Operando derecho es función con retorno múltiple.");
                opDer = ((ArrayList<ParserVal>)opDer.obj).get(0);
                if (TablaSimbolos.TABLA_SIMBOLOS.get(opDer.sval).getTipo().contains("INT")){
                       opDer.ival = 1;
                } else {
                    opDer.ival = 2;
                }
            }
            if (!checkTipo(opIzq, opDer)) {
                 Logger.logError(cursor.getCurrentLine(), "Tipos incompatibles en division entre " + getTipoParserVal(opIzq) + " y " + getTipoParserVal(opDer));
            }

            $$ = crearTerceto($2, opIzq, opDer);
            $$.sval = getTipoParserVal(opIzq);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            ((Terceto)$$.obj).addTipo($$.sval);
            Logger.logRule(cursor.getCurrentLine(), "Sentencia EXPRESION ARITMETICA");
        }
    |   factor {$$ = $1;}
    |   termino_error
    ;

termino_error
    :   termino '*' error   {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en factmetica con '*'");}
    |   termino '/' error   {Logger.logError(cursor.getCurrentLine(), "Falta de operando izquierdo en expresion_aritmetica con '/'");}
    |   error '*' factor    {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '*'");}
    |   error '/' factor    {Logger.logError(cursor.getCurrentLine(), "Falta de operando derecho en expresion_aritmetica con '-'");}
    ;

expresion_aritmetica_toi
    :   TOI '(' expresion_aritmetica ')' {
            int indice = ambito.indexOf('.');
            String ambitotoi = (indice != -1) ? ambito.substring(0, indice) : ambito;
            System.out.println("Ambito TOI: " + ambitotoi);
            if (getTipoParserVal($3).equals("INT")){
            	Logger.logWarning(cursor.getCurrentLine(), "Variable en sentencia TOI ya es de tipo entero");
            } else {
	    	    if (!TablaSimbolos.TABLA_SIMBOLOS.containsKey("auxtoi")){
		            TablaSimbolos.TABLA_SIMBOLOS.put("auxtoi", new Info("auxtoi", "ID", "INT", "Variable", ambitotoi));
	            }
	            $$=crearTerceto($1,$3,null);
	            listaTercetos.add((Terceto)$$.obj);
	            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
	            ((Terceto)$$.obj).addTipo("FLOAT");
	            $$ = crearTerceto(new ParserVal(":="),new ParserVal("auxtoi"),$$);
                listaTercetos.add((Terceto)$$.obj);
                ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                ((Terceto)$$.obj).addTipo("INT");
                $$ = new ParserVal("auxtoi");
            }
        }
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
    :   ID  {
            //Chequeo ambito
            if (!TablaSimbolos.TABLA_SIMBOLOS.containsKey($1.sval + '.' + ambito)) {
                Logger.logError(cursor.getCurrentLine(), "Variable '"+ $1.sval +"' sin declarar");
            }else {
                //obtengo ambito en donde fue declarada
                $1.sval = $1.sval + '.' + ambito;
            }
            $$=$1;
        }
    |   ID  '.' ID {
            //Chequeo ambito
            String nombreAmbitoActual = "";
            int ultimoPunto = ambito.lastIndexOf('.');
            if (ultimoPunto != -1) {
                nombreAmbitoActual = ambito.substring(ultimoPunto + 1);
            } else {
                nombreAmbitoActual = ambito;
            }
            // Comparamos si el ID del ámbito especificado ($1.sval) es igual al actual
            if ($1.sval.equals(nombreAmbitoActual)) {
                 Logger.logError(cursor.getCurrentLine(), "No se permite especificar mismo ambito en variables de ambito local.");
            }
            String ambitoaux = ambito;
            if (ambito.contains($1.sval)) {
                int indiceInicio = ambito.indexOf($1.sval);
                int indiceFinal = indiceInicio + $1.sval.length();
                ambitoaux = ambito.substring(0, indiceFinal);
            }else {
                Logger.logError(cursor.getCurrentLine(), "Funcion " + $1.sval +" no esta al alcance");
            }
            if (getScope(ambitoaux,$3.sval) == null) {
                Logger.logError(cursor.getCurrentLine(), "Variable con ambito '"+ $1.sval + "."+$3.sval +"' sin declarar");
            }else {
                //obtengo ambito en donde fue declarada
                $3.sval = $3.sval + '.' + getScope(ambitoaux,$3.sval);
            }
            $$=$3;
        }
    ;

semantica_pasaje
    :   CR SE {
            $$.sval = $1.sval + " " + $2.sval;
        }
    |   CR LE{
            $$.sval = $1.sval + " " + $2.sval;
        }
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
            ((Terceto)$$.obj).addTipo("INT");
            //Creo terceto BI para volver al inicio de la iteracion y lo agrego
            $$= crearTerceto(new ParserVal("BI"), $2, null);
            listaTercetos.add((Terceto)$$.obj);
            ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
            $$=$3;
        }
    ;

factor
    :   '-'  CTE_INT {
            String resString = $1.sval + $2.sval;
            controlarEntero(resString);
            ParserVal resultado = new ParserVal($1.sval + $2.sval);
            $$.sval = getTipoParserVal($2);
            $$ = resultado;
        }
    |   CTE_INT {
            String resString = $1.sval;
            controlarEntero(resString);
            $$=$1;
        }
    |   '-' CTE_FLOAT    {
            String resString = $1.sval + $2.sval;
            controlarFlotante(resString);
            ParserVal resultado = new ParserVal($1.sval + $2.sval);
            $$.sval = getTipoParserVal($2);
            $$ = resultado;
        }
    |   CTE_FLOAT {
            String resString = $1.sval;
            controlarFlotante(resString);
            $$=$1;
        }
    |   invocacion_funcion {
            //Crear BI a con dest al primer terceto de la funcion
            Logger.logRule(cursor.getCurrentLine(), "Sentencia INVOCACION FUNCION");
        }
    |   variable{
            if ($1.sval != null) {
                Info info = TablaSimbolos.TABLA_SIMBOLOS.get($1.sval);
                if (info != null && info.getUso() != null) {
                    // Si el uso contiene "SE"
                    if (info.getUso().contains("SE")) {
                        Logger.logError(cursor.getCurrentLine(), "Error semántico: El parámetro '" + info.getNombre() + "' es de SOLO ESCRITURA (SE) y no puede estar del lado derecho (lectura).");
                    }
                }
            }
        }
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
            //Saco el nro de terceto del BI incompleto de la pila para completarlo
            int numTercetoBackpatch = pila.pop(); // pop() del BI
            // $7 (parte_else_retorno) ya añadió sus tercetos a 'listaTercetos'.
            // Obtenemos el último terceto de la lista global para el backpatch.
            Terceto ultimoTercetoDelBloque = listaTercetos.get(listaTercetos.size() - 1);
            Integer aux = ultimoTercetoDelBloque.getSoloNumTerceto() + 1;
            String auxString = "(" + String.valueOf(aux) + ")";
            listaTercetos.get(numTercetoBackpatch - 1).addSecond(auxString);/* completo el segundo operando del BI*/
            $$ = $7; // Propagamos el ArrayList de la parte else
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
            // $1 (cuerpo_ejecutable_retorno) ya añadió sus tercetos a 'listaTercetos'.
            int numTercetoBackpatch = pila.peek();
            // Obtenemos el *último* terceto añadido a la lista global para el backpatch.
            Terceto ultimoTercetoDelBloque = listaTercetos.get(listaTercetos.size() - 1);
            // Usamos el número de ese último terceto
            Integer aux = ultimoTercetoDelBloque.getSoloNumTerceto() + 1 ;
            String auxString = "(" + String.valueOf(aux) + ")";
            listaTercetos.get(numTercetoBackpatch -1).addThird(auxString); /* completo el tercer operando del BF*/
            $$ = $1; /* Propagamos el valor de $1 (el ArrayList de sentencia_retorno) */
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
            System.out.println("Raaa: " + $2.obj + "Juan que carajo es esto? 19/11/25");
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
            ArrayList<ParserVal> listaParametros = (ArrayList<ParserVal>) $4.obj;
            String claveAcceso =$2.sval;
            Info funcInfo;
            if (getScope(ambito,claveAcceso)== null) {
                //creo info nuevo para que siga compialdo el codigo
                Logger.logError(cursor.getCurrentLine(), "Funcion "+$2.sval+ " no esta al alcance");
                funcInfo = new Info($2.sval, "ID", null, "Funcion", ambito);
                funcInfo.setListaParametrosFormales(new ArrayList<String>());
                ArrayList<String> lista = new ArrayList<String>();
                lista.add("TIPO_ERROR");
                funcInfo.setListaVariablesRetorno(lista);
            }else {
                claveAcceso = claveAcceso + "." + getScope(ambito, $2.sval);
                funcInfo = TablaSimbolos.TABLA_SIMBOLOS.get(claveAcceso);
            }
            ArrayList<String> listaParametrosFormales = funcInfo.getListaParametrosFormales();
            // Copia los valores de las expresiones de la llamada a las variables auxiliares
            for (ParserVal param : listaParametros) {
                Pair<ParserVal, ParserVal> p = (Pair<ParserVal, ParserVal>) param.obj;
                ParserVal expresion = p.getFirst();
                String nombreParametroLlamada = p.getSecond().sval; // Ej: "A"
                String nombreParametroCompleto = null;
                for(String pf : listaParametrosFormales){
                    // "A.TESTING.F2".startsWith("A.") -> TRUE
                    if(pf.startsWith(nombreParametroLlamada + ".")){
                        nombreParametroCompleto = pf;
                        break;
                    }
                }
                Info paramInfo;
                // Si es null, es que no lo encontramos
                if(nombreParametroCompleto == null){
                   Logger.logError(cursor.getCurrentLine(), "El parámetro '" + nombreParametroLlamada + "' no existe en la función " + $2.sval);
                   paramInfo = new Info(nombreParametroLlamada, "ID", "SIN TIPO", "PF NULL" , ambito,"(0)");
                } else {
                   paramInfo = TablaSimbolos.TABLA_SIMBOLOS.get(nombreParametroCompleto);
                }
                if (!paramInfo.getTipo().equals(getTipoParserVal(expresion))){
                    Logger.logError(cursor.getCurrentLine(), "Incompatibilidad de tipos en pasaje de parametros");
                }
                // Solo actuamos si es CV
                if (paramInfo != null && paramInfo.getUso() != null && paramInfo.getUso().contains("CV")) {
                    String varAux = paramInfo.getVarAux();
                    $$ = crearTerceto(new ParserVal(":="), new ParserVal(varAux), expresion);

                    listaTercetos.add((Terceto)$$.obj);
                    ((Terceto)$$.obj).addLine(cursor.getCurrentLine());
                    ((Terceto)$$.obj).addTipo(getTipoParserVal(expresion));
                }
            }
            // --- TERCETO CALL ---
            // Obtenemos la etiqueta de inicio de la función (ej: ETIQUETA1)
            String etiquetaFunc = funcInfo.getNroTercetoEtiqueta();
            // Creamos: [ CALL , etiquetaFunc , null ]
            ParserVal callTerceto = crearTerceto(new ParserVal("CALL"), new ParserVal(etiquetaFunc ), null);
            listaTercetos.add((Terceto)callTerceto.obj);
            ((Terceto)callTerceto.obj).addLine(cursor.getCurrentLine());
            // Copia los valores DE VUELTA (para CR)
            for (ParserVal param : listaParametros) {
                Pair<ParserVal, ParserVal> p = (Pair<ParserVal, ParserVal>) param.obj;
                ParserVal parametroReal = p.getFirst();
                String nombreParametroLlamada = p.getSecond().sval;
                String nombreParametroCompleto = null;
                for(String pf : listaParametrosFormales){
                    if(pf.startsWith(nombreParametroLlamada + ".")){
                        nombreParametroCompleto = pf;
                        break;
                    }
                }
                Info paramInfo;
                if(nombreParametroCompleto == null){
                   paramInfo = new Info(nombreParametroLlamada, "ID", null, "PF NULL" , ambito,"(0)");
                } else {
                   paramInfo = TablaSimbolos.TABLA_SIMBOLOS.get(nombreParametroCompleto);
                }
                // si es CR
                if (paramInfo != null && paramInfo.getUso() != null && paramInfo.getUso().contains("CR")) {
                    String varAux = paramInfo.getVarAux();
                    $$= crearTerceto(new ParserVal(":="), parametroReal, new ParserVal(varAux));

                    listaTercetos.add((Terceto)yyval.obj);
                    ((Terceto)yyval.obj).addLine(cursor.getCurrentLine());
                }
            }
            if (listaParametros.size() < listaParametrosFormales.size() ){
                Logger.logError(cursor.getCurrentLine(), "Falta de parametros en invocacion a funcion");

            }else if(listaParametros.size() > listaParametrosFormales.size()) {
                Logger.logError(cursor.getCurrentLine(), "Sobran parametros en invocacion a funcion");
            }
            ArrayList<String> listaNombresAux = funcInfo.getListaVariablesRetorno();
            ArrayList<ParserVal> listaParserVals = new ArrayList<>();
            for (String nombreAux : listaNombresAux) {
                ParserVal valRetorno = new ParserVal(nombreAux);
                valRetorno.ival=1;
                listaParserVals.add(valRetorno);
            }
            if (listaParserVals.size() == 1) {
                $$ = listaParserVals.get(0);

            } else {
                ParserVal contenedor = new ParserVal();
                contenedor.obj = listaParserVals;
                $$ = contenedor;
            }
        }
    |   invocacion_funcion_error
    ;

invocacion_funcion_error
    :   FUN  '(' lista_param_reales ')' {Logger.logError(cursor.getCurrentLine(), "Falta de nombre en funcion en invocacion de funcion");}
    ;

lista_param_reales
    :   lista_param_reales ',' parametro_real {
            // $1.obj ya es el ArrayList<ParserVal>
            // $3 es el ParserVal que viene de parametro_real
            ((ArrayList<ParserVal>)$1.obj).add($3);
            $$ = $1; // Pasa la lista modificada hacia arriba
        }
    |   parametro_real {
            // $1 es el ParserVal que viene de parametro_real
            ArrayList<ParserVal> lista = new ArrayList<>();
            lista.add($1);
            $$ = new ParserVal(lista); // Crea un nuevo ParserVal para contener la lista
        }
    ;

parametro_real
    :   expresion_aritmetica ARROW ID {
            $$.obj = new Pair<ParserVal,ParserVal> ($1,$3);
            //Hago esto para realizar de forma correcta la generacion de tercenos de invocacion, pasaje cv y cr
        }
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
static int contadorVariablesAuxTercetos = 0;
static int contadorParaBF = 0;
static final Double MAX_VALUE_POS = 3.40282347E38;
static final Double MIN_VALUE_POS = 1.17549435E-38;
static final Double MAX_VALUE_NEG = -1.17549435E-38;
static final Double MIN_VALUE_NEG = -3.404282347E38;

public static void main (String [] args) {
	System.out.println("Iniciando compilación..."); System.out.println(""); System.out.println(""); System.out.println("");
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
	String rutaTercetos = "data/tercetos.txt";
	String rutaASM = "data/programa.asm";
	Logger.exportarTercetos(rutaTercetos);
	if (Logger.hayErrores()){
		System.out.println("Se descarta generacion de codigo assembler debido a errores");
	}
	else{
		System.out.println("Generando código Assembler...");
		GeneradorAssembler generador = new GeneradorAssembler();
		generador.generarArchivoASM(rutaTercetos, rutaASM);
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
    if (s != null && s.equals("syntax error")) {
            Logger.logError(cursor.getCurrentLine(), "Error sintáctico: Token inesperado o estructura mal formada");
	} else {
	    // Si el error viene con un s personalizado (ej. lanzado manualmente)
	    Logger.logError(cursor.getCurrentLine(), s);
	}
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

public String getTipoParserVal(ParserVal val) {
    //si hay obj,es decir un TERCETO!!!, sval contiene el TIPO.
    if (val.obj != null) {
        return val.sval;
    }
    //si no hay obj, sval contiene el LEXEMA Y ACCEDIENDO A SU INFO EN TABLA DE SIMBOLO LO OBTENEMOS
    if (val.sval != null) {
        if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(val.sval)) {
            return TablaSimbolos.TABLA_SIMBOLOS.get(val.sval).getTipo();
        }
    }
    return "SIN TIPO";
}

public boolean checkTipo(ParserVal val1, ParserVal val2) {
    String tipo1 = getTipoParserVal(val1);
    String tipo2 = getTipoParserVal(val2);
    if (tipo1 == null || tipo2 == null || tipo1.equals("SIN TIPO") || tipo2.equals("SIN TIPO")) {
        return false;
    }
    return tipo1.equals(tipo2);
}

public String getScope(String ambito,String clave){
    while (!ambito.isEmpty()){
        if (TablaSimbolos.TABLA_SIMBOLOS.containsKey(clave+ "." + ambito)) {
            return ambito;
        }
        int pos = ambito.lastIndexOf('.');
        if (pos != -1) {
            ambito = ambito.substring(0, pos);
        } else {
            ambito = "";
        }
    }
    return null;
}

private void reducirAmbito(){
    String auxAmbito = ambito;
    int pos = auxAmbito.lastIndexOf('.');
    if (pos != -1) {
        auxAmbito = auxAmbito.substring(0, pos);
    }
    ambito = auxAmbito;
}

private void controlarEntero(String aux){
    try {
        short numero = Short.parseShort(aux);
    } catch (NumberFormatException e) {
        if(aux.contains("-"))
            Logger.logError(cursor.getCurrentLine(), "El número entero es demasiado pequeño");
        else
            Logger.logError(cursor.getCurrentLine(), "El número entero es demasiado grande");
    }
    if(!TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux))
        TablaSimbolos.TABLA_SIMBOLOS.put(aux,new Info("CTE_INT", "INT"));
}

private void controlarFlotante(String aux){
    boolean hayUnError = false; //no queremos que tire dos veces el mismo error. Si es demasiado pequeño o grande, no mostramos el de exceso de bits.
    if (aux.contains("F")){
        String[] parts = aux.split("F");
        float base = Float.parseFloat(parts[0]);
        int exponente = Integer.parseInt(parts[1]);
        BigDecimal result = BigDecimal.valueOf(base).multiply(BigDecimal.valueOf(Math.pow(10, exponente)));
        if ((result.signum() > 0 && result.compareTo(BigDecimal.valueOf(MIN_VALUE_POS)) < 0) ||
                (result.signum() < 0 && result.compareTo(BigDecimal.valueOf(MAX_VALUE_NEG)) > 0)) {
            Logger.logError(cursor.getCurrentLine(), "El número flotante es demasiado pequeño");
            hayUnError = true;
        }
        if ((result.signum() > 0 && result.compareTo(BigDecimal.valueOf(MAX_VALUE_POS)) > 0) ||
                (result.signum() < 0 && result.compareTo(BigDecimal.valueOf(MIN_VALUE_NEG)) < 0)) {
            Logger.logError(cursor.getCurrentLine(), "El número flotante es demasiado grande");
            hayUnError = true;
        }
        aux = aux.replace('F', 'E'); //Formateamos para meter en variable
    }
    try {
	float numero = Float.parseFloat(aux);
	if (Float.isInfinite(numero))
	    throw new NumberFormatException();
	aux = aux.replace('E', 'F'); //Formateamos para meter en variable
	if(!TablaSimbolos.TABLA_SIMBOLOS.containsKey(aux)){
	    TablaSimbolos.TABLA_SIMBOLOS.put(aux,new Info("CTE_FLOAT", "FLOAT"));
	}
    } catch (NumberFormatException e) {
        if (!hayUnError)
		    Logger.logError(cursor.getCurrentLine(), "Excede cantidad de bits");
    }
}