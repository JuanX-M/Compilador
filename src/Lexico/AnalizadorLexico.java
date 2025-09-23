package Lexico;
import java.util.ArrayList;

import Lexico.AccionesSemanticas.ASBorrarComentariosML;
import Lexico.AccionesSemanticas.AccionSemantica;
import Tools.LectorArchivo;
//import Tools.Logger;
import Tools.Cursor;
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

        ArrayList<ArrayList<Character>> data = LectorArchivo.read("MatrizTransiciones.txt", "data");
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

            this.matrizTransicion.addTransicion(e0, e1, e2, toAccionSemantica(acc));
        }

    }
    private AccionSemantica toAccionSemantica(Integer acc) {
        if (acc.equals("null") || acc.equals("-1")) {
            return null;
        }

        // Obtener la clase a partir del nombre
        AccionSemantica as1 = new ASBorrarComentariosML();

        // Crear una instancia de la clase
        return as1;
        /*} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //TODO: Definir funcion yylex()
    /*
        yylex() debe retornar el siguiente token del programa fuente y devolverlo al sintactico
        por lo tanto para mi hay que hacer lo siguiente:
            - leer caracter con el Cursor cursor.character()
            - buscar en la matriz de transicion el estado siguiente con matrizTransicion.nextEstado(estadoActual, caracter)
            - si el estado siguiente es un estado final
                - ejecutar la accion semantica asociada a la transicion
                - devolver el token al sintactico, puntero del lexema a la tabla de simbolos con yylval
                - reiniciar el estado actual al estado inicial
            - si el estado siguiente es un estado de error
                - manejar el error (imprimir mensaje, etc)??
            - si el estado siguiente es un estado intermedio
                - seguir leyendo caracteres

    */

}