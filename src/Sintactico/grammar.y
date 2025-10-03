%{



%}

% token
TWO_POINTS_ASSIGNATION STRING GREATER_OR_EQUAL LESS_OR_EQUAL EQUAL
NOT_EQUAL CTE_INT CTE_FLOAT ID IF ELSE ENDIF PRINT RETURN VAR
FOR FROM TO CR SE LE TOI INT FLOAT ARROW
%%


prog                : ID '{' cuerpo '}'
                    ;

cuerpo              : cuerpo sentencia
                    | sentencia
                    ;

sentencia           : sentencia_declarativa
                    | sentencia_ejecucion
                    | sentencia_control
                    ;

sentencia_declarativa  : sentencia_declarativa funcion
                       | funcion
                       ;

sentencia_funcion

















sentencia_funcion   : tipo ID '(' parametros_formmales')'
                        '{' cuerpo '}'
                    ;
tipo                  : INT
                    | FLOAT
                    ;
