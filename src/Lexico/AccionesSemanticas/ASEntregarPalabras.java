package Lexico.AccionesSemanticas;

import Sintactico.Parser;
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
            System.out.println(auxPalabrasReservadas.contains(aux));
            //if (auxPalabrasReservadas.contains(aux)) {
                switch (aux) {
                    case "if":
                        return new Pair<>("if", Parser.IF);
                    case "else":
                        return new Pair<>("else", Parser.ELSE);
                    case "endif":
                        return new Pair<>("endif", Parser.ENDIF);
                    case "print":
                        return new Pair<>("print", Parser.PRINT);
                    case "return":
                        return new Pair<>("return", Parser.RETURN);
                    case "var":
                        return new Pair<>("var", Parser.VAR);
                    case "for":
                        return new Pair<>("for", Parser.FOR);
                    case "from":
                        return new Pair<>("from", Parser.FROM);
                    case "to":
                        return new Pair<>("to", Parser.TO);
                    case "cr":
                        return new Pair<>("cr", Parser.CR);
                    case "se":
                        return new Pair<>("se", Parser.SE);
                    case "le":
                        return new Pair<>("le", Parser.LE);
                    case "toi":
                        return new Pair<>("toi", Parser.TOI);
                    default:
                        // Manejar el caso en que el token no coincide con ninguno de los casos
                        System.out.println("Token no reconocido: " + aux); //TODO:Logger
                        return null;
                }
            //}
            //else
            //    throw new IllegalArgumentException("Palabra no existe");
        }
        catch (IllegalArgumentException e) {
            System.out.println("No existe la palabra reservada: " + e.getMessage()); //TODO: Logger
        }
        return null;
    }

    private ArrayList<String> transformarArraylist(){ //convierte el doble array list de caracter a un array list de String
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
    }
}
