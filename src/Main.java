import Lexico.AnalizadorLexico;

public class Main {
    public static void main(String[] args) {
        String programa = "testing.txt";
        AnalizadorLexico analizadorLexico = new AnalizadorLexico(programa);
        //System.out.println(analizadorLexico.generarToken());
        //System.out.println(analizadorLexico.generarToken());
        System.out.println(analizadorLexico.getTodosLosTokens());
    }
}