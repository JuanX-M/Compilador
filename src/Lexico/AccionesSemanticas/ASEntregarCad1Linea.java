package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Logger;
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
                throw new IllegalArgumentException();
        }
        catch (IllegalArgumentException i){
            Logger.logError(cursor.getCurrentLine(), "Error: Cadena contiene salto de linea");
            return null;
        }
        return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get("STRING"));

    }
}
