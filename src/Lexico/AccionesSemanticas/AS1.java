package Lexico.AccionesSemanticas;
//import Tools.Logger;
import Tools.Cursor;
import Tools.Pair;
public class AS1 extends AccionSemantica {
    //private String acc;
    /*
     * ACCION SEMANTICA 1
     */

    public AS1() {

    }

    @Override
    public Pair<Object, Object> run(Character simbolo,Cursor cursor) {
            buffer.addCharacter(simbolo);
        return new Pair<>(null, null);
    }

}
