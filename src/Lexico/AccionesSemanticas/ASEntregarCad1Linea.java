package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Cursor;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarCad1Linea extends AccionSemantica{
    public ASEntregarCad1Linea(){
        super(7);
    }
    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.append(simbolo);
        String aux = BUFFER.toString();
        BUFFER.setLength(0);
        try {
            if (aux.contains("\r") || aux.contains("\n"))
                throw new IllegalArgumentException("Cadena contiene salto de linea");
        }
        catch (IllegalArgumentException i){
            System.out.println(i.getMessage());
            return null;
        }
        return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get("STRING"));

    }
}
