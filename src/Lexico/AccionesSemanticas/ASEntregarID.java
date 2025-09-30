package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Cursor;
import Tools.Info;
import Tools.Pair;
import static Tools.TablaSimbolos.TABLA_SIMBOLOS;

public class ASEntregarID extends AccionSemantica {

    public ASEntregarID() {
        super(5);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        if(BUFFER.length() > 20){
            BUFFER.setLength(20);
            System.out.println("WARNING: identificador truncado"); //TODO: Logger
        }
        String aux = BUFFER.toString();
        if(TABLA_SIMBOLOS.containsKey(aux)){
            return new Pair<String, Integer>(aux, Parser.id);
            // Se agregar a la tabla de simbolos lexema y su token correspondiente
        }
        TABLA_SIMBOLOS.put(aux,new Info(aux,Parser.id)); //añadir identificador a la tabla
        cursor.gobackCharacter();
        BUFFER.setLength(0); //vacio el buffer
        return new Pair<String, Integer>(aux,Parser.id);


    }

}
