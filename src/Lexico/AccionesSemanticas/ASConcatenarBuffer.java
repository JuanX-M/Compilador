package Lexico.AccionesSemanticas;
//import Tools.Logger;
import Tools.Cursor;
import Tools.Pair;
public class ASConcatenarBuffer extends AccionSemantica {

    // TODO: fijarse la diferencia con AS2


    public ASConcatenarBuffer() {
        super(1);
    }

    @Override
    public Pair<String, Integer> run(Character simbolo,Cursor cursor) {
        BUFFER.append(simbolo);
        return null;
    }

}
