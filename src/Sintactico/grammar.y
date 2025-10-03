%{



%}


%token TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR FOR FROM TO CR SE LE TOI INT FLOAT ARROW

%start  prog
%left '+' '-'
%left '*' '/'
%%


prog                    :   ID '{' cuerpo '}'
                        ;

cuerpo                  :   cuerpo sentencia
                        |   sentencia
                        ;

sentencia               :   sentencia_declarativa
                        |   sentencia_ejecucion ';'
                        |   sentencia_lambda
                        ;

sentencia_declarativa   :   sentencia_declarativa funcion
                        |   funcion
                        ;
sentencia_lambda        :    '(' tipo ID ')' '{' cuerpo '}' '(' ID ')'
                        |    '(' tipo ID ')' '{' cuerpo '}' '(' CTE_INT ')'
                        |    '(' tipo ID ')' '{' cuerpo '}' '(' CTE_FLOAT ')'

sentencia_ejecucion     :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica
                        |   IF '(' condicion ')' '{' cuerpo '}' ELSE '{' cuerpo '}' ENDIF
                        |   IF '(' condicion ')' '{' cuerpo '}' ENDIF
                        |   PRINT '(' STRING ')'
                        |   PRINT '(' expresion_aritmetica ')'
                        |   FOR '(' ID FROM CTE_INT TO CTE_INT ')' '{' cuerpo '}'
                        |   lista_variables = lista_operandos
                        ;

funcion                 :   tipo ID '(' parametros_formales ')' '{' cuerpo '}' RETURN '(' expresion_aritmetica ')' ';'
                        |   lista_tipos ID '(' parametros_formales ')' '{' cuerpo '}' RETURN '(' lista_operandos ')' ';'
                        ;

parametros_formales     :   parametros_formales ',' semantica_pasaje tipo ID
                        |   semantica_pasaje tipo ID
                        |   tipo ID
                        ;

parametros_reales       :   operando ARROW parametros_formales
                        ;

semantica_pasaje        :   CR SE
                        |   CR LE
                        ;

expresion_aritmetica    :   expresion_aritmetica '+' operando
                        |   expresion_aritmetica '-' operando
                        |   expresion_aritmetica '*' operando
                        |   expresion_aritmetica '/' operando
                        |   '-' expresion_aritmetica %prec '*'
                        |   TOI '(' expresion_aritmetica ')'
                        |   operando
                        ;

condicion               :   expresion_aritmetica GREATER_OR_EQUAL expresion_aritmetica
                        |   expresion_aritmetica LESS_OR_EQUAL expresion_aritmetica
                        |   expresion_aritmetica EQUAL expresion_aritmetica
                        |   expresion_aritmetica NOT_EQUAL expresion_aritmetica
                        |   expresion_aritmetica '>' expresion_aritmetica
                        |   expresion_aritmetica '<' expresion_aritmetica
                        ;

lista_operandos         :   lista_variables ',' expresion_aritmetica
                        |   expresion_aritmetica
                        ;

operando                :   ID
                        |   CTE_INT
                        |   CTE_FLOAT
                        |   ID '(' parametros_reales ')'
                        |   ID'.'ID
                        ;

lista_tipos             :   lista_tipos , tipo
                        |   tipo
                        ;

tipo                    :   INT
                        |   FLOAT
                        |   STRING
                        ;

