package Lexico.AccionesSemanticas;
import Tools.Logger;
import Tools.Cursor;
import Tools.Pair;
public class ASConcatenarBuffer extends AccionSemantica {

    private int estado;
    public ASConcatenarBuffer(int estado) {
        super(1);
        this.estado = estado;
    }

    public ASConcatenarBuffer() {
        super(1);
        this.estado = 0;
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.append(simbolo);

        return new Pair<>(null, null); //no es simplemente null porque si fuera null es porque hay un error
    }
}