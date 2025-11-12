package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarPalabras extends AccionSemantica {

    public ASEntregarPalabras() {
        super(12);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        String aux = BUFFER.toString();
        cursor.gobackCharacter();
        BUFFER.setLength(0);
        try {
            if (TABLA_PALABRAS_RESERVADAS.containsKey(aux))
                return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get(aux));
            else
                throw new IllegalAccessException("Palabra reservada " + aux + " no existe ni se encontro una semenjante");
        } catch (IllegalAccessException e) {
            Logger.logError(cursor.getCurrentLine(), e.getMessage());
        }
        return null;
    }
}