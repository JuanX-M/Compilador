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
        String aux = BUFFER.toString();
        if (aux.contains("-") && simbolo == '>') {
            BUFFER.setLength(0);
            return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get("->"));
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
                    if (aux.contains("-")) {
                        cursor.gobackCharacter();
                        return new Pair<>("-", (int) '-');
                    }
                    if (aux.equals(".")) {
                        cursor.gobackCharacter(); // Devolvemos la 'L' para que sea procesada después
                        return new Pair<>(".", (int) '.'); // Entregamos el punto
                    }
                    return null;
            }
        }
    }
}