package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarLiterales extends AccionSemantica {
    public ASEntregarLiterales() {
        super(6);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {

        String aux = BUFFER.append(simbolo).toString();
        if (aux.contains("->") && aux.length() == 2) {

            BUFFER.setLength(0);
            return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get(aux));
        } else {
            BUFFER.setLength(0);
            switch (simbolo) {
                // int 'literal' tranformas a codigo ASCII
                case '+':
                    return new Pair<>("+", (int) '+');
                case '/':
                    return new Pair<>("/", (int) '/');
                case '(':
                    return new Pair<>("(", (int) '(');
                case ')':
                    return new Pair<>(")", (int) ')');
                case ':':
                    return new Pair<>(":", (int) ':');
                case ';':
                    return new Pair<>(";", (int) ';');
                case ',':
                    return new Pair<>(",", (int) ',');
                case '{':
                    return new Pair<>("{", (int) '{');
                case '}':
                    return new Pair<>("}", (int) '}');
                case '*':
                    return new Pair<>("*", (int) '*');
                default:
                    return null;
            }


        }
    }
}