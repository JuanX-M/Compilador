package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Pair;
import Tools.LectorArchivo;
import java.util.ArrayList;

public class ASEntregarPalabras extends AccionSemantica{

    private ArrayList<String> palabrasReservadas;

    public ASEntregarPalabras() {
        super(12);
        this.palabrasReservadas = transformarArraylist();

    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {

        String aux = BUFFER.toString();
        cursor.gobackCharacter();
        BUFFER.setLength(0);
        ArrayList<String> auxPalabrasReservadas = new ArrayList<>(transformarArraylist());
        try {
            if (auxPalabrasReservadas.contains(aux))
                return new Pair<String, Integer>(aux, -1); //TODO:Corregir salida
            else
                throw new IllegalArgumentException("Palabra no existe");
        }
        catch (IllegalArgumentException e) {
            System.out.println("No existe la palabra reservada: " + e.getMessage()); //TODO: Logger
        }
        return null;
    }

    private ArrayList<String> transformarArraylist(){ //convierte el doble array list de caracter a un array list de String
        ArrayList<String> salida = new ArrayList<>();
        ArrayList<ArrayList<Character>> palabrasReservadasAux = LectorArchivo.read("palabrasReservadas.txt", "data");
        for (int i=0;i<=palabrasReservadasAux.size();i++){
            StringBuilder stringAux = new StringBuilder();
            for (Character c : palabrasReservadasAux.get(i)){
                stringAux.append(c);
            }
            salida.add(stringAux.toString());
        }
        return salida;
    }
}
