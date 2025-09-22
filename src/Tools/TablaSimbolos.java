package Tools;


import java.util.HashMap;
import java.util.Map;

// Clase que implementa la Tabla de Símbolos
public class TablaSimbolos {

    //TODO: Consultar si les parece bien planteada la Tabla
    public static final HashMap<String, Info> tablaSimbolos = null;

    public void addSimboloTabla(String lexema, Info i) {
        tablaSimbolos.putIfAbsent(lexema, i);
    }

    public Info getSimbolo(String lexema) {
        return tablaSimbolos.get(lexema);
    }

    public boolean containsSymbol(String lexema) {
        return tablaSimbolos.containsKey(lexema);
    }

    public void printTabla() {
        System.out.println("------ Contenido de la Tabla de Símbolos ------");
        for (Map.Entry<String, Info> entry : tablaSimbolos.entrySet()) {
            System.out.println("Lexema: '" + entry.getKey() + "' -> " + entry.getValue().toString());
        }
        System.out.println("---------------------------------------------");
    }
}