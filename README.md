# Compilador

###### Para ejecutar, debe ingresar el .txt con el codigo y todos los archivos de data adonde este ubicado
###### Luego, para ejecutar el comando make.bat, si debe ubicarse dentro de data


## Falta


## Hecho

* Está bien manejado, pero a los parámetros cr les mueve -1 al entrar a la función. Debería moverles 0, si se requiere un valor por defecto
    * En Seccuones de grmatica.y se modifico inicialiazacion de -1/-1.0 a 0/0.0 en la creacion de tercetos de varriables auxiliares de parametros formales cr 
        y inicialiazacion de parametros formales con valor por defecto de -1/-1.0 a 0/0.0
*  Se deberían insertar saltos de línea entre terceto y terceto. Se pueden leer en el log, pero no en la lista de tercetos.
    * Se agrego un for en el system.out en main del grammar.y
* Muy difícil de revisar. Se deberían insertar saltos de línea entre símbolo y símbolo
    * Se creo metodo printTabla en clase TablaSimbolos para imprimir la tabla de simbolos
* En los include se debería colocar   include \masm32\...  , no una referencia a una posición  específica en una unidad determinada (include C:\ masm32\...)
    * Se saco "C:\" de los includes en clase GeneradorAssembler
* Solucion de instrucciones del assembler

