package Lexico.AccionesSemanticas;

import Sintactico.Parser;
import Tools.Cursor;
import Tools.Pair;

public class ASEntregarCad1Linea extends AccionSemantica{
    public ASEntregarCad1Linea(){
        super(7);
    }
    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.append(simbolo);
        String aux = BUFFER.toString();
        BUFFER.setLength(0);
        /*if(aux.contains("\n")||aux.contains("\r")){
            System.out.println("error salto de linea");
            return null;
        }*/
        return new Pair<>(aux, Parser.STRING);
    }
}
