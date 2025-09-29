package Lexico;
import java.util.ArrayList;

import Lexico.AccionesSemanticas.*;
import Tools.LectorArchivo;
import Tools.Cursor;
import java.util.stream.Collectors;

public class AnalizadorLexico {

    private final int ESTADOS = 17; //filas
    private final int SIMBOLOS = 28; //columnas

    public static int estado_error = 0;
    private MatrizTransicion matrizTransicion;
    private Cursor cursor;

    public AnalizadorLexico() {
        cargarMatriz();
        cursor = new Cursor();
    }

    public static final LectorArchivo //TODO: crear un objeto para el programa fuente, va a haber otro para la matriz. es static porque se tiene que poder usar el read en el cursor

    public void cargarMatriz() {
        this.matrizTransicion = new MatrizTransicion();
        LectorArchivo lectorArchivo = new LectorArchivo("MatrizTransiciones.txt", "data");
        ArrayList<ArrayList<Character>> data = lectorArchivo.read();
        int e0 = -1; // estado actual
        int e1 = -1; // simbolo
        int e2 = -1; //siguiente estado
        int acc = -1; //numero de accion semantica

        for (ArrayList<Character> l : data) {
            String linea[] = l.stream().map(Object::toString).collect(Collectors.joining("")).split("\\s*;\\s*");

            try {
                e0 = Integer.parseInt(linea[0]);
                e1 = Integer.parseInt(linea[1]);
                e2 = Integer.parseInt(linea[2]);
                acc = Integer.parseInt(linea[3]);

            } catch (NumberFormatException e) {
                System.out.println(e.getMessage()); //TODO:Salida por error de formato
            }
            this.matrizTransicion.addTransicion(e0, e1, e2, toAccionSemantica(acc));
        }
    }

    private AccionSemantica toAccionSemantica(Integer acc) {
        switch (acc) { //crea la instancia de clase de accion semantica
            case 1 : return new ASConcatenarBuffer();
            case 3 : return new ASEntregarEntero();
            case 4 : return new ASEntregarFlotante();
            case 5 : return new ASEntregarID();
            case 6 : return new ASEntregarLiterales();
            case 7 : return new ASEntregarCad1Linea();
            case 8 : return new ASEntregarComparadores();
            case 10 : return new ASBorrarComentariosML();
            case 11 : return new ASEntregarAsignacion();
            case 12 : return new ASEntregarPalabras();
            default : return null;
        }
    }

}