package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;

public class ASBorrarComentariosML extends AccionSemantica{

    public ASBorrarComentariosML() {
        super(10);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.setLength(0);
        return new Pair<>(null, null); //no es simplemente null porque si fuera null es porque hay un error
    }
}
