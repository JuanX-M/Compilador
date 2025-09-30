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
            if (auxPalabrasReservadas.contains(aux)) {
                switch (aux) {
                    case "if":
                        return new Pair<>("if", Parser. if);
                    case "else":
                        return new Pair<>("else", Parser.else);
                    case "endif":
                        return new Pair<>("endif", Parser.endif);
                    case "print":
                        return new Pair<>("print", Parser.print);
                    case "return":
                        return new Pair<>("return", Parser. return);
                    case "var":
                        return new Pair<>("var", Parser.var);
                    case "for":
                        return new Pair<>("for", Parser. for);
                    case "from":
                        return new Pair<>("from", Parser.from);
                    case "to":
                        return new Pair<>("to", Parser.to);
                    case "cr":
                        return new Pair<>("cr", Parser.cr);
                    case "se":
                        return new Pair<>("se", Parser.se);
                    case "le":
                        return new Pair<>("le", Parser.le);
                    case "toi":
                        return new Pair<>("toi", Parser.toi);
                    default:
                        // Manejar el caso en que el token no coincide con ninguno de los casos
                        System.out.println("Token no reconocido: " + aux); //TODO:Logger
                        return null;
                }
            }
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
