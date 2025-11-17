# Compilador

### Cosas que hay que hacer andar:
* Cantidad de retornos para una asignacion multiple
* Redeclaracion de variables en diferentes ambitos
* Chequeo de ambito
  * Chequeo de funcion
  * Chequeo de variable
* Chequeo de tipo en codigo intermedio
* Conversiones Explicitas de Float a Int
* Cuando se detecte una invocacion, se debera generar codigo para la misma, chequeando que el tipo de los
    parametros reales sean compatibles con el tipo de los parametros formales correspondientes, o incluyan la
    conversion correspondiente.
* For
  * Falta de parametro en el for



### Cosas que andan:
* Variable sin declarar
* Redeclaracion de variables
* Funcion sin declarar
* Redeclaracion de funcion
* Los tercetos devuelven:
  * Tipo devuelto
  * Tipo y nombre de parametros
  * Semantica de pasaje de parametros
* Inferencia obligatoria
* Asignacion con igual numero de elementos de ambos lados de la asignacion
* Permitir mas retornos que el numero de lados izquierdos
* Chequeo de los tipos en parametro:
  * Copia resultado
  * Solo escritura
  * Etc

### Aclaraciones de diseño:




### Cosas







* Lambda
* Chequeos en tiempo de ejecucion
  * Division por cero para Int y Float
  * Overflow en productos de datos de Float
  * Perdida de informacion en conversion de Float a Int
* Prohibir operaciones entre operandos de tipos diferentes(Informar cual es la combinacion de tipos que causa la incompatibilidad)

### Casos de prueba:

#### Hechos
* 


#### Por hacer