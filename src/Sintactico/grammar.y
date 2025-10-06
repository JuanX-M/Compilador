%{
    import java.io.*;
    import Lexico.AnalizadorLexico;
%}

%token TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR FOR FROM TO CR SE LE TOI INT FLOAT ARROW

%right TWO_POINTS_ASSIGNATION
%left '+' '-'
%left '*' '/'
%right UMINUS

%start  prog

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

sentencia_declarativa   :   funcion
                        ;

sentencia_ejecucion     :   VAR ID TWO_POINTS_ASSIGNATION expresion_aritmetica
                        |   IF '(' condicion ')' '{' cuerpo '}' ELSE '{' cuerpo '}' ENDIF
                        |   IF '(' condicion ')' '{' cuerpo '}' ENDIF
                        |   PRINT '(' STRING ')'
                        |   PRINT '(' expresion_aritmetica ')'
                        |   FOR '(' ID FROM CTE_INT TO CTE_INT ')' '{' cuerpo '}'
                        |   lista_variables '='
                        ;

sentencia_lambda        :    '(' tipo ID ')' '{' cuerpo '}' '(' ID ')'
                        |    '(' tipo ID ')' '{' cuerpo '}' '(' CTE_INT ')'
                        |    '(' tipo ID ')' '{' cuerpo '}' '(' CTE_FLOAT ')'
                        ;


funcion                 : lista_tipos ID '(' parametros_formales ')' '{' cuerpo '}' RETURN '(' lista_exp_aritmeticas ')' ';'
                        ;


expresion_aritmetica    :   expresion_aritmetica '+' operando
                        |   expresion_aritmetica '-' operando
                        |   expresion_aritmetica '*' operando
                        |   expresion_aritmetica '/' operando
                        |   '-' expresion_aritmetica %prec UMINUS
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

lista_variables         :   lista_variables ',' ID
                        |   ID
                        ;

lista_exp_aritmeticas   :   lista_exp_aritmeticas ',' expresion_aritmetica
                        |   expresion_aritmetica
                        ;

tipo                    :   INT
                        |   FLOAT
                        |   STRING
                        ;

parametros_formales     :   parametros_formales ',' parametro_formal
                        |   parametro_formal
                        ;
parametro_formal        :   semantica_pasaje tipo ID
                        |   tipo ID
                        ;

lista_tipos             :   lista_tipos ',' tipo
                        |   tipo
                        ;

operando                :   ID
                        |   CTE_INT
                        |   CTE_FLOAT
                        |   ID '(' parametros_reales ')'
                        |   ID'.'ID
                        ;

semantica_pasaje        :   CR SE
                        |   CR LE
                        ;

parametros_reales       :   parametros_reales ',' parametro_real
                        |   parametro_real
                        ;

parametro_real          :   operando ARROW parametro_formal
                        ;
