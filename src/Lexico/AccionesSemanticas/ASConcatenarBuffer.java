package Lexico.AccionesSemanticas;
import Tools.Cursor;
import Tools.Pair;
public class ASConcatenarBuffer extends AccionSemantica {

    public ASConcatenarBuffer() {
        super(1);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.append(simbolo);
        return new Pair<>(null, null); //no es simplemente null porque si fuera null es porque hay un error
    }
}