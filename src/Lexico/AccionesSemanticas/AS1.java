package Lexico.AccionesSemanticas;
//import Tools.Logger;
import Tools.ProgramReader;
import Tools.Pair;
public class AS1 implements AccionSemantica {
    private String acc;
    /*
     * ACCION SEMANTICA 1
     */

    public AS1(String acc) {
        this.acc = acc;
    }

    @Override
    public Pair<String, Integer> run(char simbolo, ProgramReader reader) {
        //Logger.log("AS1");
        return new Pair<String, Integer>("Identificador", 1);
    }

}
