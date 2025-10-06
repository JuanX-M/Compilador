import Lexico.AnalizadorLexico;
import Tools.Logger;
import Tools.TablaPalabrasReservadas;
import Tools.TablaSimbolos;

public class Main {
    public static void main(String[] args) {
        String programa = "samplePrograms/testing.txt";
        TablaPalabrasReservadas tablaPalabrasReservadas = new TablaPalabrasReservadas();
        tablaPalabrasReservadas.cargarTabla();
        AnalizadorLexico analizadorLexico = new AnalizadorLexico(programa);

        //System.out.println(analizadorLexico.generarToken());
        //System.out.println(analizadorLexico.generarToken());
        System.out.println(analizadorLexico.getTodosLosTokens());
        System.out.println("TablaSimbolos: " + TablaSimbolos.TABLA_SIMBOLOS);
        System.out.println(Logger.generateLog());

    }
}