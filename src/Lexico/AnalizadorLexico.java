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

    public Pair<String, Integer> generarToken() {
        int estado = 0;
        AccionSemantica as;
        Pair<String, Integer> token = new Pair<>(null, null);
        while (!cursor.hasFinished() && !matrizTransicion.isEstadoFinal(estado)) {
            char caracter = cursor.getCharacter();
            as = matrizTransicion.getAccionSemantica(estado, caracter);
            ESTADO_ANTERIOR = estado;
            estado = matrizTransicion.getEstado(estado, caracter);
            boolean reiniciar = false;
            if (estado == -1) {
                new ASError().run(caracter, cursor);
                estado = 0;
                reiniciar = true;
            }
            else if (estado == 28) {
                Logger.logWarning(cursor.getCurrentLine(),
                        "Símbolo '" + cursor.getCharacter() + "' inválido, descartado.");
                new ASVaciarBuffer().run(caracter, cursor);
                estado = 0;
                reiniciar = true;
            }
            else if (as != null) {
                Pair<String, Integer> posibleToken = as.run(caracter, cursor);
                if (posibleToken == null) {
                    Logger.logWarning(cursor.getCurrentLine(),
                            "Token inválido descartado. Reiniciando análisis léxico...");
                    new ASVaciarBuffer().run(caracter, cursor);
                    estado = 0;
                    reiniciar = true;
                } else {
                    token = posibleToken;
                }
            }
            if (reiniciar) {
                new ASVaciarBuffer().run(caracter, cursor);
                cursor.next();
                reiniciar = false;
            }
            else {
                if (token != null && token.getSecond() != null && estado == 17) {
                    cursor.next();
                    return token;
                }
                cursor.next();
            }
        }
        if (cursor.hasFinished()) {
            new ASVaciarBuffer().run(' ', cursor);
            return new Pair<>("EOF", 0);
        }
        new ASVaciarBuffer().run(' ', cursor);
        return generarToken();
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