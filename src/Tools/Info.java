package Tools;

/**
 * Representa una entrada en la Tabla de Símbolos.
 * Almacena los atributos asociados de un lexema, como su tipo de token y nroLinea.
 */
public class Info {

    //TODO: Verificar atributos privados de Info
    private String lexema;
    private String nombre; // ej: "ID", "CTE_INT"
    private Integer token;
    private String tipo;
    private String uso;

    public Info(String nombre, Integer token) {
        this.nombre = nombre;
        this.token = token; // creo que se calcula por lexico, cambiar esto
        this.tipo = null;
        this.uso = null;
    }

    public Info(String lexema, Integer token, String tipo, String uso) {
        this.lexema = lexema;
        this.token = token; // creo que se calcula por lexico, cambiar esto
        this.tipo = tipo;
        this.uso = uso;
    }

    @Override
    public String toString() {
        return "Info{ " +
                "Lexema : " + lexema + " " +
                ", Token : " + token +
                "}";
    }

    public String getLexema() {
        return lexema;
    }
    public Integer getToken() {
        return token;
    }
}