package Lexico;
import java.util.ArrayList;

import Lexico.AccionesSemanticas.AS1;
import Lexico.AccionesSemanticas.AccionSemantica;
import Tools.BinaryFileReader;
//import Tools.Logger;
import Tools.ProgramReader;
import java.util.stream.Collectors;

public class AnalizadorLexico {

    private final int ESTADOS = 17; //filas
    private final int SIMBOLOS = 27; //columnas


    public static int estado_error = 0;
    MatrizTransicion matrizTransicion;


    public AnalizadorLexico() {
        cargarMatriz();
        System.out.println(this.matrizTransicion.toString());
    }

    public void cargarMatriz() {
        this.matrizTransicion = new MatrizTransicion();

        ArrayList<ArrayList<Character>> data = BinaryFileReader.read("MatrizTransiciones.txt", "data");
        int e0 = -1;
        int e1 = -1;
        int e2 = -1;
        String acc = null;

        for (ArrayList<Character> l : data) {
            String linea[] = l.stream().map(Object::toString).collect(Collectors.joining("")).split("\\s*;\\s*");

            try {
                e0 = Integer.parseInt(linea[0]);
                e1 = Integer.parseInt(linea[1]);
                e2 = Integer.parseInt(linea[2]);
                acc = linea[3];

            } catch (NumberFormatException e) {
            }

            this.matrizTransicion.addTransicion(e0,e1,e2,toAccionSemantica(acc));
        }

    }
    private AccionSemantica toAccionSemantica(String acc) {
        acc = acc.trim();
        if (acc.equals("null") || acc.equals("-1")) {
            return null;
        }

        // Obtener la clase a partir del nombre
        AS1 as1 = new AS1(acc);

        // Crear una instancia de la clase
        return as1;
        /*} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }*/

    }

}