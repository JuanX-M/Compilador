package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;
import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarID extends AccionSemantica {

    public ASEntregarID() {
        super(5);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        if (BUFFER.length() > 20) {
            BUFFER.setLength(20);
            Logger.logWarning(cursor.getCurrentLine(), "Identificador " + BUFFER + " truncado");
        }
        String aux = BUFFER.toString();
        cursor.gobackCharacter();
        BUFFER.setLength(0);
        return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get("id"));
    }
}
