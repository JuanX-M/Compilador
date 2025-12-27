package Tools;
import java.util.HashMap;
import java.util.Map;

// Clase que implementa la Tabla de Símbolos
public class TablaSimbolos {

    public static final HashMap<String, Info> TABLA_SIMBOLOS = new HashMap<>();

    public static void addSimboloTabla(String lexema, Info i) {
        TABLA_SIMBOLOS.putIfAbsent(lexema, i);
    }

    public static Info getSimbolo(String lexema) {
        return TABLA_SIMBOLOS.get(lexema);
    }

    public static boolean containsSymbol(String lexema) {
        return TABLA_SIMBOLOS.containsKey(lexema);
    }

    public static void printTabla() {
        System.out.println("------ Contenido de la Tabla de Símbolos ------");
        for (Map.Entry<String, Info> entry : TABLA_SIMBOLOS.entrySet()) {
            System.out.println("Clave acceso: '" + entry.getKey() + "' -> " + entry.getValue().toString());
        }
        System.out.println("---------------------------------------------");
    }
}