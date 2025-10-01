package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Cursor;
import Tools.Pair;

public class ASEntregarComparadores extends AccionSemantica{
    public ASEntregarComparadores(){
        super(8);
    }
    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        if ((simbolo == '=') || (simbolo == '!')) {
            BUFFER.append(simbolo);
            String salida = BUFFER.toString();
            BUFFER.setLength(0);
            switch (salida) {
                case ">=":
                    return new Pair<>(salida, Parser.GREATER_OR_EQUAL);
                case "<=":
                    return new Pair<>(salida, Parser.LESS_OR_EQUAL);
                case "==":
                    return new Pair<>(salida, Parser.EQUAL);
                case "=!":
                    return new Pair<>(salida, Parser.NOT_EQUAL);
                default:
                    return null;
            }
        } else {
            String salida = BUFFER.toString();
            BUFFER.setLength(0);
            cursor.gobackCharacter();
            switch (salida) {
                case ">":
                    return new Pair<>(salida, (int) '>');
                case "<":
                    return new Pair<>(salida, (int) '<');
                default:
                    return null;
            }
        }
    }
}
