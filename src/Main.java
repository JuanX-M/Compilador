import Lexico.AnalizadorLexico;
import Tools.TablaPalabrasReservadas;

public class Main {
    public static void main(String[] args) {
        String programa = "testing.txt";
        TablaPalabrasReservadas tablaPalabrasReservadas = new TablaPalabrasReservadas();
        tablaPalabrasReservadas.cargarTabla();
        AnalizadorLexico analizadorLexico = new AnalizadorLexico(programa);

        //System.out.println(analizadorLexico.generarToken());
        //System.out.println(analizadorLexico.generarToken());
        System.out.println(analizadorLexico.getTodosLosTokens());
    }
}