package Lexico.AccionesSemanticas;

import Tools.Pair;

import static Tools.TablaSimbolos.tablaSimbolos;

public class ASEntregarEntero extends AccionSemantica {

    private String nombre ="AS3";

    public String getNombre() {
        return nombre;
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {
        String aux = buffer.toString();
        try {
            int numero = Integer.parseInt(aux);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        buffer.append(simbolo);
        if(tablaSimbolos.containsKey(aux)) {
            return new Pair<String,Integer>(aux,null); //TODO: corregir
        }
        tablaSimbolos.put(aux,null);
        buffer.setLength(0);
        return new Pair<>(aux,null); // TODO: retornar TOKEN
    }

}
