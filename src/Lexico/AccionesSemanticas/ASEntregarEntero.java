package Lexico.AccionesSemanticas;

import Tools.Pair;
import Sintactico.Parser;

import static Tools.TablaSimbolos.TABLA_SIMBOLOS;

public class ASEntregarEntero extends AccionSemantica {

    public ASEntregarEntero(){
        super(11);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {
        String aux = BUFFER.toString();
        try {
            int numero = Integer.parseInt(aux);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        BUFFER.append(simbolo);
        if(TABLA_SIMBOLOS.containsKey(aux)) {
            return new Pair<String,Integer>(aux,Parser.CTE_INT);
        }
        TABLA_SIMBOLOS.put(aux,null);
        BUFFER.setLength(0);
        return new Pair<>(aux,Parser.CTE_INT);
    }
}
