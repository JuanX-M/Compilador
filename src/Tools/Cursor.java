package Tools;

import java.util.ArrayList;

public class Cursor {
    private ArrayList<ArrayList<Character>> program;
    private int currentLine;
    private int currentColumn;

    public Cursor(String p) {
        this.program = LectorArchivo.read(p, "sample_programs");
        this.currentLine = 0;
        this.currentColumn = 0;
    }

    public Character getCharacter() {
        return program.get(currentLine).get(currentColumn);

    }

    public boolean isNextEndProgram() {
        return (currentColumn + 1 == program.get(currentLine).size())
                ? (currentLine + 1 == program.size() ? true : false)
                : false;
    }

    public void next() {
        if (currentLine < program.size() && currentColumn < program.get(currentLine).size() - 1) {
            // Avanzamos a la siguiente columna en la misma línea
            currentColumn++;
        } else if (!hasFinished()) {
            // Avanzamos a la siguiente línea
            currentLine++;
            // while (program.get(currentLine).size() == 0 && !hasFinished()) {
            // currentLine++;
            // }
            currentColumn = 0;
        }
    }

    public String programToString() {
        String out = "";
        int linea = 1;

        for (ArrayList<Character> l : this.program) {
            out += "[" + linea + "]: ";
            for (Character c : l) {
                if (c != '\n')
                    out += c;
            }
            out += "\n";
            linea++;
        }

        return out;
    }

    public boolean hasProgram() {
        return this.program != null;
    }

    public void nextCol() {
        this.currentColumn++;
    }

    public void gobackCharacter() {
        currentColumn = currentColumn - 1;
    }

    public boolean hasFinished() {
        if (currentLine == program.size()) { // si estoy en la ultima linea de codigo
            return true;
        }
        return false;
    }

    public int getCurrentLine() {
        return currentLine + 1;
    }

    public int getCurrentColumn() {
        return currentColumn + 1;
    }

}