package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarCad1Linea extends AccionSemantica{
    public ASEntregarCad1Linea(){
        super(7);
    }
    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        BUFFER.append(simbolo);
        String aux = BUFFER.toString();
        BUFFER.setLength(0);
        System.out.println("aux " + aux );
        try {
            if (aux.contains("\r") || aux.contains("\n")){
                System.out.println("entra aca");
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException i){
            Logger.logError(cursor.getCurrentLine(), "Cadena contiene salto de linea");
            return null;
        }
        System.out.println("entra");
        return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get("STRING"));


    }
}
