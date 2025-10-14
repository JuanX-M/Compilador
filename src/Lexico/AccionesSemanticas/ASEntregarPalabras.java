package Lexico.AccionesSemanticas;

import Tools.Cursor;
import Tools.Logger;
import Tools.Pair;

import static Tools.TablaPalabrasReservadas.TABLA_PALABRAS_RESERVADAS;

public class ASEntregarPalabras extends AccionSemantica{

    //private ArrayList<String> palabrasReservadas;

    public ASEntregarPalabras() {
        super(12);
        //this.palabrasReservadas = transformarArraylist();
    }

    @Override
    public Pair<String, Integer> run(Character simbolo, Cursor cursor) {
        String aux = BUFFER.toString();
        cursor.gobackCharacter();
        BUFFER.setLength(0);
        //ArrayList<String> auxPalabrasReservadas = new ArrayList<>(transformarArraylist());
        try {
            if (TABLA_PALABRAS_RESERVADAS.containsKey(aux)) {
                return new Pair<>(aux, TABLA_PALABRAS_RESERVADAS.get(aux));
            }
             else
                throw new IllegalAccessException("Palabra reservada " + aux + " no existe ni se encontro una semenjante");
            } catch (IllegalAccessException e) {
                Logger.logError(cursor.getCurrentLine(), e.getMessage());
        }
        return null;
    }

    // Estos metodos siguientes los sacamos de internet, solo se nos ocurrio la idea del Warning con la palabra mal escrita

    /*private String palabraReservadaMasParecida(String palabra) {
        int minDistancia = Integer.MAX_VALUE;
        String masParecida = null;
        for (String reservada : TABLA_PALABRAS_RESERVADAS.keySet()) {
            int distancia = distanciaLevenshtein(palabra.toLowerCase(), reservada.toLowerCase());
            if (distancia < minDistancia) {
                minDistancia = distancia;
                masParecida = reservada;
            }
        }
        // Solo sugerir si es razonablemente parecida (≤2 cambios)
        return (minDistancia <= 2) ? masParecida : null;
    }

    private int distanciaLevenshtein(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];
        for (int j = 0; j <= len2; j++)
            prev[j] = j;
        for (int i = 1; i <= len1; i++) {
            curr[0] = i;
            for (int j = 1; j <= len2; j++) {
                int costo = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                curr[j] = Math.min(
                        Math.min(prev[j] + 1, curr[j - 1] + 1),
                        prev[j - 1] + costo
                );
            }
            // Intercambio de buffers
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }
        return prev[len2];
    }*/

}


    /*private ArrayList<String> transformarArraylist(){ //convierte el doble array list de caracter a un array list de String
        ArrayList<String> salida = new ArrayList<>();
        LectorArchivo lectorArchivoAux = new LectorArchivo("palabrasReservadas.txt", "data");
        ArrayList<ArrayList<Character>> palabrasReservadasAux = lectorArchivoAux.read();
        for (int i=0;i<palabrasReservadasAux.size();i++){
            StringBuilder stringAux = new StringBuilder();
            for (Character c : palabrasReservadasAux.get(i)){
                stringAux.append(c);
            }
            salida.add(stringAux.toString());
        }
        return salida;
    }*/

