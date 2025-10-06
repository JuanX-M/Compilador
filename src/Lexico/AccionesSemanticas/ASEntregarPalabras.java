package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarPalabras extends AccionSemantica{

    //private ArrayList<String> palabrasReservadas;

    public ASEntregarPalabras() {
        super(12);
        //this.palabrasReservadas = transformarArraylist();
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {

        String aux = BUFFER.toString();
        cursor.gobackCharacter();
        BUFFER.setLength(0);
        //ArrayList<String> auxPalabrasReservadas = new ArrayList<>(transformarArraylist());

        try {

            if (TABLA_PALABRAS_RESERVADAS.containsKey(aux)) {
                return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get(aux));
            } else {
                throw new IllegalArgumentException("Palabra reservada '" + aux + "' no existe");
            }
        }
        catch (IllegalArgumentException e) {
            Logger.logError(cursor.getCurrentLine(), e.getMessage());
        }
        return null;
    }

    /*private ArrayList<String> transformarArraylist(){ //convierte el doble array list de caracter a un array list de String
        ArrayList<String> salida = new ArrayList<>();
        LectorArchivo lectorArchivoAux = new LectorArchivo("palabrasReservadas.txt", "data");
        ArrayList<ArrayList<Character>> palabrasReservadasAux = lectorArchivoAux.read();
        for (int i=0;i<palabrasReservadasAux.size();i++){
            StringBuilder stringAux = new StringBuilder();
            for (Character c : palabrasReservadasAux.get(i)){
                stringAux.append(c);
            }
            salida.add(stringAux.toString());
        }
        return salida;
    }*/
}
