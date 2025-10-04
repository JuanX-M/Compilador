package Lexico.AccionesSemanticas;

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
            int numero = Integer.parseInt(aux);
        } catch (NumberFormatException e) {
            System.out.println("Excede la cantidad de bits");
            BUFFER.setLength(0);

            // si no esta BUFFER.setLength(0)  hay error para todos los enteros porque se van concatenando
            // en el mismo buffer
            return null;
        }
        BUFFER.append(simbolo);
        if(TABLA_SIMBOLOS.containsKey(aux)) {
            return new Pair<String,Integer>(aux,TABLA_PALABRAS_RESERVADAS.get("CTE_INT"));
        }
        TABLA_SIMBOLOS.put(aux,null);
        BUFFER.setLength(0);
        return new Pair<>(aux,TABLA_PALABRAS_RESERVADAS.get("CTE_INT"));
    }
}
