package Tools;

/**
 * Representa una entrada en la Tabla de Símbolos.
 * Almacena los atributos asociados de un lexema, como su tipo de token y nroLinea.
 */
public class Info {

    //TODO: Verificar atributos privados de Info
    private final String lexema;
    private final String nombre; // ej: "ID", "CTE_INT"
    private final String token;
    private final String tipo;
    private final String uso;
    private final String ambito;

    public Info(String nombre, String token, String tipo, String ambito) {
        this.lexema = nombre + "." + tipo + "." + ambito;
        this.nombre = nombre;
        this.token = token; // creo que se calcula por lexico, cambiar esto
        this.tipo = tipo;
        this.uso = null;
        this.ambito = ambito;
    }

    public Info(String nombre, String token, String tipo, String uso, String ambito) {
        this.lexema = nombre + "." + tipo + "." + ambito;
        this.nombre = nombre;
        this.token = token;
        this.tipo = tipo;
        this.uso = uso;
        this.ambito = ambito;
    }

    @Override
    public String toString() {
        return "Info{ " +
                "Lexema : " + lexema + " " +
                ", Token : " + token + " " +
                ", Tipo : " + tipo + " " +
                ", Uso : " + uso + " " +
                ", Ambito : " + ambito + " " +
                "}";
    }

    public String getLexema() {
        return lexema;
    }
    public String getToken() {
        return token;
    }
}