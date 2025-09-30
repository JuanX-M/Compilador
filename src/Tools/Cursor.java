package Tools;

import java.util.ArrayList;

public class Cursor {
    private final ArrayList<ArrayList<Character>> PROGRAM;
    private int currentLine;
    private int currentColumn;

    public Cursor(String programa) {
        LectorArchivo lectorArchivoAux = new LectorArchivo(programa, "sample_programs");
        this.PROGRAM = lectorArchivoAux.read();
        this.currentLine = 1; //numero de linea arranca en 1 TODO:Preguntar a Marcela
        this.currentColumn = 0;
    }

    public Character getCharacter() {
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
        if (currentColumn > 1)
            currentColumn = currentColumn - 1;
        else {
            currentLine = getCurrentLine() - 1;
            currentColumn = getUltimaPosicionLinea(getCurrentLine());
        }
    }

    private int getUltimaPosicionLinea(int linea){
        return PROGRAM.get(linea).size();
    }

    public boolean hasFinished() {
        return currentLine == PROGRAM.size(); // si estoy en la ultima linea de codigo
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }
}