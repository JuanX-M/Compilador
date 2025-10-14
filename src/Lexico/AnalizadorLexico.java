package Lexico;
import java.util.ArrayList;
import Lexico.AccionesSemanticas.*;
import Tools.LectorArchivo;
import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

import java.util.stream.Collectors;

public class AnalizadorLexico {

    //private final int ESTADOS = 17; //filas
    //private final int SIMBOLOS = 28; //columnas

    public static int ESTADO_ANTERIOR = 0;
    private MatrizTransicion matrizTransicion;
    private final Cursor cursor; //es final?

    public AnalizadorLexico(String programa) {
        cargarMatriz();
        cursor = new Cursor(programa);
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void cargarMatriz() {
        this.matrizTransicion = new MatrizTransicion();
        LectorArchivo lectorArchivo = new LectorArchivo("MatrizTransiciones.txt", "data");
        ArrayList<ArrayList<Character>> data = lectorArchivo.read();
        int e0 = -1; // estado actual
        int e1 = -1; // simbolo
        int e2 = -1; //siguiente estado
        int acc = -1; //numero de accion semantica


        for (ArrayList<Character> l : data) {
            String[] linea = l.stream().map(Object::toString).collect(Collectors.joining("")).split("\\s*;\\s*");

            try {
                e0 = Integer.parseInt(linea[0]);
                e1 = Integer.parseInt(linea[1]);
                e2 = Integer.parseInt(linea[2]);
                acc = Integer.parseInt(linea[3].trim());

            } catch (NumberFormatException e) {
                System.out.println("Error en cargar matriz" + e.getMessage()); //TODO:Salida por error de formato
            }
            this.matrizTransicion.addTransicion(e0, e1, e2, toAccionSemantica(acc));
        }
    }

    private AccionSemantica toAccionSemantica(Integer acc) {
        return switch (acc) { //crea la instancia de clase de accion semantica
            case 1 -> new ASConcatenarBuffer();
            case 3 -> new ASEntregarEntero();
            case 4 -> new ASEntregarFlotante();
            case 5 -> new ASEntregarID();
            case 6 -> new ASEntregarLiterales();
            case 7 -> new ASEntregarCad1Linea();
            case 8 -> new ASEntregarComparadores();
            case 10 -> new ASBorrarComentariosML();
            case 11 -> new ASEntregarAsignacion();
            case 12 -> new ASEntregarPalabras();
            default -> null;
        };
    }

public Pair<String, Integer> generarToken(){
    //TODO:generarToken() debe entregar Nro de Token solamente, info de lexema por yylval que es un objeto ParselVal
    int estado=0;
    AccionSemantica as;
    Pair<String, Integer> token;
    while (!cursor.hasFinished() &&  !matrizTransicion.isEstadoFinal(estado)){
        char caracter = cursor.getCharacter();
        as = matrizTransicion.getAccionSemantica(estado, caracter);
        ESTADO_ANTERIOR = estado;
        estado = matrizTransicion.getEstado(estado, caracter);
        System.out.println("Estado: " + estado);
        //System.out.println("Accion:  " + as + "  Estado:  " + estado);
        if (estado == -1){
            System.out.println("Entra");
            AccionSemantica auxAccion = new ASError();
            auxAccion.run(caracter, cursor);
            estado = 0;
        }
        else if (estado == 28){
            Logger.logWarning(cursor.getCurrentLine(), "Simbolo '" + cursor.getCharacter() + "' invalido, borrado para continuar con compilacion");
            estado = 0;
        }
        else
            if (as != null){ // si hay alguna accion semantica a ejecutar, la ejecuto
                token = as.run(caracter, cursor);
                if (token == null){
                    //TODO:Logger error de Accion Semantica
                    cursor.next();
                    return null;
                }
                if (token.getSecond() != null && estado ==17){ // MIRAR ACCIONES SEMNATICAS PARA ENTENDER EJEMPLOS DE RETURN DE LITERALES POR EJEMPLO
                    cursor.next();
                    return token;
                }
        }
        cursor.next();
    }
    if (cursor.hasFinished())
        return new Pair<>("Fin de programa", 0);

    return null;
}

    public ArrayList<Pair<String,Integer>> getTodosLosTokens() {
        ArrayList<Pair<String,Integer>> salida = new ArrayList<>();
        while (!cursor.hasFinished()) {
            Pair<String,Integer> token = generarToken();
            if (token != null) {
                salida.add(token);
            }
        }
        return salida;
    }


}