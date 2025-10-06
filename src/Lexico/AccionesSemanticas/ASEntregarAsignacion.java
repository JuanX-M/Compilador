package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarAsignacion extends AccionSemantica{
    public ASEntregarAsignacion(){
        super(11);
    }
    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        if (simbolo == '=') {
            BUFFER.append(simbolo);
            String salida = BUFFER.toString();
            BUFFER.setLength(0);

            return new Pair<>(salida, TABLA_PALABRAS_RESERVADAS.get(salida));
        } else {
            String salida = BUFFER.toString();
            BUFFER.setLength(0);
            cursor.gobackCharacter();
            return new Pair<>(salida, (int) '=');
        }
    }
}
