# Compilador

### Cosas que hay que hacer andar:

* Chequeo de tipos ( no se puede hcaer hasta definir como detectar una funcion con retorno multiples)
  * Parametros formales
  * Parametros reales
    * Asociacion con tipo parametro formal
* Retorno
  * Mismo tipo de funcion
  * Mismo tipo que la variable donde se asigna
* Cantidad de retornos para una asignacion multiple
* Redeclaracion de variables en diferentes ambitos

* chequear en asignacion multiple que haya como minimo 2 variables y 2 exp aritmetcas
* Cuando se detecte una invocacion, se debera generar codigo para la misma, chequeando que el tipo de los
    parametros reales sean compatibles con el tipo de los parametros formales correspondientes, o incluyan la
    conversion correspondiente.
* Chequeo retornos
  * En asignacion unaria al invocar funcion y que esta devuelve varios, tirar error semantico
  * En lado derecho de una asignacion multiple, que tiene varias funcion que retornan muchas cosas, 
    * concatenar cada arraylist de cada funcion con todo lo que esta del lado derecho
    * hacer un for que itere con arraylist de variables de lado izq e ir asignando lo del lado derecho, al salir del for descartar resto de variables del lado derecho e informar con warning
* Chequeo de parametro formal SE,LE
* Permitir mas retornos que el numero de lados izquierdos
* Chequeo de los tipos en parametro:
  * Copia resultado
  * Solo escritura
  * Etc
* revisar sentencias if/else e iteraciones con retorno una vez finalizado cheques tipos en funcion
* '-' en expresion aritmetica cuando esta solo o con espacio


### Cosas que andan:
* Solucionar '.' en el lexico para que tome ID cuando viene una letra mayuscula y no salte error de falta digito
* No permitir asignacion de variable con ambito especificado en el mismo ambito //Juanx
* Chequear sentencias if/else iteracion con return dentro de funcion

* Chequeto de ambito
  * Chequeo de variable
  * Chequeo de Funcion
* En asignacion multiple chequear si cant lado izq = cant lado derecho, por posicion, chequear tipos de forma individual
* Condicion if
* Misma cantidad de elementos en asignacion multiple
* Chequeo de tipos
  * Asignacion unaria
  * Expresiones aritmeticas
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
* Chequeo de tipos
  * Asignacion multiple
  * Lambda
* For
* Todo de lambda


### Haciendo:
* Chequeo de tipos ( no se puede hcaer hasta definir como detectar una funcion con retorno multiples) JuanX
  * Parametros formales
  * Parametros reales
    * Asociacion con tipo parametro formal

* Chequeo de ambito
    * Declaracion de funcion
### Aclaraciones de diseño:

### Testeando


* TOI
  * Que no sea de tipo entero (Dar warning)


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

