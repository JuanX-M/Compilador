# Compilador

### Cosas que hay que hacer andar:

* chequear los addtipos que se hagan bien//JuanX
* casos de uso TP4
* Arreglar problema con los flotantes 
* Realizar chequeo de constantes negativas
* chequear que el compilador no se rompa al tener errores de sintaxis



### Cosas que andan:
* Chequeos en tiempo de ejecucion
    * Division por cero para Int y Float
    * Overflow en productos de datos de Float
    * Perdida de informacion en conversion de Float a Int
* chequeo de tipo/alcance en argumento lambda con ID, hay que agregar a la gramatica ID'.'ID en argumento lambda y que su Uso no sea CR SE
* agregar sentencias lambdas a casos de pruebas con error
* Descartar generacion de codigo assembler cuando hay syntax error
* chequear que el compilador no se rompa al tener errores de sintaxis
* '-' en expresion aritmetica cuando esta solo o con espacio
* revisar sentencias if/else e iteraciones con retorno una vez finalizado cheques tipos en funcion ANDAAAAA NASHEEEEEEEEEEEEEEEEEEEEEE
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
* Semantica de pasaje de paremtros CR SE LE,CV
* * Casos de prubea que deben tirar error //JuanX
* * Meter toi en clase GeneradorAssembler para que haga la conversion, es cuando el el operador del terceto es 'toi' hace ahi la conversion

* TOI
  * Que no sea de tipo entero (Dar warning)
### Haciendo:
reducir ambito JUANX

### Aclaraciones de diseño:
* No se puede hacer TOI + TOI
* Seguimos logica de asignaciones multiples por sobre mas retornos que tipos de funcion
  * Si en l lado derecho de una asignacion multiple HAY ALGUNA FUNCION MEZCLADA CON ESPRESIONES ARITMETICAS, DESCARTAMOS TODO LO SOBRANTE
  * Si solo hay expresiones aritmeticas y sobran del lado derecho, tiramos error
* Por como tenemos hecho las acciones semanticas el limite de int y float no es 100% preciso
* Al realizar las sentencias for, el iterador del mismo es inicializado solo en la misma sentencia (igual que java)

### Testeando



### Cosas








* Chequeos en tiempo de ejecucion
  * Division por cero para Int y Float
  * Overflow en productos de datos de Float
  * Perdida de informacion en conversion de Float a Int
* Prohibir operaciones entre operandos de tipos diferentes(Informar cual es la combinacion de tipos que causa la incompatibilidad)

### Casos de prueba que faltan:


* Parametro formal no existe
* Otros que ahora no se me ocurren
* Sentencias lambda? Nico genio del lambda, hacete estos casos <3.

#### Hechos
* 



#### Por hacer

