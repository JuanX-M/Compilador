# Compilador

### Cosas que hay que hacer andar:



* '-' en expresion aritmetica cuando esta solo o con espacio
* chequear que el compilador no se rompa al tener errores de sintaxis
* Meter toi en clase GeneradorAssembler para que haga la conversion, es cuando el el operador del terceto es 'toi' hace ahi la conversion
* Hacer chequeos en tiempo en ejecucion 
* Chequeos en tiempo de ejecucion
  * Division por cero para Int y Float
  * Overflow en productos de datos de Float
  * Perdida de informacion en conversion de Float a Int


### Cosas que andan:
* revisar sentencias if/else e iteraciones con retorno una vez finalizado cheques tipos en funcion
* * chequear en asignacion multiple que haya como minimo 2 variables y 2 exp aritmeticas
* Falta de parametro formal al hacer -> en llamada funcion
* Cuando se detecte una invocacion, se debera generar codigo para la misma, chequeando que el tipo de los
  parametros reales sean compatibles con el tipo de los parametros formales correspondientes, o incluyan la
  conversion correspondiente.
* Chequeo de parametro formal SE,LE
* Chequeo retornos
  * En asignacion unaria al invocar funcion y que esta devuelve varios, tirar error semantico
* Asignacion multiple con funciones anda raro X, Y, Z, W = fun F1(Z->C), fun F1(Z->C), fun F3(Z->C); //f1 tiene 2 retornos y f3 tiene 1
* Redeclaracion de variables en diferentes ambitos
* Chequeo de los tipos en parametro:
  * Copia resultado
  * Solo escritura
  * Etc
* Cantidad de retornos para una asignacion multiple
* Solucionar '.' en el lexico para que tome ID cuando viene una letra mayuscula y no salte error de falta digito
* No permitir asignacion de variable con ambito especificado en el mismo ambito 
* Chequear sentencias if/else iteracion con return dentro de funcion

* Chequeo de tipos ( no se puede hcaer hasta definir como detectar una funcion con retorno multiples)
  * Parametros formales
  * Parametros reales
    * Asociacion con tipo parametro formal
* Retorno
  * Mismo tipo de funcion
  * Mismo tipo que la variable donde se asigna
* Chequeto de ambito
  * Chequeo de variable
  * Chequeo de Funcion
  * Declaracion de funcion
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

* Chequeo de tipos ( no se puede hcaer hasta definir como detectar una funcion con retorno multiples) JuanX
  * Parametros formales
  * Parametros reales
    * Asociacion con tipo parametro formal
* Chequeo de SE y LE de parametro formal
### Haciendo:

* * Revisar si funcionana los errores semanticos if/else e iteraciones  retorno dentro de una funcin, creo que no andan porque
    estan comentados o no tienen reglas directamente, es copiar y pegar las reglas con errores y poner _retorno//Juanx,lo hago a la mañama,puede que me lleve todo el dia
### Aclaraciones de diseño:
Seguimos logica de asignaciones multiples por sobre mas retornos que tipos de funcion

Si en l lado derecho de una asignacion multiple HAY ALGUNA FUNCION MEZCLADA CON ESPRESIONES ARITMETICAS, DESCARTAMOS TODO LO SOBRANTE
Si solo hay expresiones aritmeticas y sobran del lado derecho, tiramos error 

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

