package Lexico.AccionesSemanticas;

import Tools.Pair;
import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarFlotante extends AccionSemantica{

    public ASEntregarFlotante() {
        super(4);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {
    String aux = BUFFER.toString();
    BUFFER.setLength(0);
    cursor.gobackCharacter();
    return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get("CTE_FLOAT"));
    }
}
