package Lexico.AccionesSemanticas;

import Lexico.AnalizadorLexico;
import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

public class ASError extends AccionSemantica{

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        int aux = AnalizadorLexico.ESTADO_ANTERIOR;
        switch (aux) {
            case 1:
                Logger.logError(cursor.getCurrentLine(), "Debe ingresar un digito, un punto o una I");
                break;
            case 2, 5:
                Logger.logError(cursor.getCurrentLine(), "Debe ingresar algun digito o Letra mayuscula");
                break;
            case 4:
                Logger.logError(cursor.getCurrentLine(), "Debe ingresar simbolo + o -");
                break;
            case 10:
                Logger.logError(cursor.getCurrentLine(), "Debe ingresar un #");
                break;
            case 15:
                Logger.logError(cursor.getCurrentLine(), "Debe ingresar un =");
                break;
            default :
                break;
        }
        BUFFER.setLength(0);
        return null;
    }
}
