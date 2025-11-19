# Compilador

### Cosas que hay que hacer andar:
* Cantidad de retornos para una asignacion multiple
* Redeclaracion de variables en diferentes ambitos
* Chequeo de ambito
  * Chequeo de funcion
  * Chequeo de variable
* Chequeo de tipos
  * Asignacion multiple
  * Parametros formales
  * Parametros reales
    * Asociacion con tipo parametro formal
  * Declaracion de funcion
  * Lambda
  * Retorno
    * Mismo tipo de funcion
    * Mismo tipo que la variable donde se asigna 
  * Condicion if
  * Expresiones aritmeticas
  * TOI
    * Que no sea de tipo entero (Dar warning)
    * 
  
* Conversiones Explicitas de Float a Int
* Cuando se detecte una invocacion, se debera generar codigo para la misma, chequeando que el tipo de los
    parametros reales sean compatibles con el tipo de los parametros formales correspondientes, o incluyan la
    conversion correspondiente.
* For
  * Falta de parametro en el for
* Chequeo retornos
  * En asignacion unaria al invocar funcion y que esta devuelve varios, tirar error semantico
  * En asignacion multiple chequear si cant lado izq = cant lado derecho, por posicion, chequear tipos de forma individual
  * En lado derecho de una asignacion multiple, que tiene varias funcion que retornan muchas cosas, 
    * concatenar cada arralyst de cada funcion con todo lo que esta del lado derecho
    * hacer un for que itere con arralist de variables de lado izq e ir asignando lo del lado derecho, al salir del for descartar resto de variables del lado derecho e informar con warning
* Chequeo de parametro formal SE,LE
* Lambda


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








* Chequeos en tiempo de ejecucion
  * Division por cero para Int y Float
  * Overflow en productos de datos de Float
  * Perdida de informacion en conversion de Float a Int
* Prohibir operaciones entre operandos de tipos diferentes(Informar cual es la combinacion de tipos que causa la incompatibilidad)

### Casos de prueba:

#### Hechos
* 


#### Por hacer

hacer set de parametros formales en gramtica a info de la funcion