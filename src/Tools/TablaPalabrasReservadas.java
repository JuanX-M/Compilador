package Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TablaPalabrasReservadas {

    public static final HashMap<String, Integer> TABLA_PALABRAS_RESERVADAS = new HashMap<>();



    public void cargarTabla(){
        LectorArchivo lectorArchivo = new LectorArchivo("palabrasReservadas.txt", "data");
        lectorArchivo.read();
        ArrayList<ArrayList<Character>> data = lectorArchivo.read();
        String e0 = null; // lexema
        int e1 = -1;      // nro de token

        for (ArrayList<Character> l : data) {
            String linea[] = l.stream().map(Object::toString).collect(Collectors.joining("")).split("\\s*;\\s*");
            //System.out.println(linea);

            try {
                e0 = linea[0];

                e1 = Integer.parseInt(linea[1]);
                //System.out.println("  " + linea[0] + " " + linea[1] );
                //System.out.println(linea[1]);
                //System.out.println(Integer.parseInt(linea[2]));
                TABLA_PALABRAS_RESERVADAS.put(e0,e1);
            } catch (NumberFormatException e) {
                System.out.println("Error en TablaPalabrasReservadas" + e.getMessage()); //TODO:Salida por error de formato
            }

        }
    }

}
