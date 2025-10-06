package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Lexico.MatrizTransicion;
import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

public class ASError extends AccionSemantica{

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        int aux = AnalizadorLexico.ESTADO_ANTERIOR;
        System.out.println(aux);
        switch (aux) {
            case 1: Logger.logError(cursor.getCurrentLine(), "Debe ingresar un digito, un punto o una I");
            case 2: Logger.logError(cursor.getCurrentLine(), "Debe ingresar algun digito");
            case 4: Logger.logError(cursor.getCurrentLine(), "Debe ingresar simbolo + o -");
            case 5: Logger.logError(cursor.getCurrentLine(), "Debe ingresar algun digito");
            case 10: Logger.logError(cursor.getCurrentLine(), "Debe ingresar un #");
            case 15: Logger.logError(cursor.getCurrentLine(), "Debe ingresar un =");
            default : {aux = 0;}
        }
        BUFFER.setLength(0);
        return null;
    }

}
