package Lexico.AccionesSemanticas;
import Sintactico.Parser;
import Tools.Pair;

import java.math.BigDecimal;

public class ASEntregarFlotante extends AccionSemantica{
    public static final Double MAX_VALUE_POS = 3.40282347E38;
    public static final Double MIN_VALUE_POS = 1.17549435E-38;
    public static final Double MAX_VALUE_NEG = -1.17549435E-38;
    public static final Double MIN_VALUE_NEG = -3.404282347E38;

    private float base;
    private int exponente;

    public ASEntregarFlotante() {
        super(4);
        base = 0;
        exponente = 0;
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Tools.Cursor cursor) {

        String aux = BUFFER.toString();
        String aDevolver = BUFFER.toString();
        if (aux.contains("F")){
            String[] parts = aux.split("F");

            base = Float.parseFloat(parts[0]);
            exponente = Integer.parseInt(parts[1]);

            BigDecimal result = BigDecimal.valueOf(base).multiply(BigDecimal.valueOf(Math.pow(10, exponente)));

            System.out.println(result);

            if ((result.signum() > 0 && result.compareTo(BigDecimal.valueOf(MIN_VALUE_POS)) < 0) ||
                    (result.signum() < 0 && result.compareTo(BigDecimal.valueOf(MAX_VALUE_NEG)) > 0)) {
                System.out.println("Error: El número flotante es demasiado pequeño");
                return null;
            }

            if ((result.signum() > 0 && result.compareTo(BigDecimal.valueOf(MAX_VALUE_POS)) > 0) ||
                    (result.signum() < 0 && result.compareTo(BigDecimal.valueOf(MIN_VALUE_NEG)) < 0)) {
                System.out.println("Error: El número flotante es demasiado grande");
                return null;
            }

            aux = aux.replace('F', 'E'); //Formateamos para meter en variable
        }
        try {
                float numero = Float.parseFloat(aux);
                if (Float.isInfinite(numero)) {
                    System.out.println("Error: El número es infinito");
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) { //TODO:Logger
                System.out.println("Excede cantidad de bits");
                return null;
        }
    BUFFER.setLength(0);
    cursor.gobackCharacter();
    // retornamons el numero y el token float
    return new Pair<String, Integer>(aDevolver, Parser.CTE_FLOAT);
    }
}
