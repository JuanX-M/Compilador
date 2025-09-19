package Lexico.AccionesSemanticas;

import Tools.Pair;
import Tools.ProgramReader;

public interface AccionSemantica {

    public Pair<String, Integer> run(char simbolo, ProgramReader reader);
}
