package Lexico;
import Tools.Pair;
import Lexico.AccionesSemanticas.AccionSemantica;

import java.util.ArrayList;

public final class MatrizTransicion {

    private final Pair<Integer, AccionSemantica>[][] MATRIZ = new Pair[18][28];

    public MatrizTransicion() {
    }

    public Integer convertir(char c) {
        switch (c) {

            case ' ':
                return 0;
            case '\t':
                return 1;
            case 'a','b', 'c','d','e', 'f', 'g', 'h','i','j', 'k', 'm', 'n','l','ñ','o', 'p', 'q', 'r', 's','t','u','v', 'w', 'x',
                 'y', 'z':
                return 3;
            case 'A','B','C','D','E','G','H','J','K','L', 'M', 'N','O','P', 'Q', 'R', 'S',
                 'T', 'U', 'V', 'W', 'X', 'Y', 'Z':
                return 4;
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
                return 5;
            case 'I':
                return 6;
            case 'F':
                return 7;
            case '%':
                return 8;
            case '"':
                return 9;
            case '#':
                return 10;
            case '+':
                return 11;
            case '-':
                return 12;
            case '*':
                return 13;
            case '/':
                return 14;
            case '=':
                return 15;
            case ':':
                return 16;
            case '>':
                return 17;
            case '<':
                return 18;
            case '!':
                return 19;
            case '(':
                return 20;
            case ')':
                return 21;
            case '{':
                return 22;
            case '}':
                return 23;
            case '_':
                return 24;
            case ';':
                return 25;
            case ',':
                return 26;
            case '.':
                return 27;
            default:
                char aux1 = '\n';
                char aux2 = '\r';
                if(c == aux1 || c == aux2)
                    return 2;
                return null;
        }
    }

    public void addTransicion(int estado, int simbolo, int newEstado, AccionSemantica a) {
        MATRIZ[estado][simbolo] = new Pair<>(newEstado, a);
    }

    public boolean isEstadoFinal(int estado) {
        return (estado == 17); //si el estado es 17, es el estado final
    }

    public int getEstado(int estado, char simbolo) {
        int aux = convertir(simbolo);
        Pair<Integer, AccionSemantica> cell = MATRIZ[estado][aux];
        if (cell == null) {
            return -1; // estado inválido
        }
        return cell.getFirst();
    }

    public AccionSemantica getAccionSemantica(int estado, char simbolo) {
        //System.out.println("Estado: " + estado + ", Simbolo: '" + simbolo + "'");
        //System.out.println("Columna convertida: " + aux);
        int aux = convertir(simbolo);
        Pair<Integer, AccionSemantica> cell = MATRIZ[estado][aux];
        return cell.getSecond();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lista de Transiciones Definidas:\n");
        sb.append("---------------------------------\n");

        // Recorremos toda la matriz
        for (int i = 0; i < MATRIZ.length; i++) { // 'i' es el Estado
            for (int j = 0; j < MATRIZ[i].length; j++) { // 'j' es el Símbolo/Columna

                Pair<Integer, AccionSemantica> cell = MATRIZ[i][j];

                // Solo actuamos si la celda tiene contenido (no es nula)
                if (cell != null) {

                    // Formateamos el contenido de la celda como antes
                    Integer nextState = cell.getFirst();
                    AccionSemantica action = cell.getSecond();
                    String cellStr;
                    String stateStr = String.valueOf(nextState);

                    if (action == null) {
                        cellStr = stateStr;
                    } else {
                        cellStr = stateStr + "/" + action.getClass().getSimpleName();
                    }

                    // Creamos la línea con el formato [Estado, Simbolo] -> Contenido
                    sb.append(String.format("[%d, %d] -> %s\n", i, j, cellStr));
                }
            }
        }
        return sb.toString();
    }
}