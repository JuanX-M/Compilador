package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

public class ASEntregarLiterales extends AccionSemantica{
    public ASEntregarLiterales() {
        super(6);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.setLength(0);
        switch (simbolo) {
            // int 'literal' tranformas a codigo ASCII
            case '+' : return new Pair<>("+", (int) '+');
            case '/' : return new Pair<>("/", (int) '/');
            case '(' : return new Pair<>("(", (int) '(');
            case ')' : return new Pair<>(")", (int) ')');
            case ':' : return new Pair<>(":", (int) ':');
            case ';' : return new Pair<>(";", (int) ';');
            case ',' : return new Pair<>(",", (int) ',');
            case '{' : return new Pair<>("{", (int) '{');
            case '}' : return new Pair<>("}", (int) '}');
            case '*' : return new Pair<>("*", (int) '*');
            default : return null;
        }
    }



}
