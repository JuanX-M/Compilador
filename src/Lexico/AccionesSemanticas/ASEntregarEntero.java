package Lexico.AccionesSemanticas;

import Tools.Info;
import Tools.Logger;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;
import static Tools.TablaSimbolos.TABLA_SIMBOLOS;

public class ASEntregarEntero extends AccionSemantica {

    public ASEntregarEntero(){
        super(11);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {
        String aux = BUFFER.toString();
        //System.out.println(aux);
        try {
            short numero = Short.parseShort(aux);
        } catch (NumberFormatException e) {
            if(aux.contains("-"))
                Logger.logError(cursor.getCurrentLine(), "El número entero es demasiado pequeño");
            else
                Logger.logError(cursor.getCurrentLine(), "El número entero es demasiado grande");
            BUFFER.setLength(0);
            // si no esta BUFFER.setLength(0)  hay error para todos los enteros porque se van concatenando
            // en el mismo buffer
            return new Pair<>(null,-1);
        }
        System.out.println("Dentro de ASEntregarEntero, El simbolo es  " + simbolo);
        BUFFER.append(simbolo);
        if(TABLA_SIMBOLOS.containsKey(aux)) {
            return new Pair<String,Integer>(aux,TABLA_PALABRAS_RESERVADAS.get("CTE_INT"));
        }
        TABLA_SIMBOLOS.put(aux,new Info(aux,TABLA_PALABRAS_RESERVADAS.get("CTE_INT")));
        BUFFER.setLength(0);
        return new Pair<>(aux,TABLA_PALABRAS_RESERVADAS.get("CTE_INT"));
    }
}
