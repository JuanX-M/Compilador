package Lexico.AccionesSemanticas;

import Tools.Pair;
import Tools.Cursor;
import Tools.Buffer;

public abstract class AccionSemantica {

    public static final StringBuilder buffer = new StringBuilder();

    public AccionSemantica() {
    }

    public abstract Pair<String, Integer> run(Character simbolo,Cursor cursor);

}
