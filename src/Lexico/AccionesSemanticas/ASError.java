package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

public class ASError extends AccionSemantica{

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.setLength(0);
        System.out.println("Error lexico en linea: " + cursor.getCurrentLine());
        return null;
    }

}
