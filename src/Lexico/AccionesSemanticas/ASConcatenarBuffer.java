package Lexico.AccionesSemanticas;
//import Tools.Logger;
import Tools.Cursor;
import Tools.Pair;
public class ASConcatenarBuffer extends AccionSemantica {

    // TODO: fijarse la diferencia con AS2

    private String nombre = "AS1";

    public String getNombre() {
        return nombre;
    }

    @Override
    public Pair<String, Integer> run(Character simbolo,Cursor cursor) {
        buffer.append(simbolo);
        return null;
    }

}
