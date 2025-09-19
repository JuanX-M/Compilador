package Tools;


import java.util.HashMap;
import java.util.Map;

// Clase que implementa la Tabla de Símbolos
public class TablaSimbolos {

    //TODO: Consultar si les parece bien planteada la Tabla
    private HashMap<String, Info> Tabla;

    public TablaSimbolos() {
        this.Tabla = new HashMap<>();
    }

    public void addSimboloTabla(String lexema, Info i) {
        Tabla.putIfAbsent(lexema, i);
    }


    public Info getSimbolo(String lexema) {
        return Tabla.get(lexema);
    }

    public boolean containsSymbol(String lexema) {
        return Tabla.containsKey(lexema);
    }

    public void printTabla() {
        System.out.println("------ Contenido de la Tabla de Símbolos ------");
        for (Map.Entry<String, Info> entry : Tabla.entrySet()) {
            System.out.println("Lexema: '" + entry.getKey() + "' -> " + entry.getValue().toString());
        }
        System.out.println("---------------------------------------------");
    }
}