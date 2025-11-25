package Tools;

import java.util.ArrayList;

public class Cursor {
    private final ArrayList<ArrayList<Character>> PROGRAM;
    private int currentLine;
    private int currentColumn;

    public Cursor(String programa) {
        LectorArchivo lectorArchivoAux = new LectorArchivo(programa, "data");
        this.PROGRAM = lectorArchivoAux.read();
        this.currentLine = 0;
        this.currentColumn = 0;
    }

    public Character getCharacter() {
        if (hasFinished()) {
            return null; // Evita IndexOutOfBounds
        }
        return PROGRAM.get(currentLine).get(currentColumn);
    }

    public void next() {
        if (currentLine < PROGRAM.size() && (currentColumn < (PROGRAM.get(currentLine).size() - 1))) {  // Avanzamos a la siguiente columna en la misma línea
            currentColumn++;
        }
        else if (!hasFinished()) { // Avanzamos a la siguiente línea
            currentLine++;
            currentColumn = 0;
        }
    }

    public void gobackCharacter() {
        // Caso 1: Podemos retroceder dentro de la misma línea.
        if (currentColumn > 0)
            currentColumn--; // Es lo mismo que currentColumn = currentColumn - 1;
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
        return ((currentLine >= PROGRAM.size()) ||
                (currentLine == PROGRAM.size() - 1 && currentColumn >= PROGRAM.get(currentLine).size()));
    }

    public boolean hasFinishedLine(){
        return (currentColumn < (PROGRAM.get(currentLine).size() - 1));
    }

    /*public void addSalto(){
        for (ArrayList<Character> linea : PROGRAM.size()) {
            linea.add('\r');
            PROGRAM.get(linea.size)
        }
    }*/

    public int getCurrentLine() {
        return currentLine + 1;
    }

    public int getPreviousLine() {
        // Si estamos al inicio del archivo, no hay anterior
        if (currentLine == 0 && currentColumn == 0)
            return 1; // o 0, según como numeres las líneas
        // Si no estamos en la primera columna, seguimos en la misma línea
        if (currentColumn > 0)
            return currentLine + 1; // getCurrentLine() suma +1, así que mantenemos coherencia
        // Si estamos en la primera columna pero no en la primera línea,
        // el carácter anterior pertenece a la línea anterior
        return currentLine; // línea anterior (ya que currentLine está 0-based)
    }


    public int getCurrentColumn() {
        return currentColumn;
    }

}