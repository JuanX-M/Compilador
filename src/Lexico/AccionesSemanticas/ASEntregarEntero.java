package Lexico.AccionesSemanticas;

import Tools.Pair;
import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarEntero extends AccionSemantica {

    public ASEntregarEntero(){
        super(11);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {
        String aux = BUFFER.toString();
        BUFFER.append(simbolo);
        BUFFER.setLength(0);
        return new Pair<>(aux,TABLA_PALABRAS_RESERVADAS.get("CTE_INT"));
    }
}
