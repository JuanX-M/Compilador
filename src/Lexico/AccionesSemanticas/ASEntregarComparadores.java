package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

public class ASEntregarComparadores extends AccionSemantica{
    public ASEntregarComparadores(){
        super(8);
    }
    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        if (simbolo == '=' || simbolo == '!') {
            BUFFER.append(simbolo);
            String salida = BUFFER.toString();
            BUFFER.setLength(0);
            return new Pair<>(salida, ; //TODO: Corregir
        } else {
            String salida = BUFFER.toString();
            BUFFER.setLength(0);
            cursor.gobackCharacter();
            return new Pair<>(salida, -1); //TODO: Corregir
        }
    }
}
