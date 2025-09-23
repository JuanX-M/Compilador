package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

public class ASEntregarLiterales extends AccionSemantica{
    public ASEntregarLiterales() {
        super(6);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        switch (simbolo) {
            case '+' : return new Pair<>(null, -1); //TODO: Corregir
            case '/' : return new Pair<>(null, -1);
            case '(' : return new Pair<>(null, -1);
            case ')' : return new Pair<>(null, -1);
            case ':' : return new Pair<>(null, -1);
            case ';' : return new Pair<>(null, -1);
            case ',' : return new Pair<>(null, -1);
            case '{' : return new Pair<>(null, -1);
            case '}' : return new Pair<>(null, -1);
            default : return new Pair<>(null, -1);
        }
    }



}
