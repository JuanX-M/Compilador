package Lexico.AccionesSemanticas;

import Tools.Pair;
import Tools.Cursor;
import Tools.Buffer;
public abstract class AccionSemantica {

    public static final Buffer buffer = new Buffer();

    public AccionSemantica() {
    }

    public abstract Pair<String, Integer> run(Character simbolo,Cursor cursor);
}
