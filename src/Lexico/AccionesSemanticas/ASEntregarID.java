package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;
import static Tools.TablaSimbolos.tablaSimbolos;

public class ASEntregarID extends AccionSemantica {

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        if(buffer.length() > 20){
            buffer.setLength(20);
            System.out.println("WARNING: identificador truncado"); //TODO: hay que guardarlo en txt
        }
        String aux = buffer.toString();
        if(tablaSimbolos.containsKey(aux)){
            return new Pair<String, Integer>(aux,null); // TODO: corregir
        }
        tablaSimbolos.put(aux,null);
        buffer.setLength(0); //vacio el buffer
        buffer.append(simbolo); //guardo el ultimo caracter para el lexema siguiente
        return new Pair<String, Integer>(aux,null); //TODO: corregir


    }

}
