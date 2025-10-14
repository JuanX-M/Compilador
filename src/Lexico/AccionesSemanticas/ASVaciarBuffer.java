package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

public class ASVaciarBuffer extends AccionSemantica{

    public ASVaciarBuffer() {
        super(13);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.setLength(0);
        return new Pair<>(null,0);
    }
}
