package Lexico.AccionesSemanticas;
import Tools.Pair;

import java.math.BigDecimal;

public class ASEntregarFlotante extends AccionSemantica{
    public static final Double MAX_VALUE_POS = 3.4E38;
    public static final Double MIN_VALUE_POS = 1.7E38;
    public static final Double MAX_VALUE_NEG = -1.7E38;
    public static final Double MIN_VALUE_NEG = -3.4E38;
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

            BigDecimal result = BigDecimal.valueOf(base).multiply(BigDecimal.TEN.pow(exponente));
            //compareTo devuelve 0,1,-1 si es igual, mayor o menor
            if (result.compareTo(BigDecimal.valueOf(MIN_VALUE_POS)) < 0 || result.compareTo(BigDecimal.valueOf(MAX_VALUE_NEG)) > 0) {
                System.out.println("Error: El número flotante es demasiado pequeño");
                return new Pair<>(null, -1); //TODO:Corregir
            }
            if (result.compareTo(BigDecimal.valueOf(MAX_VALUE_POS)) > 0 || result.compareTo(BigDecimal.valueOf(MIN_VALUE_NEG)) < 0) {
                System.out.println("Error: El número flotante es demasiado grande");
                return new Pair<>(null, -1); //TODO:Corregir
            }
            aux.replace('F', 'E'); //Formateamos para meter en variable
        }
        try {
                float numero = Float.parseFloat(aux);
            } catch (NumberFormatException e) {
                System.out.println("Excede cantidad de bits");
        }
    BUFFER.setLength(0);
    cursor.gobackCharacter();
    // retornamons el numero y el token float
    return new Pair<String, Integer>(aDevolver, Parser.float);
    }
}
