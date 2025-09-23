package Lexico.AccionesSemanticas;

import Tools.Cursor;
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
            System.out.println("WARNING: identificador truncado"); //TODO: hay que guardarlo en txt
        }
        String aux = BUFFER.toString();
        if(TABLA_SIMBOLOS.containsKey(aux)){
            return new Pair<String, Integer>(aux,null); // TODO: corregir
        }
        TABLA_SIMBOLOS.put(aux,null); //añadir identificador a la tabla
        cursor.gobackCharacter();
        BUFFER.setLength(0); //vacio el buffer
        return new Pair<String, Integer>(aux,null); //TODO: corregir


    }

}
