package Tools;

import java.util.ArrayList;

public class Cursor {
    private final ArrayList<ArrayList<Character>> PROGRAM;
    private int currentLine;
    private int currentColumn;

    public Cursor(String programa) {
        LectorArchivo lectorArchivoAux = new LectorArchivo(programa, "data");
        this.PROGRAM = lectorArchivoAux.read();
        this.currentLine = 0; //numero de linea arranca en 1 TODO:Preguntar a Marcela
        this.currentColumn = 0;
    }

    public Character getCharacter() {
        if (hasFinished()) {
            return null; // Evita IndexOutOfBounds
        }
        return PROGRAM.get(currentLine).get(currentColumn);
    }

    public void next() {
        if (currentLine < PROGRAM.size() && currentColumn < PROGRAM.get(currentLine).size() - 1) { // Avanzamos a la siguiente columna en la misma línea
            currentColumn++;
        } else if (!hasFinished()) { // Avanzamos a la siguiente línea
            currentLine++;
            currentColumn = 0;
        }
    }

    public void gobackCharacter() {
        // Caso 1: Podemos retroceder dentro de la misma línea.
        if (currentColumn > 0) {
            currentColumn--; // Es lo mismo que currentColumn = currentColumn - 1;
        }
        // Caso 2: Estamos en la primera columna (0), pero no en la primera línea.
        else if (currentLine > 0) {
            // Nos movemos a la línea anterior.
            currentLine--;
            // Y posicionamos el cursor en la última columna de esa nueva línea.
            currentColumn = getUltimaPosicionLinea(currentLine);
        }
        // Caso 3 (implícito): Estamos en la primera línea y primera columna (currentLine = 0 y currentColumn = 0).
        // En este caso, no hacemos nada, porque no se puede retroceder más.
    }

    private int getUltimaPosicionLinea(int linea){
        return PROGRAM.get(linea).size()-1;
    }

    public boolean hasFinished() {
        // Si el número de línea actual es igual o mayor que el total de líneas,
        // significa que ya hemos procesado la última línea y hemos terminado.
        return currentLine >= PROGRAM.size() ||
                (currentLine == PROGRAM.size() - 1 && currentColumn >= PROGRAM.get(currentLine).size());
    }


    public int getCurrentLine() {
        return currentLine + 1;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }
}