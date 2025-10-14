package Lexico.AccionesSemanticas;

import Tools.Pair;
import Tools.Cursor;

public abstract class AccionSemantica {
    private int nombre;

    public int getNumeroAccionSemantica() {
        return nombre;
    }

    public AccionSemantica(int nombre) {
        this.nombre = nombre;
    }

    public static final StringBuilder BUFFER = new StringBuilder();

    public AccionSemantica() {}

    public abstract Pair<String, Integer> run(Character simbolo,Cursor cursor);

}
